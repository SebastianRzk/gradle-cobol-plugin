package de.sebastianruziczka


import org.gradle.api.*
import org.gradle.api.tasks.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.sebastianruziczka.buildcycle.CobolBuildcycleCompile
import de.sebastianruziczka.buildcycle.CobolBuildcycleConfiguration
import de.sebastianruziczka.buildcycle.CobolBuildcycleDemo
import de.sebastianruziczka.buildcycle.CobolBuildcycleIntegration
import de.sebastianruziczka.buildcycle.CobolBuildcycleRunDebug
import de.sebastianruziczka.buildcycle.CobolBuildcycleRunExecutable
import de.sebastianruziczka.buildcycle.CobolBuildcycleUnit

class Cobol implements Plugin<Project> {

	void apply(Project project) {
		def conf = project.extensions.create('cobol', CobolExtension)
		conf.projectFileResolver = { s -> project.file(s)}
		conf.projectFileTreeResolver {s -> project.fileTree(s)}

		project.afterEvaluate {

			if (this.mainNotSet(conf)) {
				conf = this.setMain(conf)
			}

			Logger logger = LoggerFactory.getLogger('cobolPlugin')

			new CobolBuildcycleConfiguration().apply(project, conf)
			new CobolBuildcycleCompile().apply(project, conf)
			new CobolBuildcycleRunExecutable().apply(project, conf)
			new CobolBuildcycleRunDebug().apply(project, conf)
			new CobolBuildcycleUnit().apply(project, conf)
			new CobolBuildcycleIntegration().apply(project, conf)
			new CobolBuildcycleDemo().apply(project, conf)

			project.task ('clean', type: Delete){
				group 'COBOL'
				description 'Cleans cobol build directory'
				doFirst {
					delete conf.projectFileResolver(conf.buildPath).absolutePath
				}
			}
			project.tasks.testIntegration.shouldRunAfter(project.tasks.testUnit)

			project.task ('check', dependsOn: [
				'testIntegration',
				'testUnit',
				'compileMultiTarget',
				'compile',
				'clean',
				'cobolConfiguration'
			]){
				doLast { println 'check finished'}
				group 'COBOL'
				description 'Execute full build cycle (clean+compile+test)'
			}
		}
	}

	private boolean mainNotSet(CobolExtension conf) {
		return conf.srcMain == null || conf.srcMain == ''
	}

	private CobolExtension setMain(CobolExtension conf) {
		def allSourceFiles = []
		def tree = conf.sourceTree()
		tree.each { File file ->
			allSourceFiles << file.absolutePath
		}
		if(allSourceFiles.size() == 1) {
			conf.srcMain = allSourceFiles[0].replace(project.file(conf.srcMainPath).absolutePath + '/', '')//
					.replace(conf.srcFileType, '')
			println 'Autoconfigured srcMain: ' + conf.srcMain
		}
		return conf
	}
}
