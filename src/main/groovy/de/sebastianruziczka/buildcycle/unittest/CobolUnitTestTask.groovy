package de.sebastianruziczka.buildcycle.unittest

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

import de.sebastianruziczka.CobolExtension
import de.sebastianruziczka.buildcycle.test.CobolTestPair
import de.sebastianruziczka.buildcycle.test.TestFailedException
import de.sebastianruziczka.buildcycle.test.TestResult
import de.sebastianruziczka.buildcycle.test.UnitTestError

class CobolUnitTestTask extends DefaultTask{
	def unitTestFrameworks = []
	def CobolExtension configuration

	@TaskAction
	public void test() {
		def testTree = testTree(project, configuration)
		def allTests = []

		testTree.each { File file ->
			allTests << file.absolutePath
		}

		def srcTree = sourceTree(project, configuration)
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
				cobolTestPairs << new CobolTestPair(moduleName + configuration.srcFileType, it.substring(firstNameIndex + 1))
			}
		}

		if (cobolTestPairs.size()== 0) {
			logger.warn('NO TEST-PAIRS FOUND')
			logger.warn('Convention: Main: <name>' + configuration.srcFileType + '  Test: <name>'+configuration.unittestPostfix+configuration.srcFileType)
			return
		}

		logger.info('Number of Src<>Test pairs found: ' + cobolTestPairs.size())
		unitTestFrameworks.each{
			logger.info('Configure: ' + it.toString())
			it.configure(configuration, project);
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
					result.addTest(	framework.test(it.srcFile(), it.testFile()))
				}catch (Throwable t) {
					errors << new UnitTestError(it.srcFile(), it.testFile(), t)
				}
			}
			println 'Collecting results'
			int successfull  = result.successfullTests()
			int failed = result.failedTests()
			println 'Result: ' + successfull + ' sucessfull tests, ' + failed + ' tests failed ' + errors.size() + ' tests errored'
			if (failed != 0) {
				println '-------------------------------------------------------------------------'
				println '-------------------------------FAILED TESTS------------------------------'
				println '-------------------------------------------------------------------------'
				println ''
				result.visitFailedTests ({ testFile,testMethod ->
					println '>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>'
					println 'File:'
					println '\t' + testFile.name() + '>' + testMethod.name() + ':'
					println 'Message:'
					println '\t' + testMethod.message()
					println 'Console:'
					println '\t' + testMethod.console()
					println '<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<'
				})
				throw new TestFailedException()
			}

			if (!errors.isEmpty()) {
				println '-------------------------------------------------------------------------'
				println '-------------------------------ERRORED TESTS-----------------------------'
				println '-------------------------------------------------------------------------'
				println ''
				errors.each{
					println '>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>'
					println 'Errored at files:'
					println '\t' + it.srcFile() + '<>' + it.testFile()
					println 'Exception:'
					println '\t' + it.throwable().dump()
					println 'Trace:'
					println '\t' + it.throwable().printStackTrace()
					println '<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<'
				}
				throw new TestFailedException()
			}
		}
	}

	private org.gradle.api.tasks.util.PatternFilterable sourceTree(Project project, CobolExtension conf) {
		return project.fileTree(conf.srcMainPath).include(conf.filetypePattern())
	}

	private org.gradle.api.tasks.util.PatternFilterable testTree(Project project, CobolExtension conf) {
		return project.fileTree(conf.srcTestPath).include(conf.unitTestFileTypePattern())
	}
}
