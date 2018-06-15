package de.sebastianruziczka.buildcycle.test;


import static org.assertj.core.api.Assertions.assertThat

import org.junit.Test

class TestResultTest {
	@Test
	void test_visitorWithTwoParameter() {
		TestFile file = new TestFile()
		file.addName("testfile")
		TestMethod method = new TestMethod("METHOD", TestMethodResult.FAILED, "no message", "no console")
		file.addTestMethod(method)
		TestResult testResult = new TestResult()
		testResult.addTest(file)


		TestFile resultFile = null
		TestMethod resultMethod = null
		testResult.visitFailedTests({c,b ->
			println c.name() + ":" + b.name()
			resultFile = c
			resultMethod = b
		})

		assertThat(resultFile).isEqualTo(file)
		assertThat(resultMethod).isEqualTo(method)
	}

	@Test
	void test_visitorWithOneParameter() {
		TestFile file = new TestFile()
		file.addName("testfile")
		TestMethod method = new TestMethod("METHOD", TestMethodResult.FAILED, "no message", "no console")
		file.addTestMethod(method)
		TestResult testResult = new TestResult()
		testResult.addTest(file)

		TestMethod resultMethod = null
		testResult.visitFailedTests({b ->
			println b.name()
			resultMethod = b
		})

		assertThat(resultMethod).isEqualTo(method)
	}
}
