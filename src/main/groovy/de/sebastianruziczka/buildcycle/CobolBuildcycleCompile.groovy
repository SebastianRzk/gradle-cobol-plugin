package de.sebastianruziczka.buildcycle

import org.gradle.api.Project
import org.gradle.api.tasks.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.sebastianruziczka.CobolExtension
import de.sebastianruziczka.buildcycle.compile.CobolCompileDebugTask
import de.sebastianruziczka.buildcycle.compile.CobolCompileExecutableTask
import de.sebastianruziczka.buildcycle.compile.CobolMultitargetCompileTask

class CobolBuildcycleCompile {
	void apply (Project project, CobolExtension conf){
		Logger logger = LoggerFactory.getLogger('COBOL COMPILE')


		project.task ('compile', type:CobolCompileExecutableTask) {
			group 'COBOL'
			description 'Compiles cobol source code and creates executable defined in srcMain. Incremental build disabled.'

			onlyIf {
				conf.srcMain != null && !conf.srcMain.equals('')
			}

			outputDir = new File(conf.absoluteBinMainPath())
			inputDir = new File(conf.absoluteSrcMainModulePath())

			configuration = conf
			target = conf.srcMain

			doFirst {
				checkIfMainFileIsSet(logger, conf)
				prepareBinFolder(conf)
			}
		}

		project.task ('compileDebug', type:CobolCompileDebugTask) {
			group 'COBOL Development'
			description 'Compiles each cobol source code itself to *.so into build folder.'

			outputDir = conf.projectFileResolver(conf.binMainPath)
			inputDir = new File(conf.absoluteSrcMainModulePath())

			configuration = conf

			doFirst {
				checkIfMainFileIsSet(logger, conf)
				prepareBinFolder(conf)
			}
		}

		project.task ('compileDebugWithTracing', type:CobolCompileDebugTask) {
			group 'COBOL Development'
			description 'Compiles each cobol source code itself to *.so into build folder. Enables statement based tracing.'

			outputDir = conf.projectFileResolver(conf.binMainPath)
			inputDir = new File(conf.absoluteSrcMainModulePath())

			configuration = conf
			tracing = true

			doFirst {
				checkIfMainFileIsSet(logger, conf)
				prepareBinFolder(conf)
			}
		}

		project.task ('compileMultiTarget', type: CobolMultitargetCompileTask) {
			group 'COBOL'
			description 'Compiles additional executables when defined in multiCompileTargets'

			onlyIf({
				return !conf.multiCompileTargets.isEmpty()
			})

			allTargets = conf.multiCompileTargets
			configuration = conf

			doFirst { prepareBinFolder(conf) }
		}
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
