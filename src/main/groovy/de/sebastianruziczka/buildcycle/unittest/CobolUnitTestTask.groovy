package de.sebastianruziczka.buildcycle.unittest

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import de.sebastianruziczka.CobolExtension
import de.sebastianruziczka.api.CobolSourceFile
import de.sebastianruziczka.buildcycle.test.TestResult
import de.sebastianruziczka.buildcycle.test.TestResultConsolePrinter
import de.sebastianruziczka.buildcycle.test.UnitTestError

class CobolUnitTestTask extends DefaultTask{
	def unitTestFrameworks = []
	def CobolExtension configuration

	@TaskAction
	public void test() {
		unitTestFrameworks.forEach({ it.clean() })

		def testTree = this.configuration.unitTestTree()
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
		unitTestFrameworks.each{
			logger.info('Preparing: ' + it.toString())
			it.prepare();
		}
		logger.info('All unittest frameworks prepared')

		unitTestFrameworks.each{ framework ->
			println 'Starting Cobol-Unittest with framework: ' + framework.toString()
			def errors = []
			TestResult result = new TestResult()
			cobolTestPairs.each{
				try{
					result.addTest(	framework.test(it))
				}catch (Throwable t) {
					errors << new UnitTestError(it, t)
				}
			}
			new TestResultConsolePrinter().print(result, errors)
		}
	}
}
