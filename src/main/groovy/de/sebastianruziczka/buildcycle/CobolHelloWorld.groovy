package de.sebastianruziczka.buildcycle

import org.gradle.api.Project
import org.gradle.api.tasks.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.sebastianruziczka.CobolExtension

class CobolHelloWorld {
	void apply (Project project, CobolExtension conf){
		Logger logger = LoggerFactory.getLogger('runCobol')


		project.task ('helloWorld') {
			group 'COBOL Demo'
			description 'Copys HelloWorld.cbl in src/main/cobol and executes it'
			doFirst {
				if (conf.fileFormat == 'fixed') {
					copy('res/fixed/HELLOWORLD.cbl', conf.srcMainPath + '/HELLOWORLD.cbl' , logger)
				}
			}
			doLast{
				println 'Overwrite configured srcMain settings'
				conf.srcMain = 'HELLOWORLD'
				new File(conf.absoluteBinMainPath()).getParentFile().mkdirs()
				conf.compiler.buildDebug(conf)
						.setTargetAndBuild(conf.absoluteSrcMainPath())
						.setExecutableDestinationPath(conf.absoluteDebugMainPath(conf.srcMain))
						.execute('COMPILE DEBUG: ' + conf.absoluteBinMainPath())

				println 'Overwriting configured terminal with console terminal'
				conf.terminal = 'current'
				project.tasks.runDebug.execute()
			}
		}
	}

	private void copy(String source, String destination, Logger logger) {
		logger.info('COPY: '+ source + '>>' + destination)
		URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader()
		URL sourceFile = urlClassLoader.findResource(source)
		InputStream is = sourceFile.openStream()

		File targetFile = new File(destination)

		if(!targetFile.getParentFile().exists()) {
			targetFile.mkdirs()
		}

		OutputStream outStream = new FileOutputStream(targetFile)

		byte[] buffer = new byte[1024]
		int length
		while ((length = is.read(buffer)) > 0) {
			outStream.write(buffer, 0, length)
		}

		outStream.close()
		is.close()
	}
}
