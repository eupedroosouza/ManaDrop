package fr.il_totore.manadrop.spigot.task;

import fr.il_totore.manadrop.util.LoggerProcessBuilder;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.os.OperatingSystem;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BuildTools extends DefaultTask {

    private final List<String> allVersions = new ArrayList<>();
    public File workDir;
    public boolean refreshBuildTools = true;
    public boolean refreshVersions = false;
    public String buildToolsURL = "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar";
    public boolean stopOnError = true;
    public boolean showMavenInstallCheckLogs = false;
    public boolean showBuildToolsLogs = true;
    public boolean checkIfExistsInMavenRepository = true;
    public int maxRamAllowed = 1024;

    public List<String> arguments = new ArrayList<>();
    public String mavenLocal;

    @TaskAction
    public void run() throws IOException, InterruptedException {
        Objects.requireNonNull(workDir, "workDir cannot be null !");
        if(!workDir.exists()) workDir.mkdirs();
        File buildTools = new File(workDir, "BuildTools.jar");
        if(refreshBuildTools) buildTools.delete();
        if(!buildTools.exists()) {
            System.out.println("Downloading BuildTools...");
            HttpURLConnection connection = (HttpURLConnection) new URL(buildToolsURL).openConnection();
            connection.setDoInput(true);
            Files.copy(connection.getInputStream(), buildTools.toPath());
        }
        if(checkIfExistsInMavenRepository && mavenLocal == null){
            System.out.println("Checking maven...");

            /* Provided by gradle-maven-exec-plugin (https://github.com/dkorotych/gradle-maven-exec-plugin/tree/master)

             * Copyright 2022 Dmitry Korotych
             * <p>
             * Licensed under the Apache License, Version 2.0 (the "License");
             * you may not use this file except in compliance with the License.
             * You may obtain a copy of the License at
             * <p>
             * http://www.apache.org/licenses/LICENSE-2.0
             * <p>
             * Unless required by applicable law or agreed to in writing, software
             * distributed under the License is distributed on an "AS IS" BASIS,
             * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
             * See the License for the specific language governing permissions and
             * limitations under the License.

            */
            Project project = getProject();
            mavenLocal = (String) Optional.of(project)
                    .map(Project::getExtensions)
                    .map(ExtensionContainer::getExtraProperties)
                    .filter(properties -> properties.has("maven"))
                    .map(properties -> properties.get("maven"))
                    .orElse(null);
            if(mavenLocal == null)
                mavenLocal = Optional.ofNullable(System.getenv("MAVEN_HOME"))
                        .filter(((Predicate<String>) s -> s.trim().isEmpty()).negate())
                        .map(Paths::get)
                        .map(path -> path.resolve("bin"))
                        .map(path -> path.resolve("mvn" + (OperatingSystem.current().isWindows() ? ".cmd" : "")))
                        .map(Path::toAbsolutePath)
                        .map(Path::toString)
                        .orElseThrow(() -> new GradleException("Maven installation not found by MAVEN_HOME environment variable"));

        }
        if(mavenLocal != null)
            System.out.println("Maven: " + mavenLocal);

        for(String version : allVersions) {
            System.out.println("Checking for " + version + "...");
            if(checkIfExistsInMavenRepository){
                LoggerProcessBuilder mavenProcessBuilder = new LoggerProcessBuilder(new ProcessBuilder(mavenLocal, "dependency:get",
                            "-Dartifact=\"org.spigotmc:spigot:" + version + "-R0.1-SNAPSHOT\""),
                        showMavenInstallCheckLogs ? System.out : null,
                        showMavenInstallCheckLogs ? System.err : null);
                mavenProcessBuilder.environment().putAll(System.getenv());
                if(mavenProcessBuilder.startAndWait().exitValue() == 0 && !refreshVersions) {
                    System.out.println(version + " is already installed! Skipping...");
                    continue;
                }
            }

            System.out.println("Building " + version + "...");

            List<String> buildArguments = Stream.concat(Stream.of("java", "-Xmx" + maxRamAllowed + "M",
                    "-jar", "BuildTools.jar", "--rev", version), arguments.stream()).collect(Collectors.toList());

            LoggerProcessBuilder buildProcessBuilder = new LoggerProcessBuilder(new ProcessBuilder(buildArguments),
                    showBuildToolsLogs ? System.out : null,
                    showBuildToolsLogs ? System.err : null);
            buildProcessBuilder.directory(workDir);
            Process process = buildProcessBuilder.startAndWait();
            if(process.exitValue() != 0) {
                System.err.println("Failed to build version " + version + ". Exit code: " + process.exitValue());
                if(stopOnError) return;
            }
        }
    }

    public void versions(String... versions) {
        allVersions.addAll(Arrays.asList(versions));
    }

    public Collection<String> allVersions() {
        return allVersions;
    }

    public void arguments(String... arguments){
        this.arguments.addAll(Arrays.asList(arguments));
    }
}
