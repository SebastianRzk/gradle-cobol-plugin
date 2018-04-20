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
				workingDir = conf.binMainPath
				if (!conf.customTerminal.equals('')) {
					logger.info('Compiling terminal String, replace {path} with actual executable')
					logger.info('Before:')
					logger.info(conf.customTerminal)
					commandLine = conf.customTerminal.replace('{path}', conf.absoluteBinMainPath(project))
					logger.info('After:')
					logger.info(commandLine)
					return;
				}else if (conf.terminal.equals('gnome-terminal')) {
					String geometry = '--geometry=' + conf.terminalRows + 'x' + conf.terminalColumns
					commandLine 'gnome-terminal', '--wait', geometry, '--', conf.absoluteBinMainPath(project)
				}else if (conf.terminal.equals('xterm')) {
					logger.warn('!!!xterm does not return the exit value of your programm!!!')
					logger.warn('!!!The return value can be positive even though the program ended unexpectedly!!!')
					String geometryValue = conf.terminalRows + 'x' + conf.terminalColumns
					commandLine 'xterm', '+hold', '-geometry', geometryValue, '-e', conf.absoluteBinMainPath(project)
				}else {
					throw new IllegalArgumentException('No terminal defined!')
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
