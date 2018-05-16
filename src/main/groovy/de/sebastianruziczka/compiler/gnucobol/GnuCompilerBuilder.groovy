package de.sebastianruziczka.compiler.gnucobol

import de.sebastianruziczka.compiler.api.CompileJob
import de.sebastianruziczka.compiler.api.CompileStandard
import de.sebastianruziczka.compiler.api.CompilerBuilder
import de.sebastianruziczka.compiler.api.ExecutableCompilerBuilder
import de.sebastianruziczka.process.ProcessWrapper

class GnuCompilerBuilder implements CompilerBuilder {

	@Override
	public ExecutableCompilerBuilder buildExecutable() {
		return new GnuExecutableCompilerBuilder()
	}

	@Override
	public String getBaseCompilerCommand() {
		return GnuCompileJob.COBC
	}
}

class GnuExecutableCompilerBuilder implements ExecutableCompilerBuilder {

	private CompileStandard standard = CompileStandard.none
	private ArrayList<String> includePaths = new ArrayList<String>()
	private ArrayList<String> dependencyPaths = new ArrayList<String>()

	@Override
	public ExecutableCompilerBuilder setCompileStandard(CompileStandard standard) {
		GnuExecutableCompilerBuilder copy = new GnuExecutableCompilerBuilder()
		copy.standard = standard
		copy.dependencyPaths = this.dependencyPaths
		copy.includePaths = this.includePaths

		return copy
	}

	@Override
	public ExecutableCompilerBuilder addIncludePath(String path) {
		GnuExecutableCompilerBuilder copy = new GnuExecutableCompilerBuilder()
		copy.standard = this.standard
		copy.dependencyPaths = this.dependencyPaths
		copy.includePaths = this.includePaths
		copy.includePaths.add(path)

		return copy
	}

	@Override
	public CompileJob setTargetAndBuild(String targetPath) {
		return new GnuCompileJob(this.standard, this.includePaths, this.dependencyPaths, targetPath)
	}

	@Override
	public ExecutableCompilerBuilder addDependencyPath(String path) {
		GnuExecutableCompilerBuilder copy = new GnuExecutableCompilerBuilder()
		copy.standard = this.standard
		copy.dependencyPaths = this.dependencyPaths
		copy.dependencyPaths.add(path)
		copy.includePaths = this.includePaths
		return copy
	}

	@Override
	public ExecutableCompilerBuilder addDependencyPaths(ArrayList<String> list) {
		GnuExecutableCompilerBuilder copy = new GnuExecutableCompilerBuilder()
		copy.standard = this.standard
		copy.dependencyPaths = this.dependencyPaths

		for(String path: list){
			copy.dependencyPaths.add(path)
		}

		copy.includePaths = this.includePaths
		return copy
	}
}

class GnuCompileJob implements CompileJob  {

	static final String COBC = 'cobc'
	private CompileStandard standard = CompileStandard.none
	private ArrayList<String> dependencyPaths
	private ArrayList<String> includePaths
	private ArrayList<String> additionalOptions = new ArrayList<>()

	private String target
	private String outputPath = null

	public GnuCompileJob(CompileStandard standard, ArrayList<String> includePaths, ArrayList<String> dependencyPaths, String target){
		this.standard = standard
		this.includePaths = includePaths
		this.target = target
		this.dependencyPaths = dependencyPaths
	}

	@Override
	public CompileJob setExecutableDestinationPath(String outputPath) {
		GnuCompileJob copy = new GnuCompileJob(this.standard, this.includePaths, this.dependencyPaths, this.target)
		copy.outputPath = outputPath
		copy.additionalOptions = this.additionalOptions
		return copy
	}

	@Override
	public int execute(String processName) {
		def args = [COBC]
		args << '-v'
		args << '-x'

		if (this.standard != CompileStandard.none) {
			args << '-std=' + this.standard.toString()
		}

		for (String dependency : this.includePaths) {
			args << '-I'
			args << dependency
		}

		String logPath = this.target + '_COMPILE.LOG'
		if (this.outputPath) {
			args << '-o'
			args << this.outputPath
			logPath = this.outputPath + '_COMPILE.LOG'
		}

		if ( this.additionalOptions.size() > 0) {
			for (String option : this.additionalOptions) {
				args <<  '-' + option
			}
		}

		args << this.target

		if (this.dependencyPaths) {
			args += dependencyPaths
		}

		ProcessBuilder processBuilder = new ProcessBuilder(args)
		File folder = new File(this.target).getParentFile()
		processBuilder.directory(folder)
		ProcessWrapper processWrapper = new ProcessWrapper(processBuilder, processName, logPath)
		return processWrapper.exec()
	}



	@Override
	public CompileJob addAdditionalOption(String option) {
		GnuCompileJob copy = new GnuCompileJob(this.standard, this.includePaths, this.dependencyPaths, this.target)
		copy.outputPath = this.outputPath
		copy.additionalOptions = this.additionalOptions
		copy.additionalOptions.add(option)
		return copy
	}
}
