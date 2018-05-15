package de.sebastianruziczka.buildcycle.compile

import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.sebastianruziczka.CobolExtension
import de.sebastianruziczka.process.ProcessWrapper

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

		def command = ['cobc']
		command << '-v' //
		command << '-x' // Build executable
		command << '-o'
		command << conf.absoluteBinMainPath(mainFile) // Executable destination path
		if (conf.fileFormat){
			command << '-'+ conf.fileFormat
		}
		command << conf.absoluteSrcMainPath(mainFile)
		command += dependencies // Add all module dependencies

		ProcessBuilder processBuilder = new ProcessBuilder(command)
		processBuilder.directory(new File(conf.absoluteSrcMainModulePath(mainFile)))
		def env = processBuilder.environment()
		env.put('COBCOPY', modulePath)
		String logPath = conf.projectFileResolver(conf.binMainPath + '/' + mainFile + '_COMPILE.LOG').absolutePath
		ProcessWrapper wrapper = new ProcessWrapper(processBuilder, 'Compile ' + mainFile, logPath)

		logger.info('Start cobc compile job')
		wrapper.exec()
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
