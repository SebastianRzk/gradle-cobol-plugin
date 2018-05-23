package de.sebastianruziczka.compiler.api

import de.sebastianruziczka.CobolExtension

interface CompilerBuilder {

	String getBaseCompilerCommand()

	ExecutableCompilerBuilder buildExecutable(CobolExtension configuration)

	DebugCompilerBuilder buildDebug(CobolExtension configuration)
}

interface CompileJob {

	CompileJob setExecutableDestinationPath(String outputPath)

	CompileJob addAdditionalOption(String option)

	int execute(String processName)
}


enum CompileStandard {
	none, ibm
}
