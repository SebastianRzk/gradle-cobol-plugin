package de.sebastianruziczka

import org.gradle.api.file.FileTree

import de.sebastianruziczka.compiler.api.CompilerBuilder
import de.sebastianruziczka.compiler.gnucobol.GnuCompilerBuilder

class CobolExtension {
	String srcFileType = '.cbl'
	String srcMain = ''

	String unittestPostfix = 'UT'
	String unittestCodeCoverage = false

	String integrationtestPostfix = 'IT'
	String integrationtestCodeCoverage = false


	String srcMainPath = 'src/main/cobol'
	String buildPath = 'build'
	String binMainPath = 'build/bin/main/cobol'
	String resMainPath = 'res/main/cobol'
	String srcTestPath = 'src/test/cobol'
	String resIntegrationTest = 'res/integrationtest/cobol'
	String incrementsBuildPath = 'build/incremental/'

	def multiCompileTargets = []

	String fileFormat = 'fixed'

	String terminal = 'current'
	String customTerminal = ''
	int terminalRows = 43
	int terminalColumns = 80

	Closure<File> projectFileResolver = null
	Closure<FileTree> projectFileTreeResolver = null

	CompilerBuilder compiler = new GnuCompilerBuilder()

	String compilerLogLevel = 'FINE'


	private String groovyPattern = '**/*'

	String filetypePattern(){
		this.groovyPattern + this.srcFileType
	}

	String unitTestFileTypePattern() {
		return this.groovyPattern + this.unittestPostfix + this.srcFileType
	}

	String integrationTestFileTypePattern() {
		return this.groovyPattern + this.integrationtestPostfix + this.srcFileType
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

	String absoluteBinMainModule(String sourceFile){
		if (!sourceFile.endsWith(this.srcFileType)) {
			sourceFile = sourceFile + this.srcFileType
		}
		return this.projectFileResolver(this.binMainPath + '/' + sourceFile).getParent()
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

	String debugFileType = '.so'
	String absoluteDebugMainPath(String name) {
		return this.absoluteBinMainPath(name) + this.debugFileType
	}

	String absoluteSrcTestPath() {
		return this.projectFileResolver(this.srcTestPath).absolutePath
	}

	String absoluteUnitTestFrameworkPath(String frameWorkName) {
		return this.projectFileResolver(this.buildPath).absolutePath + '/' + frameWorkName
	}

	org.gradle.api.tasks.util.PatternFilterable sourceTree() {
		return this.projectFileTreeResolver(this.srcMainPath).include(this.filetypePattern())
	}

	org.gradle.api.tasks.util.PatternFilterable unitTestTree() {
		return this.projectFileTreeResolver(this.srcTestPath).include(this.unitTestFileTypePattern())
	}

	org.gradle.api.tasks.util.PatternFilterable integrationTestTree() {
		return this.projectFileTreeResolver(this.srcTestPath).include(this.integrationTestFileTypePattern())
	}
}
