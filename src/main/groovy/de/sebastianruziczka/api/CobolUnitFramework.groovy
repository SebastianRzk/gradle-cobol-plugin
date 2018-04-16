package de.sebastianruziczka.api

import org.gradle.api.Project

import de.sebastianruziczka.CobolExtension


interface CobolUnitFramework {
	void configure(CobolExtension configuration, Project project)

	int prepare()

	void test(String srcName, String testName)
}
