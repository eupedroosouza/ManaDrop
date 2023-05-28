/*
 * Copyright (C) 2023 PatrickKR
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.patrick.gradle.remapper.tasks;

import io.github.patrick.gradle.remapper.tasks.util.Action;
import io.github.patrick.gradle.remapper.tasks.util.ActualProcedure;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Iterator;

public class RemapTask extends DefaultTask {

    private Property<String> version = getProject().getObjects().property(String.class);
    private Property<Action> action = getProject().getObjects().property(Action.class);
    private Property<Boolean> skip = getProject().getObjects().property(Boolean.class).convention(false);
    private Property<AbstractArchiveTask> inputTask = getProject().getObjects().property(AbstractArchiveTask.class);
    private Property<String> archiveClassifier = getProject().getObjects().property(String.class);
    private Property<String> archiveName = getProject().getObjects().property(String.class);
    private DirectoryProperty archiveDirectory = getProject().getObjects().directoryProperty();

    @TaskAction
    public void run() throws IOException {
        if(!skip.get()){
            AbstractArchiveTask task = inputTask.isPresent() ? inputTask.get() : (AbstractArchiveTask) getProject().getTasks().named("jar").get();
            File archiveFile = java.util.Optional.of(task)
                    .map(AbstractArchiveTask::getArchiveFile)
                    .map(Provider::getOrNull)
                    .map(RegularFile::getAsFile)
                    .orElseThrow(() -> new IllegalStateException("Build file not found."));

            if(this.version == null)
                throw new IllegalStateException("Version cannot be null to remap.");
            String version = this.version.get();

            File archiveDirectory = this.archiveDirectory.isPresent() ? this.archiveDirectory.getAsFile().get() : null;

            File targetFoler = archiveDirectory != null ? archiveDirectory : archiveFile.getParentFile();
            File targetFile = new File(targetFoler, archiveName.getOrElse(archiveClassifier.isPresent() ?
                    fileNameWithClassifier(task, archiveClassifier.get()) : archiveFile.getName()));

            File fromFile = archiveFile;
            File toFile = Files.createTempFile(null, ".jar").toFile();

            Action action = this.action.getOrElse(Action.MOJANG_TO_SPIGOT);
            Iterator<ActualProcedure> iterator = Arrays.stream(action.getProcedures()).iterator();
            boolean shouldRemove = false;
            while(iterator.hasNext()){
                ActualProcedure procedure = iterator.next();
                procedure.remap(getProject(), version, fromFile, toFile);

                if(shouldRemove)
                    fromFile.delete();


                if(iterator.hasNext()){
                    fromFile = toFile;
                    toFile =  Files.createTempFile(null, ".jar").toFile();
                    shouldRemove = true;
                }
            }

            Files.copy(toFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            toFile.delete();
            System.out.println("Successfully obfuscate jar (" + getProject().getName() + ", " + action + ").");
        }
    }

    private String fileNameWithClassifier(AbstractArchiveTask task, String classifier){
        return task.getArchiveBaseName().get() + "-" + task.getArchiveVersion().get() + "-" + classifier + ".jar";
    }

    @Input
    public Property<String> getVersion() {
        return version;
    }

    @Input
    @Optional
    public Property<Action> getAction() {
        return action;
    }

    @Input
    @Optional
    public Property<Boolean> getSkip() {
        return skip;
    }

    @Input
    @Optional
    public Property<AbstractArchiveTask> getInputTask() {
        return inputTask;
    }

    @Input
    @Optional
    public Property<String> getArchiveClassifier() {
        return archiveClassifier;
    }

    @Input
    @Optional
    public Property<String> getArchiveName() {
        return archiveName;
    }

    @InputDirectory
    @Optional
    public DirectoryProperty getArchiveDirectory() {
        return archiveDirectory;
    }
}
