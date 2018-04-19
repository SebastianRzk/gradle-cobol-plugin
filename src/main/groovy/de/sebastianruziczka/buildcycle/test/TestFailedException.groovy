package de.sebastianruziczka.buildcycle.test

class TestFailedException extends RuntimeException{
	public TestFailedException() {
		super('See error log for more information')
	}
}
