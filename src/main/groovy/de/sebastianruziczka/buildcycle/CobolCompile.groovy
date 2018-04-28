package de.sebastianruziczka.buildcycle

import org.gradle.api.Project
import org.gradle.api.tasks.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.sebastianruziczka.CobolExtension
import de.sebastianruziczka.process.ProcessWrapper

class CobolCompile {
	void apply (Project project, CobolExtension conf){
		Logger logger = LoggerFactory.getLogger('compileCobol')

		project.task ('compileCobol') {
			group 'COBOL'
			description 'Compiles cobol source code and creates executable defined in srcMain'
			doFirst {
				checkIfMainFileIsSet(logger, conf)
				prepareBinFolder(conf)

				String mainFile = conf.srcMain

				execCobolCompileForFile(project, conf, mainFile, logger)
			}
		}

		project.task ('compileMultiTargetCobol') {
			group 'COBOL'
			description 'Compiles additional executables when defined in multiCompileTargets'
			onlyIf({ return !conf.multiCompileTargets.isEmpty()})
			doFirst {
				prepareBinFolder(conf)

				conf.multiCompileTargets.each{
					execCobolCompileForFile(project, conf, it, logger)
				}
			}
		}
	}

	private void execCobolCompileForFile(Project project, CobolExtension conf, String mainFile, Logger logger) {
		def dependencies = resolveCompileDependencies(project, conf, mainFile)

		def command = ['cobc']
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
		String logPath = conf.projectFileResolver(conf.binMainPath + '/' + mainFile + '_COMPILE.LOG').absolutePath
		ProcessWrapper wrapper = new ProcessWrapper(processBuilder, 'Compile ' + mainFile, logPath)

		logger.info('Start cobc compile job')
		wrapper.exec()
	}

	private List resolveCompileDependencies(Project project, CobolExtension conf, String mainFile) {
		def list = []
		def tree = project.fileTree(conf.srcMainPath).include(conf.filetypePattern())
		tree.each { File file ->
			if (!file.absolutePath.equals(conf.absoluteSrcMainPath(mainFile))){
				list << file.absolutePath
			}
		}
		return list
	}

	private checkIfMainFileIsSet(Logger logger, CobolExtension conf) {
		if (conf.srcMain.equals('')) {
			logger.error('No main file configured!')
			logger.error('Please specify main file in your build.gradle')
			print conf.dump()
			throw new Exception('No main file configured!')
		}
	}

	private prepareBinFolder(CobolExtension conf) {
		if (!conf.projectFileResolver(conf.binMainPath).exists()){
			conf.projectFileResolver(conf.binMainPath).mkdirs()
		}
	}
}
