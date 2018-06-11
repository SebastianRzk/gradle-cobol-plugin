package de.sebastianruziczka.api

import org.gradle.api.Project

import de.sebastianruziczka.CobolExtension
import de.sebastianruziczka.buildcycle.test.TestFile


interface CobolTestFramework {
	void configure(CobolExtension configuration, Project project)

	int prepare()

	TestFile test(CobolSourceFile file)

	void clean()
}
