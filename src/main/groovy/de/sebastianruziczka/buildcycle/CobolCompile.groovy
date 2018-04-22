package de.sebastianruziczka.buildcycle

import org.gradle.api.Project
import org.gradle.api.tasks.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.sebastianruziczka.CobolExtension

class CobolCompile {
	void apply (Project project, CobolExtension conf){
		Logger logger = LoggerFactory.getLogger('compileCobol')

		project.task ('compileCobol', type:Exec, dependsOn: 'clean') {
			doFirst {
				def list = []
				def tree = project.fileTree(conf.srcMainPath).include(conf.filetypePattern())
				tree.each { File file ->
					if (!file.absolutePath.equals(conf.absoluteSrcMainPath(project))){
						list << file.absolutePath
					}
				}
				if (conf.srcMain.equals('')) {
					logger.error('No main file configured!')
					logger.error('Please specify main file in your build.gradle')
					print conf.dump()
					throw new Exception('No main file configured!')
				}
				if (!project.file(conf.binMainPath).exists()){
					project.file(conf.binMainPath).mkdirs()
				}
				commandLine 'cobc'

				def arguments = []
				arguments << '-x' // Build executable
				arguments << '-o'
				arguments << conf.absoluteBinMainPath(project) // Executable destination path
				if (conf.fileFormat){
					arguments << '-'+ conf.fileFormat
				}
				arguments << conf.absoluteSrcMainPath(project)
				arguments += list // Add all module dependencies
				workingDir = conf.absoluteSrcMainModulePath(project)
				args = arguments

				logger.info('Start cobc compile job')
				logger.info('cobc args:')
				logger.info(args.toString())
				logger.info('cobc workingDir')
				logger.info(workingDir.toString())
			}
		}

	}
}
