package de.sebastianruziczka.buildcycle

import org.gradle.api.Project
import org.gradle.api.tasks.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.sebastianruziczka.CobolExtension

class CobolRun {
	void apply (Project project, CobolExtension conf){
		Logger logger = LoggerFactory.getLogger('runCobol')

		project.task ('runCobol', type:Exec, dependsOn: [
			'compileCobol',
			'cobolCopyRessources'
		]) {
			doFirst {
				standardInput = System.in
				workingDir = conf.binMainPath
				if (!conf.customTerminal.equals('')) {
					logger.info('Compiling terminal String, replace {path} with actual executable')
					logger.info('Before:')
					logger.info(conf.customTerminal)
					commandLine = conf.customTerminal.replace('{path}', conf.absoluteBinMainPath())
					logger.info('After:')
					logger.info(commandLine)
					return;
				}else if (conf.terminal.equals('gnome-terminal')) {
					String geometry = '--geometry=' + conf.terminalColumns + 'x' + conf.terminalRows
					commandLine 'gnome-terminal', '--wait', geometry, '--', conf.absoluteBinMainPath()
				}else if (conf.terminal.equals('xterm')) {
					logger.warn('!!!xterm does not return the exit value of your programm!!!')
					logger.warn('!!!The return value can be positive even though the program ended unexpectedly!!!')
					String geometryValue = conf.terminalColumns + 'x' + conf.terminalRows
					commandLine 'xterm', '+hold', '-geometry', geometryValue, '-e', conf.absoluteBinMainPath()
				}else {
					throw new IllegalArgumentException('No terminal defined!')
				}
			}
		}


		project.task ('cobolCopyRessources', type: Copy){
			doFirst {
				conf.projectFileResolver('bin/main/cobol').mkdirs()
			}
			from conf.projectFileResolver(conf.resMainPath).absolutePath
			into conf.projectFileResolver(conf.binMainPath).absolutePath
		}
	}
}
