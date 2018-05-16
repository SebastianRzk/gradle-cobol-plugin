# gradle-cobol-plugin
[![Build Status](https://travis-ci.org/RosesTheN00b/gradle-cobol-plugin.svg?branch=master)](https://travis-ci.org/RosesTheN00b/gradle-cobol-plugin)

This project is part of my master thesis.

You can discover some examples [here](https://github.com/RosesTheN00b/gradle-cobol-plugin-example)


## Gradle-cobol environment

* [![Build Status](https://travis-ci.org/RosesTheN00b/gradle-cobol-plugin.svg?branch=master)](https://travis-ci.org/RosesTheN00b/gradle-cobol-plugin)  [gradle-cobol-plugin](https://github.com/RosesTheN00b/gradle-cobol-plugin) The base gradle plugin (compile, run)
* [![Build Status](https://travis-ci.org/RosesTheN00b/gradle-cobol-plugin-unittest-extension.svg?branch=master)](https://travis-ci.org/RosesTheN00b/gradle-cobol-plugin-unittest-extension)  [gradle-cobol-plugin-unittest-extension](https://github.com/RosesTheN00b/gradle-cobol-plugin-unittest-extension) Adds unittests to the base plugin
* [![Build Status](https://travis-ci.org/RosesTheN00b/gradle-cobol-plugin-example.svg?branch=master)](https://travis-ci.org/RosesTheN00b/gradle-cobol-plugin-example)  [gradle-cobol-plugin-example](https://github.com/RosesTheN00b/gradle-cobol-plugin-example) This Project contains many gradle-cobol example projects

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

Add to your build.gradle (preferred):

    plugins {
         id 'de.sebastianruziczka.Cobol' version 'latest'
    }


Or hardcode a specific version:

    plugins {
         id 'de.sebastianruziczka.Cobol' version '0.0.18'
    }


And a minimal configuration:

    cobol {
         srcMain = 'HelloWorld' // Path to your main file in src/main/cobol
    }


Run your application with

    gradle run

Add this lines at the top of your build.gradle to enable unittests (more information: [gradle-cobol-plugin-unittest-extension](https://github.com/RosesTheN00b/gradle-cobol-plugin-unittest-extension)):

     buildscript {
     	 dependencies {
     		classpath group: 'de.sebastianruziczka', name: 'gradle-cobol-plugin-unittest-extension', version: 'latest'
     	}
     }

Or hardcode with specific version (not preferred):

     buildscript {
     	 dependencies {
     		classpath group: 'de.sebastianruziczka', name: 'gradle-cobol-plugin-unittest-extension', version: '0.0.6'
     	}
     }

# Deep documentation

## Configuration of the plugin

Following properties can be modified in the _cobol_ block in your _build.gradle_ :


| name | usage | default | other | required |
| ---- | ----- | ------- | ----- | -------- |
| srcFileType | _compileCobol_, _run_, _test_, _compileMultiTarget_ | '.cbl' | e.g. '.CBL' | yes |
| srcMain | _compileCobol_, _run_ | '' | | yes |
| srcMainPath | _compileCobol_, _run_, _test_, _compileMultiTargetCobol_ | 'src/main/cobol' || yes |
| binMainPath | _compileCobol_, _run_, _test_ | 'build/bin/main/cobol' || yes |
| resMainPath | _compile_, _run_ | 'res/main/cobol' || yes |
| srcTestPath | _test_ | 'src/test/cobol' | | yes |
| multiCompileTargets | _compileMultiTargetCobol_ | [] | other files to be compiled | No |
| fileFormat | _compileCobol_, _run_, _test_, _compileMultiTargetCobol_ | 'fixed' |'free'| yes |
| terminal | _run_ | 'xterm' | 'gnome-terminal' | (yes) (or  _customTerminal_) |
| terminalRows | _run_ | 43 |  | yes |
| terminalColumns | _run_ | 80 |  | yes |
| customTerminal | _run_ | '' | | no |
| compiler | all tasks | instance of GnuCobol |  | yes |
## Terminal configuration


### preconfigured terminals


Set the parameter _terminal_ in the cobol block in your build gradle, to use one of the preconfigured terminals.

| terminal | value | default |
| -------- | ----- | --------|
| gnome-terminal | 'gnome-terminal' ||
| xterm | 'xterm' | yes |


### configure own terminal

Set the parameter _customTerminal_ in the cobol block in your build.gradle to use a custom terminal commands.

Insert the full qualified terminal command string. Use `{path}` as placeholder for the absolute path to the executable.


## tasks

| name | input | output | dependsOn |
| ---- | ----- | ------ | --------- |
| _compile_ | `srcMain` | executable in `build` |  |
| _cobolCopyRessources_ | `resMain` | ressources in build directory |  |
| _buildCobol_ |  | runnable programm in build directory | _compileCobol_, _cobolCopyRessources_ | 
| _run_ | everything in build directory | terminal process | _buildCobol_ |
| _compileMultiTarget_ | defined main files in `multiCompileTargets` | executables in build directory |  |
| _testUnitCobol_ | `srcTest` | result of tests |  |
| _checkCobol_ | everything | check result | _testUnitCobol_, _compileCobol_, _cobolConfiguration_ |

## Develop own testing plugin

Basics how to extend this project.

Example: [![Build Status](https://travis-ci.org/RosesTheN00b/gradle-cobol-plugin-unittest-extension.svg?branch=master)](https://travis-ci.org/RosesTheN00b/gradle-cobol-plugin-unittest-extension)  [gradle-cobol-plugin-unittest-extension](https://github.com/RosesTheN00b/gradle-cobol-plugin-unittest-extension) Adds unittests to the base plugin

### Basics

At first, add the gradleAPI and gradle-cobol-plugin as dependency:

	repositories {
		mavenCentral()
		jcenter()
		maven {
			url 'https://sebastianruziczka.de/repo/mvn'
		}
	}

	dependencies {
		compileOnly gradleApi()
		compileOnly group: 'de.sebastianruziczka', name: 'gradle-cobol-plugin', version: 'latest'
	}


To use your own testing groovy/java/kotlin ... -code, several conditions must be met.

Make sure your main framework class:

* ... is in the classpath of this plugin (via buildscript dependencies)
* ... is in the package de.*
* ... implements the interface de.sebastianruziczka.CobolTestFramework
* ... is annotated with @CobolUnitFrameworkProvider
		
Methods of the interface CobolTestFramework:

* `void configure(CobolExtension configuration, Project project)`: Initial configuration call. Here you can define new gradle tasks, if needed.
* `int prepare()`: Initial call for framework initialization. Returns a process return code (default: 0)
* `TestFile test(String srcName, String testName)`: Called for every pair of src<->testfile. Returns an instance of `TestFile`

### Reuse exiting code

#### ProcessWrapper (de.sebastianruziczka.process.ProcessWrapper)

Wrapps an existing Java ProcessBuilder. 

Features:

* Waits until process is finished
* Writes process output into file
* Writes process output into terminal (loglevel: INFO)
* Throws exception when process returns with statuscode != 0
* Loggs process output in terminal (loglevel: ERROR) when process returns with statuscode != 0

#### CompilerBuilder (de.sebastianruziczka.compiler.api.CompilerBuilder)

Interface to the configured compiler. An instance is located in `CobolExtension.compiler`

