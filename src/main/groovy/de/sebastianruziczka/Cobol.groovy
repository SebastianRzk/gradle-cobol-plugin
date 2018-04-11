package de.sebastianruziczka

class CobolExtension {
	String srcFileType = '.cbl'
	String srcMain = ''

	String srcMainPath = 'src/main/cobol'
	String binMainPath = 'build/bin/main/cobol'
	String resMainPath = 'res/main/cobol'

	String fileFormat = 'fixed'

	String terminal = 'gnome-terminal'
	String customTerminal = ''

	def filetypePattern(){
		'**/*' + srcFileType
	}

	def absoluteSrcMainModulePath(Project project){
		return project.file(srcMainPath + '/' + srcMain + srcFileType).getParent()
	}

	def absoluteSrcMainPath(Project project){
		return project.file(srcMainPath + '/' + srcMain + srcFileType).absolutePath
	}

	def absoluteBinMainPath(Project project){
		return project.file(binMainPath + '/' +  srcMain).absolutePath
	}
}

import org.gradle.api.*
import org.gradle.api.tasks.*
import  org.gradle.util.GradleVersion
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Cobol implements Plugin<Project> {

	void apply(Project project) {
		def Logger logger = LoggerFactory.getLogger('cobolCompile')
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

				//commandLine conf.absoluteBinMainPath(project)
				println commandLine
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

		project.task ('cobolCompilerVersion', type: Exec){
			commandLine = 'cobc'
			args = ['--version']
		}

		project.task ('cobolGradleVersion'){
			doLast {
				GradleVersion gradleVersion = GradleVersion.current()
				println gradleVersion.properties.collect{'\t'+it}.join('\n')
			}
		}

		project.task ('cobolGradleConfiguration') {
			doFirst {
				println conf.properties.collect{'\t'+it}.join('\n')
				println '\t###Computed paths:###'
				println '\tabsoluteSrcMainModulePath: ' + conf.absoluteSrcMainModulePath(project)
				println '\tabsoluteSrcMainPath: ' + conf.absoluteSrcMainPath(project)
				println '\tabsoluteBinMainPath: ' + conf.absoluteBinMainPath(project)
			}
		}

		project.task ('cobolConfiguration', dependsOn: [
			'cobolGradleVersion',
			'cobolCompilerVersion',
			'cobolGradleConfiguration'
		]){ doFirst { println 'Conf' } }

		project.task ('cobolCheck', dependsOn: [
			'cobolConfiguration',
			'cobolCompile'
		]){ doLast { println 'check finished' } }


	}
}
