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
		def dependencies = resolveCompileDependencies(project, conf, target)
		String modulePath = new File(conf.absoluteBinMainPath(target)).getParent()
		File module = new File(modulePath)
		/**
		 * Create folder in /build/ when needed
		 */
		if (!module.exists()) {
			logger.info('Create folder for compile: ' + modulePath)
			module.mkdirs()
		}
		conf.compiler//
				.buildExecutable(this.conf)//
				.addDependencyPaths(dependencies)
				.addIncludePath(modulePath)
				.setTargetAndBuild(conf.absoluteSrcMainPath(target))
				.addAdditionalOption(conf.fileFormat)
				.setExecutableDestinationPath(conf.absoluteBinMainPath(target))
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
