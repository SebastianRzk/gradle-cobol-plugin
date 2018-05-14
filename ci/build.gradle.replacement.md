buildscript {
	 dependencies {
		classpath group: 'de.sebastianruziczka', name: 'gradle-cobol-plugin-unittest-extension', version: '0.0.5'
	}
}

plugins {
	id 'de.sebastianruziczka.Cobol' version '0.0.17' apply false
}

subprojects {
	apply plugin: 'de.sebastianruziczka.Cobol'
}


