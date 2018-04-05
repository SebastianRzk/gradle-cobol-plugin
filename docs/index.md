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
         id 'de.sebastianruziczka.Cobol' version '0.0.1'
    }

And a minimal configuration:

    cobol {
         src_main = 'HelloWorld' // Path to your main file in src/main/cobol
    }

Further configuration can be found [here](/docs/further/configuration.md)

Run your application with

    gradle cobolRun

## deep documentation

[configuration](/docs/further/configuration.md)


