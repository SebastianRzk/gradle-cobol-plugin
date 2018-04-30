package de.sebastianruziczka.buildcycle.test

class TestResult {
	private def testFiles = []

	public void addTest(TestFile testFile) {
		this.testFiles << testFile
	}

	public int successfullTests() {
		int result = 0
		this.testFiles.each{  result += it.successfullTests() }
		return result
	}

	public int failedTests() {
		int result = 0
		this.testFiles.each{  result += it.failedTests() }
		return result
	}

	public void visitFailedTests(Closure visitor) {
		this.testFiles.each{ it.visitFailedTests(visitor)}
	}
}
