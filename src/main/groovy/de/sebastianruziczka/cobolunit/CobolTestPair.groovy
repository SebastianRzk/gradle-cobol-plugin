package de.sebastianruziczka.cobolunit

class CobolTestPair {

	private String testFile
	private String srcFile

	CobolTestPair(String srcFile, String testFile){
		this.testFile = testFile
		this.srcFile = srcFile
	}

	public String testFile() {
		return this.testFile
	}

	public String srcFile() {
		return this.srcFile
	}
}