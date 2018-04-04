package de.sebastianruziczka

class CobolExtension {
	String src_file_type = '.cbl'
	String src_main = ''

	String src_main_path = 'src/main/cobol'
	String bin_main_path = 'build/bin/main/cobol'
	String res_main_path = 'res/main/cobol'

	Boolean free_format = false

	def filetypePattern(){
		'**/*' + src_file_type
	}

	def absoluteSrcMainModulePath(Project project){
		return project.file(src_main_path + '/' + src_main + src_file_type).getParent()
	}

	def absoluteSrcMainPath(Project project){
		return project.file(src_main_path + '/' + src_main + src_file_type).absolutePath
	}

	def absoluteBinMainPath(Project project){
		return project.file(bin_main_path + '/' +  src_main).absolutePath
	}
}

import org.gradle.api.*
import org.gradle.api.tasks.*

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.lang.Exception

class Cobol implements Plugin<Project> {

	void apply(Project project) {
		def Logger logger = LoggerFactory.getLogger('cobolCompile')
		def conf = project.extensions.create('cobol', CobolExtension)

		project.task ('cobolCompile', type:Exec, dependsOn: 'cobolClean') {

			def list = []
			def tree = project.fileTree(conf.src_main_path).include(conf.filetypePattern())
			tree.each { File file ->
				if (!file.path.equals(conf.absoluteSrcMainPath(project))){
					list << file.path
				}
			}

			doFirst {
				if (conf.src_main.equals('')) {
					logger.error('No main file configured!')
					logger.error('Please specify main file in your build.gradle')
					print conf.dump()
					throw new Exception('No main file configured!')
				}
				if (!project.file(conf.bin_main_path).exists()){
					project.file(conf.bin_main_path).mkdirs()
				}
				commandLine 'cobc'

				def arguments = []
				arguments << '-x' // Build executable
				arguments << '-o'
				arguments << conf.absoluteBinMainPath(project) // Executable destination path
				if (conf.free_format){
					arguments << '-free'
				}else{
					arguments << '-fixed'
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

		project.task ('cobolRun', type:Exec, dependsOn: ['cobolCompile', 'cobolCopyRessources']) {
			commandLine 'gnome-terminal', '--wait', '--', conf.absoluteBinMainPath(project)
			standardInput = System.in
		}

		project.task ('cobolCopyRessources', type: Copy){
			doFirst {
				project.file('bin/main/cobol').mkdirs()
			}
			from project.file(conf.res_main_path).absolutePath
			into project.file(conf.bin_main_path).absolutePath
		}

		project.task ('cobolClean', type: Delete){
			doFirst {
				delete project.file(conf.bin_main_path).absolutePath
			}
		}

		project.task ('cobolCompilerVersion', type: Exec){
			commandLine = 'cobc'
			args = ['--version']
		}

		project.task ('cobolGradleConfiguration') {
			doFirst {
				println '>>> Configured settings:'
				println conf.dump()
				println 'Computed paths:'
				println 'absoluteSrcMainModulePath: ' + conf.absoluteSrcMainModulePath(project)
				println 'absoluteSrcMainPath: ' + conf.absoluteSrcMainPath(project)
				println 'absoluteBinMainPath: ' + conf.absoluteBinMainPath(project)
				println '<<<'
			}
		}

		project.task ('cobolConfiguration', dependsOn: ['cobolCompilerVersion', 'cobolGradleConfiguration']){
			doFirst {
				println 'Conf'
			}
		}

		project.task ('cobolCheck', dependsOn: ['cobolConfiguration', 'cobolCompile']){
			doLast {
				println 'check finished'
			}
		}


	}
}

