package de.sebastianruziczka.buildcycle.compile

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction

import de.sebastianruziczka.CobolExtension

class CobolCompileMultibleFilesTask extends DefaultTask{

	CobolExtension configuration
	String target
	Project pr

	@SkipWhenEmpty
	@InputDirectory
	def File inputDir

	@OutputFile
	def File outputDir

	@TaskAction
	public def compile() {
		println 'chello'
	}
}
