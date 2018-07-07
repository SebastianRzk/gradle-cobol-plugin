package de.sebastianruziczka.buildcycle.test

class TestMethod {
	private String name
	private TestMethodResult result
	private String message
	private String console = ''

	public TestMethod(String name, TestMethodResult result) {
		this(name, result, null)
	}

	public TestMethod(String name, TestMethodResult result, String message) {
		this.result = result
		this.name = name
		this.message = message
	}


	public TestMethod(String name, TestMethodResult result, String message, String console) {
		this.result = result
		this.name = name
		this.message = message
		this.console = console
	}

	public String name() {
		return this.name
	}

	public TestMethodResult result() {
		return this.result
	}

	public String message() {
		return this.message
	}

	public String console() {
		return this.console
	}

	public void visitFailedTests(Closure c, TestFile parent) {
		if (this.result == TestMethodResult.SUCCESSFUL) {
			return
		}
		if (c.maximumNumberOfParameters == 1) {
			c(this)
			return
		}
		c(parent, this)
	}

	@Override
	public String toString() {
		return this.result.name() + ' ::: ' + this.name
	}
}
