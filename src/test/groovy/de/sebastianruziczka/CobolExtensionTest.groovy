package de.sebastianruziczka;


import static org.assertj.core.api.Assertions.*

import org.junit.Test

class CobolExtensionTest {


	private CobolExtension createExtension() {
		CobolExtension component_under_test = new CobolExtension();
		component_under_test.projectFileResolver = {s -> get(s)}
		return component_under_test
	}

	File get(String s) {
		return new File('/absolute/' + s)
	}

	@Test
	void test_absoluteBinMainPath_withMain_shouldReturnPathInBin() {
		CobolExtension component_under_test = this.createExtension()
		assertThat(component_under_test.absoluteBinMainPath('Main')).isEqualTo('/absolute/build/bin/main/cobol/Main')
	}

	@Test
	void test_absoluteBinMainPath_withMainInSubmodule_shouldReturnPathInBinWithSubmodule() {
		CobolExtension component_under_test = this.createExtension()
		assertThat(component_under_test.absoluteBinMainPath('mypackage/Main')).isEqualTo('/absolute/build/bin/main/cobol/mypackage/Main')
	}
	@Test
	void test_absoluteBinMainPath_withMainAndFileType_shouldRemoveFiletype() {
		CobolExtension component_under_test = this.createExtension()
		assertThat(component_under_test.absoluteBinMainPath('mypackage/Main.cbl')).isEqualTo('/absolute/build/bin/main/cobol/mypackage/Main')
	}

	@Test
	void test_absoluteSrcMainPath_withNoParams_shouldReturnPath() {
		CobolExtension component_under_test = this.createExtension()
		component_under_test.srcMain = 'Main'
		assertThat(component_under_test.absoluteSrcMainPath()).isEqualTo('/absolute/src/main/cobol/Main.cbl')
	}

	@Test
	void test_absoluteSrcMainPath_withNoParamsAndAdditionalFileType_shouldReturnPath() {
		CobolExtension component_under_test = this.createExtension()
		component_under_test.srcMain = 'Main.cbl'
		assertThat(component_under_test.absoluteSrcMainPath()).isEqualTo('/absolute/src/main/cobol/Main.cbl')
	}
}


