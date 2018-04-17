package de.sebastianruziczka.api

import org.gradle.api.Project

import de.sebastianruziczka.CobolExtension
import de.sebastianruziczka.buildcycle.test.TestFile


interface CobolUnitFramework {
	void configure(CobolExtension configuration, Project project)

	int prepare()

	TestFile test(String srcName, String testName)
}
