# gradle-cobol-plugin
[![Build Status](https://travis-ci.org/RosesTheN00b/gradle-cobol-plugin.svg?branch=master)](https://travis-ci.org/RosesTheN00b/gradle-cobol-plugin)

This project is part of my master thesis.

You can discover some examples [here](https://github.com/RosesTheN00b/gradle-cobol-plugin-example)

This repo is the sourcecode of the gradle-cobol-plugin. A documentation will follow later :-).


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
         id 'de.sebastianruziczka.Cobol' version '0.0.2'
    }

And a minimal configuration:

    cobol {
         srcMain = 'HelloWorld' // Path to your main file in src/main/cobol
    }


Run your application with

    gradle cobolRun

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

