package de.sebastianruziczka.buildcycle.demo

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import de.sebastianruziczka.CobolExtension

class HelloWorldTask extends DefaultTask{

	CobolExtension configuration


	@TaskAction
	public void compileAndRun() {
		println 'Overwrite configured srcMain settings'
		this.configuration.srcMain = 'HELLOWORLD'
		new File(this.configuration.absoluteBinMainPath()).getParentFile().mkdirs()
		this.configuration.compiler.buildDebug(this.configuration)
				.setTargetAndBuild(this.configuration.absoluteSrcMainPath())
				.setExecutableDestinationPath(this.configuration.absoluteDebugMainPath(this.configuration.srcMain))
				.execute('COMPILE DEBUG: ' + this.configuration.absoluteBinMainPath())

		println 'Overwriting configured terminal with console terminal'
		this.configuration.terminal = 'current'
		this.project.tasks.runDebug.execute()
	}
}
