package de.sebastianruziczka.buildcycle.compile

import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.sebastianruziczka.CobolExtension

class CobolCompilerDirectExectuatble {
	private Project project
	private CobolExtension conf
	private Logger logger = LoggerFactory.getLogger('COBOL COMPILER')

	public CobolCompilerDirectExectuatble(Project project, CobolExtension conf) {
		this.project = project
		this.conf = conf
	}
}
