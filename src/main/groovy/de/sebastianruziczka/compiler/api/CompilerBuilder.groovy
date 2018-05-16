package de.sebastianruziczka.compiler.api

import de.sebastianruziczka.CobolExtension

interface CompilerBuilder {

	String getBaseCompilerCommand()

	ExecutableCompilerBuilder buildExecutable(CobolExtension configuration)
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
