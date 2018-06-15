package de.sebastianruziczka.buildcycle.test

class TestResultConsolePrinter {

	public void print(TestResult results, Collection<UnitTestError> errors) {
		println 'Collecting results'
		int successfull  = results.successfullTests()
		int failed = results.failedTests()
		println 'Result: ' + successfull + ' sucessfull tests, ' + failed + ' tests failed ' + errors.size() + ' tests errored'
		if (failed != 0) {
			println '-------------------------------------------------------------------------'
			println '-------------------------------FAILED TESTS------------------------------'
			println '-------------------------------------------------------------------------'
			println ''
			results.visitFailedTests ({ testFile,testMethod ->
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
