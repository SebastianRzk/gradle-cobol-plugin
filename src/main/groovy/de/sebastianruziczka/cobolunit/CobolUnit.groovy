package de.sebastianruziczka.cobolunit

import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.sebastianruziczka.CobolExtension

class CobolUnit {
	Logger logger = LoggerFactory.getLogger('cobolUnit')

	private CobolExtension configuration;
	private Project project;
	private def defaultConf = ["ZUTZCWS", "SAMPLET"]
	private final static MAIN_FRAMEWORK_PROGRAMM =  'ZUTZCPC.CBL'
	private final static DEFAULT_CONF_NAME = 'DEFAULT.CONF'

	void configure(CobolExtension configuration, Project project) {
		this.configuration = configuration;
		this.project = project;
	}

	int prepare() {
		def files = [
			MAIN_FRAMEWORK_PROGRAMM,
			'ZUTZCPD.CPY',
			'ZUTZCWS.CPY'
		]
		String binFramworkPath = this.configuration.absoluteUnitTestFramworkPath(this.project, this.getClass().getSimpleName()) + '/';
		new File(binFramworkPath).mkdirs()
		logger.info('Moving sources of framwork into build')
		files.each{
			copy('res/'+it, binFramworkPath+it )
		}

		logger.info('Create default test.conf')
		this.createTestConf()

		logger.info('Start compiling cobol-unit test framework')
		return this.compileTestFramwork(binFramworkPath, MAIN_FRAMEWORK_PROGRAMM)
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

	private void createTestConf() {
		String path = this.defaultConfPath()
		logger.info('Using Path: '+path)
		def defaultConfFile = new File(path)
		defaultConfFile.delete()

		defaultConfFile.withWriter { out ->
			this.defaultConf.each { out.println it }
		}
	}


	private String defaultConfPath() {
		return this.configuration.absoluteUnitTestFramworkPath(this.project, this.getClass().getSimpleName()) + '/' + DEFAULT_CONF_NAME
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

	public void test(String srcName, String testName) {
		this.preprocessTest('Main.CBL', 'Main_Test.CBL', null)
	}

	private int preprocessTest(String mainFile, String testFile, String testConfig) {
		String zutzcpcPath = this.configuration.absoluteUnitTestFramworkPath(this.project, this.getClass().getSimpleName()) + '/' + this.getFileName(MAIN_FRAMEWORK_PROGRAMM)
		ProcessBuilder processBuilder=new ProcessBuilder(zutzcpcPath)

		def env = processBuilder.environment()
		env.put('SRCPRG', this.configuration.absoluteSrcMainModulePath(this.project)+ '/'+ mainFile)
		env.put('TESTPRG', this.configuration.absoluteUnitTestFramworkPath(this.project, this.getClass().getSimpleName()) + '/' + testFile)
		env.put('TESTNAME', this.getFileName(testFile))
		if (testConfig == null) {
			env.put('UTSTCFG', this.defaultConfPath())
		}else {
			env.put('UTSTCFG', testConfig)
		}
		String copybooks = this.getParent(mainFile) + ':' + this.getParent(testFile)
		env.put('COBCOPY', copybooks)
		env.put('UTESTS', this.configuration.absoluteSrcTestPath(this.project) + '/' + testFile)

		logger.info('Environment: ' + env.dump())

		processBuilder.redirectOutput(new File("precompile.log"))

		logger.info('Test precompile command args: ' + processBuilder.command().dump())
		def process = processBuilder.start()
		process.waitFor()
		return process.exitValue()
	}

	private String getFileName(String path) {
		File file = new File(path)
		String name = file.getName()
		if (name.contains(".")) {
			name = name.split("\\.")[0]
		}
		return name
	}

	private String getParent(String path) {
		File file = new File(path)
		return file.getParent()
	}
}
