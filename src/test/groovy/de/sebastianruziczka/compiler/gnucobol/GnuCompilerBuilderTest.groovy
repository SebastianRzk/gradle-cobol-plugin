package de.sebastianruziczka.compiler.gnucobol;

import static org.junit.Assert.*

import org.junit.Test

import de.sebastianruziczka.compiler.api.CompileStandard

class GnuCompilerBuilderTest {
	@Test
	void some() {
		new GnuCompilerBuilder()//
				.buildExecutable()//
				.addDependencyPath("somepedendency")
				.addIncludePath("someInclude")
				.addIncludePath("otherInclude")
				.setCompileStandard(CompileStandard.ibm)
				.setTargetAndBuild("mainfile.cbl")
				.setExecutableDestinationPath("build/mainfile.cbl")
				.execute("compile process")
	}
}
