package de.sebastianruziczka


import org.gradle.api.*
import org.gradle.api.tasks.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.sebastianruziczka.buildcycle.CobolCompile
import de.sebastianruziczka.buildcycle.CobolConfiguration
import de.sebastianruziczka.buildcycle.CobolRunDebug
import de.sebastianruziczka.buildcycle.CobolRunExecutable
import de.sebastianruziczka.buildcycle.CobolUnit

class Cobol implements Plugin<Project> {

	void apply(Project project) {
		def conf = project.extensions.create('cobol', CobolExtension)

		project.afterEvaluate {
			Logger logger = LoggerFactory.getLogger('cobolPlugin')
			conf.projectFileResolver = { s -> project.file(s)}

			new CobolConfiguration().apply(project, conf)
			new CobolCompile().apply(project, conf)
			new CobolRunExecutable().apply(project, conf)
			new CobolRunDebug().apply(project, conf)
			new CobolUnit().apply(project, conf)

			project.task ('clean', type: Delete){
				group 'COBOL'
				description 'Cleans cobol build directory'
				doFirst {
					delete conf.projectFileResolver(conf.binMainPath).absolutePath
				}
			}

			project.task ('check', dependsOn: [
				'testUnitCobol',
				'compileMultiTargetCobol',
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
