package de.sebastianruziczka.buildcycle.test

class TestFile {

	private def testMethods = []
	private String name = ''

	public void addTestMethod(TestMethod method) {
		this.testMethods << method
	}

	public void addName(String name) {
		this.name = name
	}

	public String name() {
		return this.name
	}

	public int successfullTests() {
		int result = 0
		this.testMethods.each{
			if (it.result() == TestMethodResult.SUCCESSFUL) {
				result ++
			}
		}

		return result
	}


	public int failedTests() {
		int result = 0
		this.testMethods.each{
			if (it.result() == TestMethodResult.FAILED) {
				result ++
			}
		}

		return result
	}
}
