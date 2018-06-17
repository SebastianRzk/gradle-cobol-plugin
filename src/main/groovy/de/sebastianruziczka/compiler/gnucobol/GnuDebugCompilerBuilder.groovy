package de.sebastianruziczka.compiler.gnucobol

import de.sebastianruziczka.CobolExtension
import de.sebastianruziczka.compiler.api.CompileJob
import de.sebastianruziczka.compiler.api.CompileStandard
import de.sebastianruziczka.compiler.api.DebugCompilerBuilder
import de.sebastianruziczka.process.ProcessWrapper


class GnuDebugCompilerBuilder implements DebugCompilerBuilder {

	private CobolExtension configuration
	private CompileStandard compileStandard = CompileStandard.none
	private List<String> includePaths = new ArrayList<>()

	public GnuDebugCompilerBuilder(CobolExtension configuration) {
		this.configuration = configuration
	}

	private GnuDebugCompilerBuilder(CobolExtension configuration, CompileStandard compileStandard, List<String> includePaths) {
		this.configuration = configuration
		this.compileStandard = compileStandard
		this.includePaths = includePaths
	}

	@Override
	public DebugCompilerBuilder setCompileStandard(CompileStandard standard) {
		return new GnuDebugCompilerBuilder(this.configuration, compileStandard, this.includePaths)
	}

	@Override
	public DebugCompilerBuilder addIncludePath(String path) {
		List<String> includePaths = new LinkedList(this.includePaths)
		includePaths.add(path)
		return new GnuDebugCompilerBuilder(this.configuration, this.compileStandard, includePaths)
	}

	@Override
	public CompileJob setTargetAndBuild(String targetPath) {
		return new GnuDebugCompileJob(targetPath, this.includePaths, this.compileStandard, this.configuration)
	}
}

class GnuDebugCompileJob implements CompileJob {

	static final String COBC = 'cobc'
	private String target
	private List<String> includePaths
	private CompileStandard compileStandard
	private CobolExtension configuration
	private List<String> additionalOptions = new ArrayList()
	private String destinationPath = null

	public GnuDebugCompileJob(String target, List<String> includePaths, CompileStandard compileStandard, CobolExtension configuration) {
		this.target = target
		this.includePaths = includePaths
		this.compileStandard = compileStandard
		this.configuration = configuration
	}

	private GnuDebugCompileJob(String target, List<String> includePaths, CompileStandard compileStandard, CobolExtension configuration, List<String> additionalOptions, String destinationPath) {
		this.target = target
		this.includePaths = includePaths
		this.compileStandard = compileStandard
		this.configuration = configuration
		this.additionalOptions = additionalOptions
		this.destinationPath = destinationPath
	}



	@Override
	public CompileJob setExecutableDestinationPath(String outputPath) {
		return new GnuDebugCompileJob(this.target, this.includePaths, this.compileStandard, this.configuration, this.additionalOptions, outputPath)
	}

	@Override
	public CompileJob addAdditionalOption(String option) {
		List<String> additionalOptions = new LinkedList(this.additionalOptions)
		additionalOptions.add(option)
		return new GnuDebugCompileJob(this.target, this.includePaths, this.compileStandard, this.configuration, additionalOptions, this.destinationPath)
	}

	@Override
	public int execute(String processName) {
		def args = [COBC]

		args << new GnuCobolLoglevelOptionResolver().resolve(this.configuration)

		if (this.compileStandard != CompileStandard.none) {
			args << '-std=' + this.compileStandard.toString()
		}

		for (String dependency : this.includePaths) {
			args << '-I'
			args << dependency
		}

		String logPath = this.target + '_COMPILE.LOG'
		if (this.destinationPath) {
			args << '-o'
			args << this.destinationPath
			logPath = this.destinationPath + '_COMPILE.LOG'
		}

		if ( this.additionalOptions.size() > 0) {
			for (String option : this.additionalOptions) {
				args <<  '-' + option
			}
		}

		args << this.target

		File folder = new File(this.target).getParentFile()
		ProcessWrapper processWrapper = new ProcessWrapper(args, folder, processName, logPath)
		return processWrapper.exec()
	}

	@Override
	public CompileJob addCodeCoverageOption() {
		return this.addAdditionalOption('debug').addAdditionalOption('ftraceall');
	}
}