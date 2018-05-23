package de.sebastianruziczka.buildcycle.compile

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs

import de.sebastianruziczka.CobolExtension

class CobolCompileModulesTask extends DefaultTask{

	CobolExtension configuration
	String target
	Project pr

	@SkipWhenEmpty
	@InputDirectory
	def File inputDir

	@OutputFile
	def File outputDir

	@TaskAction
	public def compile(IncrementalTaskInputs inputs) {
		inputs.outOfDate { change ->
			compilerFile(change.file)
		}

		inputs.removed { change ->
			def targetFile = project.file(change.file.absolutePath())
			if (targetFile.exists()) {
				targetFile.delete()
			}
		}
	}

	private compileFile(File file) {
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
				.buildExecutable(this.configuration)//
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
