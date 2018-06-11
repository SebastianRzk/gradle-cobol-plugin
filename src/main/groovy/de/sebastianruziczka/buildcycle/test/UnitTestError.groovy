package de.sebastianruziczka.buildcycle.test

import static de.sebastianruziczka.api.CobolCodeType.source
import static de.sebastianruziczka.api.CobolCodeType.test

import de.sebastianruziczka.api.CobolSourceFile

class UnitTestError {
	private CobolSourceFile file
	private Throwable t
	public UnitTestError(CobolSourceFile file, Throwable t) {
		this.file = file
		this.t = t
	}

	public String srcFile() {
		return this.file.getModulePath(source)
	}

	public String testFile() {
		return this.file.getModulePath(test)
	}

	public Throwable throwable() {
		return this.t
	}
}
