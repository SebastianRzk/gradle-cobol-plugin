package de.sebastianruziczka.buildcycle.integrationtest

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import de.sebastianruziczka.CobolExtension
import de.sebastianruziczka.api.CobolSourceFile
import de.sebastianruziczka.buildcycle.test.TestFile
import de.sebastianruziczka.buildcycle.test.TestResult
import de.sebastianruziczka.buildcycle.test.TestResultConsolePrinter
import de.sebastianruziczka.buildcycle.test.UnitTestError

class CobolIntegrationTestTask extends DefaultTask{
	def integrationTestFrameworks = []
	def CobolExtension configuration

	@TaskAction
	public void test() {
		integrationTestFrameworks.forEach({ it.clean() })

		def testTree = this.configuration.integrationTestTree()
		def allTests = []

		testTree.each { File file ->
			allTests << file.absolutePath
		}

		def srcTree = this.configuration.sourceTree()
		def allSrc = []
		srcTree.each { File file ->
			allSrc << file.absolutePath
		}


		def cobolTestPairs = []
		int unitTestFileEndingChars = configuration.unitTestFileTypePattern().length() - '**/*'.length()
		allTests.each {
			int lastNameIndex = it.length() - unitTestFileEndingChars
			int firstNameIndex = configuration.absoluteSrcTestPath().length()

			String moduleName = it.substring(firstNameIndex +1,lastNameIndex)

			String expectedSrcModulePath = configuration.projectFileResolver(configuration.srcMainPath + '/' + moduleName + configuration.srcFileType).absolutePath
			if (allSrc.contains(expectedSrcModulePath)) {
				allSrc.remove(expectedSrcModulePath)
				cobolTestPairs << new CobolSourceFile(this.configuration, moduleName + configuration.srcFileType)
			}
		}

		if (cobolTestPairs.size() == 0) {
			logger.warn('NO TEST-PAIRS FOUND')
			logger.warn('Convention: Main: <name>' + configuration.srcFileType + '  Test: <name>'+configuration.unittestPostfix+configuration.srcFileType)
			return
		}

		logger.info('Number of Src<>Test pairs found: ' + cobolTestPairs.size())
		integrationTestFrameworks.each{
			logger.info('Preparing: ' + it.toString())
			it.prepare();
		}
		logger.info('All unittest frameworks prepared')

		integrationTestFrameworks.each{ framework ->
			println 'Starting Cobol-Unittest with framework: ' + framework.toString()
			def errors = []
			TestResult result = new TestResult()
			cobolTestPairs.each{
				try{
					TestFile testFile = framework.test(it)
					result.addTest(testFile)
					if (project.hasProperty("showResults")) {
						println  testFile
					}
				}catch (Throwable t) {
					errors << new UnitTestError(it, t)
				}
			}
			new TestResultConsolePrinter().print(result, errors)
		}
	}
}
