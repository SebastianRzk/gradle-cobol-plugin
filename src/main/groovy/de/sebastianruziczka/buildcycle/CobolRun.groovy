package de.sebastianruziczka.buildcycle

import org.gradle.api.Project
import org.gradle.api.tasks.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.sebastianruziczka.CobolExtension

class CobolRun {
	void apply (Project project, CobolExtension conf){
		Logger logger = LoggerFactory.getLogger('cobolCompile')

		project.task ('cobolRun', type:Exec, dependsOn: [
			'cobolCompile',
			'cobolCopyRessources',
			'cobolCopyCopybooks'
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

		project.task ('cobolCopyCopybooks', type:Copy){
			from project.file(conf.srcMainPath).absolutePath
			into project.file(conf.binMainPath).absolutePath
			exclude conf.filetypePattern()
		}

		project.task ('cobolCopyRessources', type: Copy){
			doFirst {
				project.file('bin/main/cobol').mkdirs()
			}
			from project.file(conf.resMainPath).absolutePath
			into project.file(conf.binMainPath).absolutePath
		}
	}
}
