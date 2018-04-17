package de.sebastianruziczka.buildcycle.test

class TestFile {

	private def testMethods = []

	public void addTestMethod(TestMethod method) {
		this.testMethods << method
	}

	public int successfullTests() {
		int result = 0
		this.testMethods.each{
			if (it.testMethodResult == TestMethodResult.SUCCESSFUL) {
				result ++
			}
		}

		return result
	}


	public int failedTests() {
		int result = 0
		this.testMethods.each{
			if (it.testMethodResult == TestMethodResult.FAILED) {
				result ++
			}
		}

		return result
	}
}
