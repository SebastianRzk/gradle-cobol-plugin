package de.sebastianruziczka


import org.gradle.api.*
import org.gradle.api.tasks.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.sebastianruziczka.buildcycle.CobolCompile
import de.sebastianruziczka.buildcycle.CobolConfiguration
import de.sebastianruziczka.buildcycle.CobolDemo
import de.sebastianruziczka.buildcycle.CobolRunDebug
import de.sebastianruziczka.buildcycle.CobolRunExecutable
import de.sebastianruziczka.buildcycle.CobolUnit

class Cobol implements Plugin<Project> {

	void apply(Project project) {
		def conf = project.extensions.create('cobol', CobolExtension)
		conf.projectFileResolver = { s -> project.file(s)}
		conf.projectFileTreeResolver {s -> project.fileTree(s)}

		project.afterEvaluate {
			if (conf.srcMain == null || conf.srcMain == '') {
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
			}

			Logger logger = LoggerFactory.getLogger('cobolPlugin')

			new CobolConfiguration().apply(project, conf)
			new CobolCompile().apply(project, conf)
			new CobolRunExecutable().apply(project, conf)
			new CobolRunDebug().apply(project, conf)
			new CobolUnit().apply(project, conf)
			new CobolDemo().apply(project, conf)

			project.task ('clean', type: Delete){
				group 'COBOL'
				description 'Cleans cobol build directory'
				doFirst {
					delete conf.projectFileResolver(conf.binMainPath).absolutePath
				}
			}

			project.task ('check', dependsOn: [
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
}
