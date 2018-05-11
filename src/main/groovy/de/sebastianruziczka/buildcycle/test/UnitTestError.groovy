package de.sebastianruziczka.buildcycle.test

class UnitTestError {
	private String srcFile
	private String testFile
	private Throwable t
	public UnitTestError(String srcFile, String testFile, Throwable t) {
		this.srcFile = srcFile
		this.testFile = testFile
		this.t = t
	}

	public String srcFile() {
		return this.srcFile
	}

	public String testFile() {
		return this.testFile
	}

	public Throwable throwable() {
		return this.t
	}
}
