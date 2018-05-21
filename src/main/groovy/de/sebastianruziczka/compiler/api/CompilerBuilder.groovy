package de.sebastianruziczka.compiler.api

import de.sebastianruziczka.CobolExtension

interface CompilerBuilder {

	String getBaseCompilerCommand()

	ExecutableCompilerBuilder buildExecutable(CobolExtension configuration)

	Stage1CompilerBuilder buildStage1(CobolExtension configuration)
}

interface Stage1CompilerBuilder {
	Stage1CompilerBuilder setCompileStandard(CompileStandard standard)

	Stage1CompilerBuilder addIncludePath(String path)

	CompileJob setTargetAndBuild(String targetPath)
}



interface ExecutableCompilerBuilder{

	ExecutableCompilerBuilder setCompileStandard(CompileStandard standard)

	ExecutableCompilerBuilder addIncludePath(String path)

	ExecutableCompilerBuilder addDependencyPath(String path)

	ExecutableCompilerBuilder addDependencyPaths(List<String> list)

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
