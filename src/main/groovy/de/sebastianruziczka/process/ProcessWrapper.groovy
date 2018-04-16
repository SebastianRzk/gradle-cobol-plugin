package de.sebastianruziczka.process

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ProcessWrapper {
	private Logger logger;

	private ProcessBuilder processBuilder
	private String taskName
	private String logFilePath

	public ProcessWrapper(ProcessBuilder processBuilder, String taskName, String logFilePath) {
		this.processBuilder = processBuilder
		this.taskName = taskName
		this.logFilePath = logFilePath
		this.logger = LoggerFactory.getLogger('PocessWrapper for ' + taskName)
	}

	public int exec() {
		this.logger.info('ProcessWrapper starting process ' + this.taskName)
		File outputFile  = new File(this.logFilePath)
		this.processBuilder.redirectOutput(outputFile)
		this.logger.info('redirect process output to ' + this.logFilePath)

		Process process = this.processBuilder.start()
		process.waitFor()
		String output = new File(this.logFilePath).text
		if (process.exitValue() != 0) {
			logger.error('Process ' + this.taskName + ' ended unexpected!')
			logger.error('Error code: ' + process.exitValue())
			logger.error(output)
			throw new ProcessFailedException('Process ' + this.taskName + ' ended unexpected!')
		}
		logger.info(output)
		return process.exitValue()
	}
}
