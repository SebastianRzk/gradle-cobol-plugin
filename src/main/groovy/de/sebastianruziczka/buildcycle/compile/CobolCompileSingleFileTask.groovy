package de.sebastianruziczka.buildcycle.compile

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction

import de.sebastianruziczka.CobolExtension

class CobolCompileSingleFileTask extends DefaultTask{

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
		this.compile(this.target)
	}

	public void compile(String mainFile) {
		def dependencies = resolveCompileDependencies(project, conf, mainFile)

		/**
		 * Create folder in /build/ when needed
		 */
		String modulePath = new File(conf.absoluteBinMainPath(mainFile)).getParent()
		File module = new File(modulePath)
		if (!module.exists()) {
			logger.info('Create folder for compile: ' + modulePath)
			module.mkdirs()
		}
		conf.compiler//
				.buildExecutable(this.conf)//
				.addDependencyPaths(dependencies)
				.addIncludePath(modulePath)
				.setTargetAndBuild(conf.absoluteSrcMainPath(mainFile))
				.addAdditionalOption(conf.fileFormat)
				.setExecutableDestinationPath(conf.absoluteBinMainPath(mainFile))
				.execute("COMPILE TASK")
	}

	private List resolveCompileDependencies(Project project, CobolExtension conf, String mainFile) {
		def list = []
		def tree = project.fileTree(conf.absoluteSrcMainModulePath(mainFile)).include(conf.filetypePattern())
		tree.each { File file ->
			if (!file.absolutePath.equals(conf.absoluteSrcMainPath(mainFile))){
				list << file.absolutePath
			}
		}
		return list
	}
}
