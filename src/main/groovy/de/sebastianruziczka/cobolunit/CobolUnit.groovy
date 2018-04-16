package de.sebastianruziczka.cobolunit

import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.sebastianruziczka.CobolExtension

class CobolUnit {
	Logger logger = LoggerFactory.getLogger('cobolUnit')

	private CobolExtension configuration
	private Project project
	private def defaultConf = ["ZUTZCWS", "SAMPLET"]
	private final static MAIN_FRAMEWORK_PROGRAMM =  'ZUTZCPC.CBL'
	private final static DEFAULT_CONF_NAME = 'DEFAULT.CONF'

	void configure(CobolExtension configuration, Project project) {
		this.configuration = configuration
		this.project = project
	}

	int prepare() {
		def files = [
			MAIN_FRAMEWORK_PROGRAMM,
			'ZUTZCPD.CPY',
			'ZUTZCWS.CPY'
		]
		String binFramworkPath = this.frameworkBin()+ '/'
		new File(binFramworkPath).mkdirs()
		logger.info('Moving sources of framwork into build')
		files.each{
			copy('res/' + it, binFramworkPath + it )
		}

		logger.info('Create default test.conf')
		this.createTestConf()

		logger.info('Start compiling cobol-unit test framework')
		return this.compileTestFramework(binFramworkPath, MAIN_FRAMEWORK_PROGRAMM)
	}

	private int compileTestFramework(String frameworkPath,String mainfile) {
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
		return this.frameworkBin() + '/' + DEFAULT_CONF_NAME
	}

	private void copy(String source, String destination) {
		logger.info('COPY: '+ source + '>>' + destination)
		URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader()
		URL manifestUrl = urlClassLoader.findResource(source)
		InputStream is = manifestUrl.openStream()

		File targetFile = new File(destination)
		OutputStream outStream = new FileOutputStream(targetFile)

		byte[] buffer = new byte[1024]
		int length
		while ((length = is.read(buffer)) > 0) {
			outStream.write(buffer, 0, length)
		}

		outStream.close()
		is.close()
	}

	public void test(String srcName, String testName) {
		String srcModulePath = this.srcModuleOf(srcName)
		String testModulePath = this.testModuleOf(testName)

		logger.info('Preprocess Test: ' + testName)
		this.preprocessTest(srcName, testName, null)
		logger.info('Compile Test: ' + testName)
		this.compileTest(srcModulePath, testModulePath, testName)
		logger.info('Run Test: ' + testName)
		String result = this.executeTest(this.frameworkBinModuleOf(testName), this.getFileName(testName))
	}

	private String executeTest(String binModulePath, String execName) {
		def logFilePath = binModulePath + '/' + 'TESTEXEC.LOG'
		File logOutput = new File(logFilePath)

		ProcessBuilder processBuilder = new ProcessBuilder(binModulePath + '/' + execName)
		processBuilder.directory(new File(binModulePath))
		processBuilder.redirectOutput(logOutput)

		logger.info('Executing test file: '+ binModulePath + '/' + execName)


		Process process = processBuilder.start()
		process.waitFor()
		String output = new File(logFilePath).text
		if (process.exitValue() != 0) {
			logger.error(output)
			throw new IllegalArgumentException('Test execution returned non zero value: '+ process.exitValue())
		}
		logger.info(output)
		return output
	}




	private String srcModuleOf(String relativePath) {
		String absolutePath = this.configuration.absoluteSrcMainPath(this.project) + '/' + relativePath
		return this.project.file(absolutePath).getParent()
	}

	private String testModuleOf(String relativePath) {
		String absolutePath = this.configuration.absoluteSrcTestPath(this.project) + '/' + relativePath
		return this.project.file(absolutePath).getParent()
	}

	private String frameworkBinModuleOf(String relativePath) {
		String absolutePath = this.frameworkBin() + '/' + relativePath
		return this.project.file(absolutePath).getParent()
	}

	private int compileTest(String srcModulePath, String testModulePath, String testName) {
		String precompiledTestPath = this.frameworkBin() + '/' + testName
		ProcessBuilder processBuilder = new ProcessBuilder('cobc', '-x', precompiledTestPath)
		def modulePath = this.frameworkBinModuleOf(testName)
		processBuilder.directory(new File(modulePath))
		def logPath = modulePath+ '/' + this.getFileName(testName) + '_' + 'TESTCOMPILE.LOG'
		processBuilder.redirectOutput(new File(logPath))
		def env = processBuilder.environment()
		String cobCopyEnvValue = srcModulePath + ':' + testModulePath
		env.put('COBCOPY', cobCopyEnvValue)
		logger.info('Compiling precompiled test')
		logger.info('Module path: ' + modulePath)
		logger.info('Log-path: ' + logPath)
		logger.info('Precompiled test path: ' + precompiledTestPath)
		logger.info('ENV: ' + env)
		def process = processBuilder.start()
		process.waitFor()
		return process.exitValue()
	}

	private String frameworkBin() {
		return this.configuration.absoluteUnitTestFramworkPath(this.project, this.getClass().getSimpleName())
	}

	private int preprocessTest(String mainFile, String testFile, String testConfig) {
		String zutzcpcPath = this.frameworkBin() + '/' + this.getFileName(MAIN_FRAMEWORK_PROGRAMM)
		ProcessBuilder processBuilder = new ProcessBuilder(zutzcpcPath)

		def env = processBuilder.environment()
		env.put('SRCPRG', this.configuration.absoluteSrcMainModulePath(this.project)+ '/'+ mainFile)
		env.put('TESTPRG', this.frameworkBin() + '/' + testFile)
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

		processBuilder.redirectOutput(new File(this.frameworkBinModuleOf(mainFile) + '/' + this.getFileName(testFile) + '_' + 'PRECOMPILER.LOG'))
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
