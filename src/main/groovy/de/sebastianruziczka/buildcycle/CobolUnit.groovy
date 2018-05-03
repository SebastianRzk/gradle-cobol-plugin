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
import de.sebastianruziczka.buildcycle.test.TestFile
import de.sebastianruziczka.buildcycle.test.TestMethod
import de.sebastianruziczka.buildcycle.test.TestResult

class CobolUnit {
	void apply (Project project, CobolExtension conf){
		Logger logger = LoggerFactory.getLogger('testUnitCobol')
		project.task ('testUnitCobolConfiguration'){
			group 'COBOL Configuration'
			description 'Returns the detected unittest frameworks'
			doLast {
				def allUnitTestFrameworks = this.resolveUnitTestFrameworks(logger)
				println 'Detected unittest frameworks : ' + allUnitTestFrameworks.size()
				allUnitTestFrameworks.each{
					println '\t'+it.toString()
				}
			}
		}

		project.task ('testUnitCobol'){
			group 'COBOL'
			description 'Executes UnitTests'
			def allUnitTestFrameworks = this.resolveUnitTestFrameworks(logger)

			onlyIf({
				this.testPresets(logger, project, conf, allUnitTestFrameworks)
			})

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
					logger.info('Configure: ' + it.toString())
					it.configure(conf, project);
					logger.info('Preparing: ' + it.toString())
					it.prepare();
				}
				logger.info('All unittest frameworks prepared')

				allUnitTestFrameworks.each{ framework ->
					println 'Starting Cobol-Unittest with framework: ' + framework.toString()
					TestResult result = new TestResult()
					cobolTestPairs.each{
						result.addTest(framework.test(it.srcFile(), it.testFile()))
					}
					println 'Collecting results'
					int successfull  = result.successfullTests()
					int failed = result.failedTests()
					println 'Result: ' + successfull + ' sucessfull tests, ' + failed + ' tests failed'
					if (failed != 0) {
						println '-------------------------------------------------------------------------'
						println '-------------------------------FAILED TESTS------------------------------'
						println '-------------------------------------------------------------------------'
						println ''
						result.visitFailedTests ({ file,test -> printFailedTest(file, test) })
						throw new TestFailedException()
					}
				}
			}
		}
	}

	private void printFailedTest(TestFile testFile, TestMethod testMethod) {
		println '>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>'
		println 'File:'
		println '\t' + testFile.name() + '>' + testMethod.name() + ':'
		println 'Message:'
		println '\t' + testMethod.message()
		println 'Console:'
		println '\t' + testMethod.console()
		println '<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<'
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