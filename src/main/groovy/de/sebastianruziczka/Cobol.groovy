package de.sebastianruziczka


import org.gradle.api.*
import org.gradle.api.tasks.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.sebastianruziczka.buildcycle.CobolCompile
import de.sebastianruziczka.buildcycle.CobolConfiguration
import de.sebastianruziczka.buildcycle.CobolRun
import de.sebastianruziczka.cobolunit.CobolUnit

class Cobol implements Plugin<Project> {

	void apply(Project project) {
		Logger logger = LoggerFactory.getLogger('cobolCompile')
		def conf = project.extensions.create('cobol', CobolExtension)

		new CobolConfiguration().apply(project, conf)
		new CobolCompile().apply(project, conf)
		new CobolRun().apply(project, conf)

		project.task ('cobolClean', type: Delete){
			doFirst {
				delete project.file(conf.binMainPath).absolutePath
			}
		}

		project.task ('cobolUnit'){
			doLast {
				CobolUnit cobolUnit = new CobolUnit();
				cobolUnit.configure(conf, project);
				cobolUnit.prepare();
				println 'precompile test'
				cobolUnit.test(null, null)
				println 'precompile done'
			}
		}

		project.task ('cobolCheck', dependsOn: [
			'cobolConfiguration',
			'cobolCompile'
		]){ doLast { println 'check finished'
			} }
	}
}
