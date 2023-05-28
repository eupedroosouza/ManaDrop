![banner](logo/banner.png)
![Build badge](https://img.shields.io/github/actions/workflow/status/eupedroosouza/ManaDrop/gradle.yml?branch=master)
![License Badge](https://img.shields.io/github/license/eupedroosouza/ManaDrop)
![Release badge](https://img.shields.io/github/v/release/eupedroosouza/ManaDrop?include_prereleases)

ManaDrop is a Gradle plugin designed to help Minecraft developers.
It helps to automatise some tasks and makes projects more portables.

### Features
Here are main features of this plugin. For more details about them, check the [wiki](https://github.com/eupedroosouza/ManaDrop/wiki/)

### General
- [Dependency and Repository shortcuts](https://github.com/eupedroosouza/ManaDrop/wiki/General-features#dependency-and-repository-shortcuts)
- [YAML validation](https://github.com/eupedroosouza/ManaDrop/wiki/General-features#yaml-validation)

### Spigot and Paper
- [Plugin.yml generation](https://github.com/eupedroosouza/ManaDrop/wiki/Spigot#pluginyml-generation)
- [NMS dependencies management](https://github.com/eupedroosouza/ManaDrop/wiki/Spigot#nms-support)

### BungeeCord and Waterfall
- [Bungee.yml generation](https://github.com/eupedroosouza/ManaDrop/wiki/BungeeCord#bungeeyml-generation)

### NMS Remap
- [Wiki](https://github.com/eupedroosouza/ManaDrop/wiki/Remap)


### Merge 
As of version 0.4.4, ManaDrop has been merged with mojang-spigot-remapper by @patrick-choe (https://github.com/patrick-choe/mojang-spigot-remapper) and now has a NMS remapping system.
- [More info and Wiki](https://github.com/eupedroosouza/ManaDrop/wiki/Remap) 

### Use in your project
You can use this plugin in your Gradle Build by applying it.
- Using `apply plugin: io.github.eupedroosouza.fr.il_totore.manadrop:version`

- Using the new `plugins` statement
```gradle
plugins {
   id 'io.github.eupedroosouza.fr.il_totore.manadrop' version 'version'
}
```

Check the latest available version [here](https://plugins.gradle.org/plugin/io.github.eupedroosouza.fr.il_totore.manadrop)

### Changelog

#### 0.4.4
- BuildTools now accepts arguments.
- ManaDrop has been merged with mojang-spigot-remapper and now has a NMS remapping system.


[//]: # ([![Codacy Badge]&#40;https://api.codacy.com/project/badge/Grade/9af1fd09f7514581a0c2d900c176d50c&#41;]&#40;https://www.codacy.com/manual/Iltotore/ManaDrop?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Iltotore/ManaDrop&amp;utm_campaign=Badge_Grade&#41;)

[//]: # ([![Known Vulnerabilities]&#40;https://snyk.io/test/github/Iltotore/ManaDrop/badge.svg?targetFile=build.gradle&#41;]&#40;https://snyk.io/test/github/Iltotore/ManaDrop?targetFile=build.gradle&#41;)