package de.sebastianruziczka.compiler.gnucobol

import de.sebastianruziczka.CobolExtension
import de.sebastianruziczka.compiler.api.CompileJob
import de.sebastianruziczka.compiler.api.CompileStandard
import de.sebastianruziczka.compiler.api.CompilerBuilder
import de.sebastianruziczka.compiler.api.DebugCompilerBuilder
import de.sebastianruziczka.process.ProcessWrapper


class GnuDebugCompilerBuilder implements DebugCompilerBuilder {

	private CobolExtension configuration
	private CompileStandard compileStandard = CompileStandard.none
	private List<String> includePaths = new ArrayList<>()
	private CompilerBuilder compilerBuilder;

	public GnuDebugCompilerBuilder(CobolExtension configuration, CompilerBuilder compilerBuilder) {
		this.configuration = configuration
		this.compilerBuilder = compilerBuilder
	}

	private GnuDebugCompilerBuilder(CobolExtension configuration, CompileStandard compileStandard, List<String> includePaths, CompilerBuilder compilerBuilder) {
		this.configuration = configuration
		this.compileStandard = compileStandard
		this.includePaths = includePaths
		this.compilerBuilder = compilerBuilder;
	}

	@Override
	public DebugCompilerBuilder setCompileStandard(CompileStandard standard) {
		return new GnuDebugCompilerBuilder(this.configuration, compileStandard, this.includePaths, this.compilerBuilder)
	}

	@Override
	public DebugCompilerBuilder addIncludePath(String path) {
		List<String> includePaths = new LinkedList(this.includePaths)
		includePaths.add(path)
		return new GnuDebugCompilerBuilder(this.configuration, this.compileStandard, includePaths, this.compilerBuilder)
	}

	@Override
	public CompileJob setTargetAndBuild(String targetPath) {
		return new GnuDebugCompileJob(targetPath, this.includePaths, this.compileStandard, this.configuration, this.compilerBuilder)
	}
}

class GnuDebugCompileJob implements CompileJob {

	private String target
	private List<String> includePaths
	private CompileStandard compileStandard
	private CobolExtension configuration
	private List<String> additionalOptions = new ArrayList()
	private String destinationPath = null
	private CompilerBuilder compilerBuilder

	public GnuDebugCompileJob(String target, List<String> includePaths, CompileStandard compileStandard, CobolExtension configuration, CompilerBuilder compilerBuilder) {
		this.target = target
		this.includePaths = includePaths
		this.compileStandard = compileStandard
		this.configuration = configuration
		this.compilerBuilder = compilerBuilder
	}

	private GnuDebugCompileJob(String target, List<String> includePaths, CompileStandard compileStandard, CobolExtension configuration, List<String> additionalOptions, String destinationPath, CompilerBuilder compilerBuilder) {
		this.target = target
		this.includePaths = includePaths
		this.compileStandard = compileStandard
		this.configuration = configuration
		this.additionalOptions = additionalOptions
		this.destinationPath = destinationPath
		this.compilerBuilder = compilerBuilder
	}



	@Override
	public CompileJob setExecutableDestinationPath(String outputPath) {
		return new GnuDebugCompileJob(this.target, this.includePaths, this.compileStandard, this.configuration, this.additionalOptions, outputPath, this.compilerBuilder)
	}

	@Override
	public CompileJob addAdditionalOption(String option) {
		List<String> additionalOptions = new LinkedList(this.additionalOptions)
		additionalOptions.add(option)
		return new GnuDebugCompileJob(this.target, this.includePaths, this.compileStandard, this.configuration, additionalOptions, this.destinationPath, this.compilerBuilder)
	}

	@Override
	public int execute(String processName) {
		def args = [this.compilerBuilder.getBaseCompilerCommand()]

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