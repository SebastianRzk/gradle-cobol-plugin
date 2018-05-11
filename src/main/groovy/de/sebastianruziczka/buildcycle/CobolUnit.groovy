package de.sebastianruziczka.buildcycle

import java.lang.reflect.Constructor

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.reflections.Reflections
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.sebastianruziczka.CobolExtension
import de.sebastianruziczka.api.CobolUnitFrameworkProvider
import de.sebastianruziczka.buildcycle.unittest.CobolUnitTestTask

class CobolUnit {
	void apply (Project project, CobolExtension conf){
		Logger logger = LoggerFactory.getLogger('testUnitCobol')
		def allUnitTestFrameworks = this.resolveUnitTestFrameworks(logger)
		project.task ('testUnitCobolConfiguration'){
			group 'COBOL Configuration'
			description 'Returns the detected unittest frameworks'
			doLast {
				println 'Detected unittest frameworks : ' + allUnitTestFrameworks.size()
				allUnitTestFrameworks.each{
					println '\t'+it.toString()
				}
			}
		}

		project.task ('testUnitCobol', type:CobolUnitTestTask){
			group 'COBOL'
			description 'Executes UnitTests'

			onlyIf({
				this.testPresets(logger, project, conf, allUnitTestFrameworks)
			})

			unitTestFrameworks = allUnitTestFrameworks
			configuration = conf
		}
	}



	private def resolveUnitTestFrameworks(Logger logger) {
		def allUnitTestFrameworks = []
		try {
			Reflections reflections = new Reflections("de");
			Set<Class<? extends CobolUnitFrameworkProvider>> cobolUnitFrameworks = reflections.getTypesAnnotatedWith(CobolUnitFrameworkProvider.class)
			cobolUnitFrameworks.each{
				logger.info('Detected framworks: ' + cobolUnitFrameworks)
				Constructor constructor = it.getDeclaredConstructors0(true)[0]
				allUnitTestFrameworks << constructor.newInstance()
			}
		} catch (Throwable t) {
			logger.error('Failed while searching for cobol unit frameworks', t)
			logger.error(t.message)
			t.printStackTrace()
		}
		return allUnitTestFrameworks
	}

	private org.gradle.api.tasks.util.PatternFilterable sourceTree(Project project, CobolExtension conf) {
		return project.fileTree(conf.srcMainPath).include(conf.filetypePattern())
	}

	private org.gradle.api.tasks.util.PatternFilterable testTree(Project project, CobolExtension conf) {
		return project.fileTree(conf.srcTestPath).include(conf.unitTestFileTypePattern())
	}

	private boolean testPresets(Logger logger, Project project, CobolExtension conf, def allUnitTestFrameworks) {
		if (allUnitTestFrameworks.isEmpty()) {
			this.printNoUnittestFrameworkDefined(logger)
			return false
		}

		if (testTree(project, conf).getFiles().isEmpty()) {
			logger.info("No test files found!")
			println "No test files found!"
			return false
		}

		if (sourceTree(project, conf).getFiles().isEmpty()) {
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