package de.sebastianruziczka.buildcycle.compile

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs

import de.sebastianruziczka.CobolExtension

class CobolCompileDebugTask extends DefaultTask{

	CobolExtension configuration

	@SkipWhenEmpty
	@InputDirectory
	def File inputDir

	@OutputFile
	def File outputDir

	@TaskAction
	public def compile(IncrementalTaskInputs inputs) {
		String sourceModule = this.configuration.projectFileResolver(this.configuration.srcMainPath).absolutePath
		inputs.outOfDate { change ->
			if (!change.file.name.endsWith(this.configuration.srcFileType)) {
				return
			}
			String target = change.file.absolutePath.replace(sourceModule, '')
			compileFile(target, change.file.absolutePath)
		}

		inputs.removed { change ->
			def targetFile = this.configuration.projectFileResolver(change.file.absolutePath())
			if (targetFile.exists()) {
				targetFile.delete()
			}
		}
	}

	void compileFile(String target, String absoluteTargetPath) {
		String modulePath = new File(absoluteTargetPath).getParent()
		File module = new File(modulePath)
		/**
		 * Create folder in /build/ when needed
		 */
		if (!module.exists()) {
			logger.info('Create folder for compile: ' + modulePath)
			module.mkdirs()
		}
		this.configuration.compiler.buildDebug(this.configuration)
				.setTargetAndBuild(absoluteTargetPath)
				.setExecutableDestinationPath(this.configuration.absoluteDebugMainPath(target))
				.execute('COMPILE DEBUG: ' + target)
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
