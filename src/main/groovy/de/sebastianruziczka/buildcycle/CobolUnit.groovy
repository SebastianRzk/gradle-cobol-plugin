package de.sebastianruziczka.buildcycle

import java.lang.reflect.Constructor

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.reflections.Reflections
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.sebastianruziczka.CobolExtension
import de.sebastianruziczka.api.CobolUnitFrameworkProvider
import de.sebastianruziczka.buildcycle.test.CobolTestPair
import de.sebastianruziczka.buildcycle.test.TestFailedException
import de.sebastianruziczka.buildcycle.test.TestResult

class CobolUnit {
	void apply (Project project, CobolExtension conf){
		Logger logger = LoggerFactory.getLogger('testUnitCobol')
		project.task ('testUnitCobol'){
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

			onlyIf({this.testPresets(logger, project, conf, allUnitTestFrameworks)})

			doLast {
				if (allUnitTestFrameworks.isEmpty()) {
					printNoUnittestFrameworkDefined(logger)
				}
				def testTree = testTree(project, conf)
				def allTests = []

				testTree.each { File file ->
					allTests << file.absolutePath
				}

				def srcTree = sourceTree(project, conf)
				def allSrc = []
				srcTree.each { File file ->
					allSrc << file.absolutePath
				}


				def cobolTestPairs = []
				int unitTestFileEndingChars = conf.unitTestFileTypePattern().length() - '**/*'.length()
				allTests.each {
					int lastNameIndex = it.length() - unitTestFileEndingChars
					int firstNameIndex = conf.absoluteSrcTestPath().length()

					String moduleName = it.substring(firstNameIndex +1,lastNameIndex)

					String expectedSrcModulePath = conf.projectFileResolver(conf.srcMainPath + '/' + moduleName + conf.srcFileType).absolutePath
					if (allSrc.contains(expectedSrcModulePath)) {
						allSrc.remove(expectedSrcModulePath)
						cobolTestPairs << new CobolTestPair(moduleName + conf.srcFileType, it.substring(firstNameIndex + 1))
					}
				}

				if (cobolTestPairs.size()== 0) {
					logger.warn('NO TEST-PAIRS FOUND')
					logger.warn('Convention: Main: <name>' + conf.srcFileType + '  Test: <name>'+conf.unittestPostfix+conf.srcFileType)
					return
				}
				logger.info('Number of Src<>Test pairs found: ' + cobolTestPairs.size())
				allUnitTestFrameworks.each{
					it.configure(conf, project);
					it.prepare();
				}

				allUnitTestFrameworks.each{ framework ->
					println 'Starting Cobol-Unittest with framework: ' + framework.toString()
					TestResult result = new TestResult()
					cobolTestPairs.each{
						result.addTest(framework.test(it.srcFile(), it.testFile()))
					}
					println 'Result: ' + result.successfullTests() + ' sucessfull tests, ' + result.failedTests() + ' tests failed'
					if (result.failedTests() != 0) {
						throw new TestFailedException()
					}
				}
			}
		}
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
		if (testTree(project, conf).getFiles().isEmpty()) {
			logger.info("No source files found!")
			println "No source files found!"
			return false
		}

		return true
	}

	private printNoUnittestFrameworkDefined(Logger logger) {
		logger.info("No cobol unit framework found.")
		logger.info("Make sure your framwork class:")
		logger.info("\t 1. ... is in the classpath of this plugin (via buildscript dependencies)")
		logger.info("\t 2. ... is in the package de.*")
		logger.info("\t 3. ... implements the interface de.sebastianruziczka.CobolUnitFramwork")
		logger.info("\t 4. ... is annotated with @CobolUnitFrameworkProvider")
		println 'No unittest framework found. Use --info for more information'
	}
}