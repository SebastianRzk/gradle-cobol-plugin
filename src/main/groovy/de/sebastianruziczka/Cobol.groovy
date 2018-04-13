package de.sebastianruziczka


import org.gradle.api.*
import org.gradle.api.tasks.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.sebastianruziczka.buildcycle.CobolConfiguration
import de.sebastianruziczka.cobolunit.CobolUnit

class Cobol implements Plugin<Project> {

	void apply(Project project) {
		Logger logger = LoggerFactory.getLogger('cobolCompile')
		def conf = project.extensions.create('cobol', CobolExtension)

		project.task ('cobolCompile', type:Exec, dependsOn: 'cobolClean') {
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

		project.task ('cobolRun', type:Exec, dependsOn: [
			'cobolCompile',
			'cobolCopyRessources'
		]) {
			doFirst {
				standardInput = System.in
				if (!conf.customTerminal.equals('')) {
					logger.info('Compiling terminal String, replace {path} with actual executable')
					logger.info('Before:')
					logger.info(conf.customTerminal)
					commandLine = conf.customTerminal.replace('{path}', conf.absoluteBinMainPath(project))
					logger.info('After:')
					logger.info(commandLine)
					return;
				}else if (conf.terminal.equals('gnome-terminal')) {
					commandLine 'gnome-terminal', '--wait', '--', conf.absoluteBinMainPath(project)
				}
			}
		}

		project.task ('cobolCopyRessources', type: Copy){
			doFirst {
				project.file('bin/main/cobol').mkdirs()
			}
			from project.file(conf.resMainPath).absolutePath
			into project.file(conf.binMainPath).absolutePath
		}

		project.task ('cobolClean', type: Delete){
			doFirst {
				delete project.file(conf.binMainPath).absolutePath
			}
		}

		project.task ('cobolUnit'){
			doLast {
				CobolUnit cobolUnit = new CobolUnit();
				cobolUnit.configure(conf, project);
				cobolUnit.prepare();
			}
		}

		new CobolConfiguration().apply(project, conf)

		project.task ('cobolCheck', dependsOn: [
			'cobolConfiguration',
			'cobolCompile'
		]){ doLast { println 'check finished' } }


	}
}
