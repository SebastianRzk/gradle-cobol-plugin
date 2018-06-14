package de.sebastianruziczka.buildcycle.compile

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import de.sebastianruziczka.CobolExtension

class CobolMultitargetCompileTask extends DefaultTask{

	List<String> allTargets = new ArrayList<>()

	CobolExtension configuration;

	@TaskAction
	public void compile() {
		this.allTargets.each{
			new CobolCompileExecutableImpl().compile(project, this.configuration, it)
		}
	}
}
