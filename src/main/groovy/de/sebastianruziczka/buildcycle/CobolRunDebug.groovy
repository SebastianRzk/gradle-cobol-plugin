package de.sebastianruziczka.buildcycle

import org.gradle.api.Project
import org.gradle.api.tasks.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.sebastianruziczka.CobolExtension

class CobolRunDebug {
	void apply (Project project, CobolExtension conf){
		Logger logger = LoggerFactory.getLogger('runDebug')


		project.task ('buildDebug', dependsOn: [
			'compileDebug',
			'copyRessources'
		]){
			group 'COBOL Development'
			description 'Builds incremental an cobcrun-runnable programm in build folder'
		}

		project.task ('runDebug', type:Exec, dependsOn: ['buildDebug']) {
			group 'COBOL Development'
			description 'Compiles incremental the cobol programm sourcecode and runs it with cobcrun'
			doFirst {
				standardInput = System.in
				workingDir = conf.binMainPath
				def terminalCommand = ['cobcrun']
				terminalCommand << '-M'
				terminalCommand << conf.absoluteBinMainModule(conf.srcMain) + '/ '// Add dynamic module path
				terminalCommand << conf.srcMain

				if (!conf.customTerminal.equals('')) {
					logger.info('Compiling terminal String, replace {path} with actual terminal command')
					logger.info('Before:')
					logger.info(conf.customTerminal)
					commandLine = conf.customTerminal.replace('{path}', terminalCommand)
					logger.info('After:')
					logger.info(commandLine)
					return;
				}else if (conf.terminal.equals('gnome-terminal')) {
					String geometry = '--geometry=' + conf.terminalColumns + 'x' + conf.terminalRows
					def commandList = [
						"gnome-terminal",
						"--wait",
						geometry,
						"--"
					]
					commandList.addAll(terminalCommand)
					println 'terminalCommand: ' + terminalCommand
					commandLine = commandList
				}else if (conf.terminal.equals('xterm')) {
					logger.warn('!!!xterm does not return the exit value of your programm!!!')
					logger.warn('!!!The return value can be positive even though the program ended unexpectedly!!!')
					String geometryValue = conf.terminalColumns + 'x' + conf.terminalRows
					def commandList =  [
						'xterm',
						'+hold',
						'-geometry',
						geometryValue,
						'-e'
					]
					commandList.addAll(terminalCommand)
					commandLine commandList
				}else {
					throw new IllegalArgumentException('No terminal defined!')
				}
			}
		}
	}
}
