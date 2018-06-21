package de.sebastianruziczka.buildcycle

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.sebastianruziczka.CobolExtension
import de.sebastianruziczka.api.CobolIntegrationTestFrameworkProvider
import de.sebastianruziczka.api.CobolTestFramework
import de.sebastianruziczka.buildcycle.integrationtest.CobolIntegrationTestTask
import de.sebastianruziczka.buildcycle.test.FrameworkResolver

class CobolBuildcycleIntegration {
	void apply (Project project, CobolExtension conf){
		Logger logger = LoggerFactory.getLogger('testIntegration')

		FrameworkResolver frameworkResolver = new FrameworkResolver('de')
		def allIntegrationTestFrameworks =  frameworkResolver.resolve(CobolIntegrationTestFrameworkProvider, CobolTestFramework, conf, project)

		project.task ('cobolIntegrationTestConfiguration'){
			group 'COBOL Configuration'
			description 'Returns the detected integration test frameworks'
			doLast {
				println 'Detected integration test frameworks : ' + allIntegrationTestFrameworks.size()
				allIntegrationTestFrameworks.each{
					println '\t'+it.toString()
				}
			}
		}

		project.task ('testIntegration', type:CobolIntegrationTestTask, dependsOn: ['compileDebugWithTracing']){
			group 'COBOL Development'
			description 'Executes integration tests'

			onlyIf({
				this.testPresets(logger, project, conf, allIntegrationTestFrameworks)
			})

			integrationTestFrameworks = allIntegrationTestFrameworks
			configuration = conf
		}
	}


	private boolean testPresets(Logger logger, Project project, CobolExtension conf, def allUnitTestFrameworks) {
		if (allUnitTestFrameworks.isEmpty()) {
			this.printNoUnittestFrameworkDefined(logger)
			return false
		}

		if (conf.integrationTestTree().getFiles().isEmpty()) {
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
		logger.info("No cobol integration framework found.")
		logger.info("Make sure your framework class:")
		logger.info("\t 1. ... is in the classpath of this plugin (via buildscript dependencies)")
		logger.info("\t 2. ... is in the package de.*")
		logger.info("\t 3. ... implements the interface de.sebastianruziczka.CobolTestFramework")
		logger.info("\t 4. ... is annotated with @CobolIntegrationTestFrameworkProvider")
		println 'No integration framework found. Use --info for more information'
	}
}