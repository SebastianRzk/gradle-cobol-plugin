package de.sebastianruziczka.compiler.api

interface CompilerBuilder {

	String getBaseCompilerCommand()

	ExecutableCompilerBuilder buildExecutable()
}


interface ExecutableCompilerBuilder{

	ExecutableCompilerBuilder setCompileStandard(CompileStandard standard)

	ExecutableCompilerBuilder addIncludePath(String path)

	ExecutableCompilerBuilder addDependencyPath(String path)

	ExecutableCompilerBuilder addDependencyPaths(ArrayList<String> list)

	CompileJob setTargetAndBuild(String targetPath)
}

interface CompileJob {

	CompileJob setExecutableDestinationPath(String outputPath)

	CompileJob addAdditionalOption(String option)

	int execute(String processName)
}


enum CompileStandard {
	none, ibm
}
