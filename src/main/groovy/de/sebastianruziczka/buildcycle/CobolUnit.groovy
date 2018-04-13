package de.sebastianruziczka.buildcycle

import org.gradle.api.Project

import de.sebastianruziczka.CobolExtension

class CobolUnit {

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
		files.each{ copy('res/'+it, binFramworkPath+it )}

		return 0
	}

	private void copy(String source, String destination) {
		println '\tCOPY'+ source + '>>' + destination
		URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
		URL manifestUrl = urlClassLoader.findResource(source);
		InputStream is = manifestUrl.openStream();

		byte[] buffer = new byte[is.available()];
		is.read(buffer);

		File targetFile = new File(destination);
		OutputStream outStream = new FileOutputStream(targetFile);
		outStream.write(buffer);
		outStream.close();
		is.close();
	}
}
