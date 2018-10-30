package de.sebastianruziczka.compiler.gnucobol

import de.sebastianruziczka.CobolExtension
import de.sebastianruziczka.compiler.api.CompilerBuilder
import de.sebastianruziczka.compiler.api.ExecutableCompilerBuilder
import de.sebastianruziczka.compiler.api.DebugCompilerBuilder

class GnuCompilerBuilder implements CompilerBuilder {

	@Override
	public ExecutableCompilerBuilder buildExecutable(CobolExtension configuration) {
		return new GnuExecutableCompilerBuilder(configuration, this)
	}

	@Override
	public String getBaseCompilerCommand() {
		return 'cobc'
	}

	@Override
	public DebugCompilerBuilder buildDebug(CobolExtension configuration) {
		return new GnuDebugCompilerBuilder(configuration, this)
	}
}


class GnuCobolLoglevelOptionResolver {

	public String resolve(CobolExtension configuration) {
		if ('FINEST'.equals(configuration.compilerLogLevel.toUpperCase())) {
			return '-vvv'
		}else if ('FINER'.equals(configuration.compilerLogLevel.toUpperCase())) {
			return '-vv'
		}
		return '-v'
	}
}


