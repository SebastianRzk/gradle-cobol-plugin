package de.sebastianruziczka.buildcycle.test

class TestMethod {
	private String name
	private TestMethodResult result
	private String message
	public TestMethod(String name, TestMethodResult result) {
		this(name, result, null)
	}

	public TestMethod(String name, TestMethodResult result, String message) {
		this.result = result
		this.name = name
		this.message = message
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
}
