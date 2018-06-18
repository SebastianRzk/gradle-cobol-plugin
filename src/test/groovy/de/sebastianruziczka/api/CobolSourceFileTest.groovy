package de.sebastianruziczka.api;

import static org.assertj.core.api.Assertions.assertThat
import static org.junit.Assert.*

import org.junit.Test

import de.sebastianruziczka.CobolExtension

class CobolSourceFileTest {


	@Test
	public void test_baseFileName_shouldReturnNameWithoutPath() {
		CobolSourceFile component_under_test = new CobolSourceFile(new CobolExtension(), 'path/Filename.cbl')

		assertThat(component_under_test.baseFileName()).isEqualTo('Filename')
	}
}
