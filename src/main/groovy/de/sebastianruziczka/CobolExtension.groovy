package de.sebastianruziczka

import de.sebastianruziczka.compiler.api.CompilerBuilder
import de.sebastianruziczka.compiler.gnucobol.GnuCompilerBuilder

class CobolExtension {
	String srcFileType = '.cbl'
	String srcMain = ''

	String unittestPostfix = 'UT'

	String srcMainPath = 'src/main/cobol'
	String binMainPath = 'build/bin/main/cobol'
	String resMainPath = 'res/main/cobol'
	String srcTestPath = 'src/test/cobol'
	String incrementsBuildPath = 'build/incremental/'

	def multiCompileTargets = []

	String fileFormat = 'fixed'

	String terminal = 'xterm'
	String customTerminal = ''
	int terminalRows = 43
	int terminalColumns = 80

	Closure<File> projectFileResolver = null

	CompilerBuilder compiler = new GnuCompilerBuilder()

	String compilerLogLevel = 'FINE'

	String filetypePattern(){
		'**/*' + this.srcFileType
	}

	String unitTestFileTypePattern() {
		return '**/*' + this.unittestPostfix + srcFileType
	}

	String absoluteSrcMainModulePath(){
		return this.absoluteSrcMainModulePath(this.srcMain)
	}

	String absoluteSrcMainModulePath(String sourceFile){
		if (!sourceFile.endsWith(this.srcFileType)) {
			sourceFile = sourceFile + this.srcFileType
		}
		return this.projectFileResolver(srcMainPath + '/' + sourceFile).getParent()
	}

	String absoluteSrcMainPath(){
		return this.absoluteSrcMainPath(this.srcMain)
	}

	String absoluteSrcMainPath(String mainFile){
		if (!mainFile.endsWith(this.srcFileType)) {
			mainFile = mainFile + this.srcFileType
		}
		return this.projectFileResolver(srcMainPath + '/' + mainFile).absolutePath
	}

	String absoluteBinMainPath(){
		return this.projectFileResolver(binMainPath + '/' +  srcMain).absolutePath
	}

	String absoluteBinMainPath(String name){
		if (name.endsWith(this.srcFileType)) {
			name = name.substring(0, name.length() - this.srcFileType.length())
		}
		return this.projectFileResolver(binMainPath + '/' +  name).absolutePath
	}


	String absoluteSrcTestPath() {
		return this.projectFileResolver(this.srcTestPath).absolutePath
	}


	String absoluteUnitTestFrameworkPath(String frameWorkName) {
		return this.projectFileResolver(this.binMainPath).absolutePath + '/' + frameWorkName
	}
}
