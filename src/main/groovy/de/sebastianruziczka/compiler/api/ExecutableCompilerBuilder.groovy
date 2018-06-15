package de.sebastianruziczka.compiler.api

interface ExecutableCompilerBuilder{

	ExecutableCompilerBuilder setCompileStandard(CompileStandard standard)

	ExecutableCompilerBuilder addIncludePath(String path)

	ExecutableCompilerBuilder addDependencyPath(String path)

	ExecutableCompilerBuilder addDependencyPaths(List<String> list)

	CompileJob setTargetAndBuild(String targetPath)
}