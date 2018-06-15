package de.sebastianruziczka.buildcycle

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.sebastianruziczka.CobolExtension
import de.sebastianruziczka.api.CobolTestFramework
import de.sebastianruziczka.api.CobolUnitFrameworkProvider
import de.sebastianruziczka.buildcycle.test.FrameworkResolver
import de.sebastianruziczka.buildcycle.unittest.CobolUnitTestTask

class CobolUnit {
	void apply (Project project, CobolExtension conf){
		Logger logger = LoggerFactory.getLogger('testUnit')

		FrameworkResolver frameworkResolver = new FrameworkResolver('de')
		def allUnitTestFrameworks =  frameworkResolver.resolve(CobolUnitFrameworkProvider, CobolTestFramework, conf, project)

		project.task ('cobolUnitTestConfiguration'){
			group 'COBOL Configuration'
			description 'Returns the detected unittest frameworks'
			doLast {
				println 'Detected unittest frameworks : ' + allUnitTestFrameworks.size()
				allUnitTestFrameworks.each{
					println '\t'+it.toString()
				}
			}
		}

		project.task ('testUnit', type:CobolUnitTestTask){
			group 'COBOL Development'
			description 'Executes unit tests'

			onlyIf({
				this.testPresets(logger, project, conf, allUnitTestFrameworks)
			})

			unitTestFrameworks = allUnitTestFrameworks
			configuration = conf
		}
	}

	private boolean testPresets(Logger logger, Project project, CobolExtension conf, def allUnitTestFrameworks) {
		if (allUnitTestFrameworks.isEmpty()) {
			this.printNoUnittestFrameworkDefined(logger)
			return false
		}

		if (conf.unitTestTree().getFiles().isEmpty()) {
			logger.info("No test files found!")
			println "No test files found!"
			return false
		}

		if (conf.sourceTree().getFiles().isEmpty()) {
			logger.info("No source files found!")
			println "No source files found!"
			return false
		}

		return true
	}

	private printNoUnittestFrameworkDefined(Logger logger) {
		logger.info("No cobol unit framework found.")
		logger.info("Make sure your framework class:")
		logger.info("\t 1. ... is in the classpath of this plugin (via buildscript dependencies)")
		logger.info("\t 2. ... is in the package de.*")
		logger.info("\t 3. ... implements the interface de.sebastianruziczka.CobolTestFramework")
		logger.info("\t 4. ... is annotated with @CobolUnitFrameworkProvider")
		println 'No unittest framework found. Use --info for more information'
	}
}