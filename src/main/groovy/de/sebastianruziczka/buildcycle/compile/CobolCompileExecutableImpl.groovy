package de.sebastianruziczka.buildcycle.compile

import org.gradle.api.Project

import de.sebastianruziczka.CobolExtension

class CobolCompileExecutableImpl{

	public def compile(Project project, CobolExtension configuration, String target) {
		def dependencies = resolveCompileDependencies(project, configuration, target)
		String modulePath = new File(configuration.absoluteBinMainPath(target)).getParent()
		File module = new File(modulePath)
		/**
		 * Create folder in /build/ when needed
		 */
		if (!module.exists()) {
			logger.info('Create folder for compile: ' + modulePath)
			module.mkdirs()
		}
		configuration.compiler//
				.buildExecutable(configuration)//
				.addDependencyPaths(dependencies)
				.addIncludePath(modulePath)
				.setTargetAndBuild(configuration.absoluteSrcMainPath(target))
				.addAdditionalOption(configuration.fileFormat)
				.setExecutableDestinationPath(configuration.absoluteBinMainPath(target))
				.execute("COMPILE TASK")
	}

	private List resolveCompileDependencies(Project project,CobolExtension configuration, String mainFile) {
		def list = []
		def tree = project.fileTree(configuration.absoluteSrcMainModulePath(mainFile)).include(configuration.filetypePattern())
		tree.each { File file ->
			if (!file.absolutePath.equals(configuration.absoluteSrcMainPath(mainFile))){
				list << file.absolutePath
			}
		}
		return list
	}
}
