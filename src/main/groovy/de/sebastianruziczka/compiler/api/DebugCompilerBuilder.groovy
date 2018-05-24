package de.sebastianruziczka.compiler.api



interface DebugCompilerBuilder {
	DebugCompilerBuilder setCompileStandard(CompileStandard standard)

	DebugCompilerBuilder addIncludePath(String path)

	CompileJob setTargetAndBuild(String targetPath)
}

