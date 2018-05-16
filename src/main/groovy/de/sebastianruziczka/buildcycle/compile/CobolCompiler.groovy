package de.sebastianruziczka.buildcycle.compile

import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.sebastianruziczka.CobolExtension

class CobolCompiler {
	private Project project
	private CobolExtension conf
	private Logger logger = LoggerFactory.getLogger('COBOL COMPILER')

	public CobolCompiler(Project project, CobolExtension conf) {
		this.project = project
		this.conf = conf
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
		logger.error("EEEEEEEEEEEEEEEEEEERRRRRRRRRORRRRRRRRRRRRRR!")
		conf.compiler//
				.buildExecutable()//
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
