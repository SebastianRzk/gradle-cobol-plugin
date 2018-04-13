package de.sebastianruziczka.cobolunit

import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.sebastianruziczka.CobolExtension

class CobolUnit {
	Logger logger = LoggerFactory.getLogger('cobolUnit')

	private CobolExtension configuration;
	private Project project;

	void configure(CobolExtension configuration, Project project) {
		this.configuration = configuration;
		this.project = project;
	}

	int prepare() {
		def files = [
			'ZUTZCPC.CBL',
			'ZUTZCPD.CPY',
			'ZUTZCWS.CPY'
		]
		String binFramworkPath = this.configuration.aboluteUnitTestFramworkPath(this.project, this.getClass().getSimpleName()) + '/';
		new File(binFramworkPath).mkdirs()
		logger.info('Moving sources of framwork into build')
		files.each{ copy('res/'+it, binFramworkPath+it )}
		logger.info('Start compiling cobol-unit test framework')
		return compileTestFramwork(binFramworkPath, 'ZUTZCPC.CBL')
	}

	private int compileTestFramwork(String frameworkPath,String mainfile) {
		ProcessBuilder processBuilder=new ProcessBuilder('cobc', '-x', '-std=ibm', mainfile)
		def file = new File(frameworkPath)
		processBuilder.directory(file)
		logger.info('Framwork compile command args: ' + processBuilder.command().dump())
		def process = processBuilder.start()
		process.waitFor()
		return process.exitValue()
	}

	private void copy(String source, String destination) {
		logger.info('COPY: '+ source + '>>' + destination)
		URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
		URL manifestUrl = urlClassLoader.findResource(source);
		InputStream is = manifestUrl.openStream();

		File targetFile = new File(destination);
		OutputStream outStream = new FileOutputStream(targetFile);

		byte[] buffer = new byte[1024];
		int length;
		while ((length = is.read(buffer)) > 0) {
			outStream.write(buffer, 0, length);
		}

		outStream.close();
		is.close();
	}
}
