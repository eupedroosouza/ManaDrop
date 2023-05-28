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
package io.github.patrick.gradle.remapper.tasks.util;

import net.md_5.specialsource.Jar;
import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.JarRemapper;
import net.md_5.specialsource.provider.JarProvider;
import net.md_5.specialsource.provider.JointProvider;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.DependencyHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public enum ActualProcedure {
    MOJANG_OBF("org.spigotmc:minecraft-server:{version}-R0.1-SNAPSHOT:maps-mojang@txt",
            "org.spigotmc:spigot:{version}-R0.1-SNAPSHOT:remapped-mojang",
            true),
    OBF_MOJANG("org.spigotmc:minecraft-server:{version}-R0.1-SNAPSHOT:maps-mojang@txt",
            "org.spigotmc:spigot:{version}-R0.1-SNAPSHOT:remapped-obf",
            false),
    SPIGOT_OBF("org.spigotmc:minecraft-server:{version}-R0.1-SNAPSHOT:maps-spigot@csrg",
            "org.spigotmc:spigot:{version}-R0.1-SNAPSHOT",
            true),
    OBF_SPIGOT("org.spigotmc:minecraft-server:{version}-R0.1-SNAPSHOT:maps-spigot@csrg",
            "org.spigotmc:spigot:{version}-R0.1-SNAPSHOT:remapped-obf",
            false);

    private final String mapping;
    private final String inheritance;
    private final boolean reversed;

    ActualProcedure(String mapping, String inheritance, boolean reversed) {
        this.mapping = mapping;
        this.inheritance = inheritance;
        this.reversed = reversed;
    }

    public String getMapping(String version){
        return mapping.replace("{version}", version);
    }

    public String getInheritance(String version){
        return inheritance.replace("{version}", version);
    }

    public void remap(Project project, String version, File jarFile, File outputFile) throws IOException {
        DependencyHandler dependencies = project.getDependencies();

        File mappingFile = project.getConfigurations().detachedConfiguration(dependencies
                .create(getMapping(version))).getSingleFile();
        List<File> inheritanceFiles = new ArrayList<>(project.getConfigurations().detachedConfiguration(dependencies
                .create(getInheritance(version))).getFiles());

        try(Jar inputJar = Jar.init(jarFile)){
            // ignore SpecialSource multiple main class err
            PrintStream err = System.err;
            System.setErr(nullPrintStream);

            try(Jar inheritanceJar = Jar.init(inheritanceFiles)){
                JarMapping mappings = new JarMapping();
                mappings.loadMappings(mappingFile.getCanonicalPath(), reversed, false, null, null);

                JointProvider provider = new JointProvider();
                provider.add(new JarProvider(inputJar));
                provider.add(new JarProvider(inheritanceJar));
                mappings.setFallbackInheritanceProvider(provider);

                JarRemapper mapper = new JarRemapper(mappings);
                mapper.remapJar(inputJar, outputFile);
            }finally {
                System.setErr(err);
            }

        }

    }

    private static final PrintStream nullPrintStream = new PrintStream(new OutputStream() {
        @Override
        public void write(int b) throws IOException {

        }
    });

}
