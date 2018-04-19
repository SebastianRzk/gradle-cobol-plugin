package de.sebastianruziczka.process

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ProcessWrapper {
	private Logger logger;

	private ProcessBuilder processBuilder
	private String taskName
	private String logFilePath
	private String processOutput = ""

	public ProcessWrapper(ProcessBuilder processBuilder, String taskName, String logFilePath) {
		this.processBuilder = processBuilder
		this.taskName = taskName
		this.logFilePath = logFilePath
		this.logger = LoggerFactory.getLogger('PocessWrapper for ' + taskName)
	}

	public int exec() {
		return exec(false)
	}

	public int exec(boolean ignoreExitCode) {
		this.logger.info('ProcessWrapper starting process ' + this.taskName)
		File outputFile  = new File(this.logFilePath)
		this.processBuilder.redirectOutput(outputFile)
		this.logger.info('redirect process output to ' + this.logFilePath)

		Process process = this.processBuilder.start()
		process.waitFor()
		this.processOutput = new File(this.logFilePath).text
		if (process.exitValue() != 0) {
			logger.error('Process ' + this.taskName + ' ended unexpected!')
			logger.error('Error code: ' + process.exitValue())
			logger.error(this.processOutput)
			if (!ignoreExitCode) {
				throw new ProcessFailedException('Process ' + this.taskName + ' ended unexpected!')
			}
		}
		logger.info(this.processOutput)
		return process.exitValue()
	}

	String processOutput() {
		return this.processOutput
	}
}
