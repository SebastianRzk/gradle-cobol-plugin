# gradle-cobol-plugin
[![Build Status](https://travis-ci.org/RosesTheN00b/gradle-cobol-plugin.svg?branch=master)](https://travis-ci.org/RosesTheN00b/gradle-cobol-plugin)

This project is part of my master thesis.

You can discover some examples [here](https://github.com/RosesTheN00b/gradle-cobol-plugin-example)


## Gradle-cobol environment

* [gradle-cobol-plugin](https://github.com/RosesTheN00b/gradle-cobol-plugin) The base gradle plugin (compile, run)
* [gradle-cobol-plugin-unittest-extension](https://github.com/RosesTheN00b/gradle-cobol-plugin-unittest-extension) Adds unittests to the base plugin
* [gradle-cobol-plugin-example](https://github.com/RosesTheN00b/gradle-cobol-plugin-example) This Project contains many gradle-cobol example projects

Further Reading:

* [GNUCobol-compiler](https://open-cobol.sourceforge.io/) The cobol compiler
* [cobol-unit-test](https://github.com/neopragma/cobol-unit-test) The documentation of the unit-test feature


## Quickstart guide

### Install

Build your own plugin-jar with the command

    gradle publish

or import it from the provided repo (in your settings.gradle):

    mvn: https://sebastianruziczka.de/repo/mvn
    ivy: https://sebastianruziczka.de/repo/ivy

To use the plugin, you need [GNUCobol](https://sourceforge.net/projects/open-cobol/) and [Gradle](https://gradle.org/).

On ubuntu:

    sudo apt install gradle open-cobol

On Arch (via yaourt):

    yaourt gnu-cobol gradle


### Configure the plugin

Add to your build.gradle:

    plugins {
         id 'de.sebastianruziczka.Cobol' version '0.0.8'
    }

And a minimal configuration:

    cobol {
         srcMain = 'HelloWorld' // Path to your main file in src/main/cobol
    }


Run your application with

    gradle cobolRun

Add this lines at the top of your build.gradle to enable unittests (more information: [gradle-cobol-plugin-unittest-extension](https://github.com/RosesTheN00b/gradle-cobol-plugin-unittest-extension)):

     buildscript {
     	 dependencies {
     		classpath group: 'de.sebastianruziczka', name: 'gradle-cobol-plugin-unittest-extension', version: '0.0.1'
     	}
     }

## Deep documentation

### Configuration of the plugin

Following properties can be modified in the _cobol_ block in your _build.gradle_ :


| name | default | other | required |
| ---- | ------- | ----- | -------- |
| srcFileType | '.cbl' | e.g. '.CBL' | yes |
| srcMain | '' | | yes |
| srcMainPath | 'src/main/cobol' || yes |
| binMainPath | 'build/bin/main/cobol' || yes |
| resMainPath | 'res/main/cobol' || yes |
| fileFormat | 'fixed' |'free'| yes |

### Terminal configuration


#### preconfigured terminals


Set the parameter _terminal_ in the cobol block in your build gradle, to use one of the preconfigured terminals.

| terminal | value | default |
| -------- | ----- | --------|
| gnome-terminal | 'gnome-terminal' ||
| xterm | 'xterm' | yes |


#### configure own terminal

Set the parameter _customTerminal_ in the cobol clock in your build.gradle to use a custom terminal commands.

Insert the full qualified terminal command string. Use `{path}` as placeholder for the absolute path to the executable.

