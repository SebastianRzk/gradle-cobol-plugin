package de.sebastianruziczka

import org.gradle.api.Project

class CobolExtension {
	String srcFileType = '.cbl'
	String srcMain = ''

	String unittestPostfix = 'UT'

	String srcMainPath = 'src/main/cobol'
	String binMainPath = 'build/bin/main/cobol'
	String resMainPath = 'res/main/cobol'
	String srcTestPath = 'src/test/cobol'

	def multiMainTargets = []

	String fileFormat = 'fixed'

	String terminal = 'xterm'
	String customTerminal = ''
	int terminalRows = 80
	int terminalColumns = 43


	String filetypePattern(){
		'**/*' + this.srcFileType
	}

	String unitTestFileTypePattern() {
		return '**/*' + this.unittestPostfix + srcFileType
	}

	String absoluteSrcMainModulePath(Project project){
		return this.absoluteSrcMainModulePath(project, this.srcMain)
	}

	String absoluteSrcMainModulePath(Project project, String sourceFile){
		if (!sourceFile.endsWith(this.srcFileType)) {
			sourceFile = sourceFile + this.srcFileType
		}
		return project.file(srcMainPath + '/' + sourceFile).getParent()
	}

	String absoluteSrcMainPath(Project project){
		return this.absoluteSrcMainPath(project, this.srcMain)
	}

	String absoluteSrcMainPath(Project project, String mainFile){
		if (!mainFile.endsWith(this.srcFileType)) {
			mainFile = mainFile + this.srcFileType
		}
		return project.file(srcMainPath + '/' + mainFile).absolutePath
	}

	String absoluteBinMainPath(Project project){
		return project.file(binMainPath + '/' +  srcMain).absolutePath
	}

	String absoluteBinMainPath(Project project, String name){
		if (name.endsWith(this.srcFileType)) {
			name = name.substring(0, name.length() - this.srcFileType.length())
		}
		return project.file(binMainPath + '/' +  name).absolutePath
	}


	String absoluteSrcTestPath(Project project) {
		return project.file(this.srcTestPath).absolutePath
	}


	String absoluteUnitTestFramworkPath(Project project, String frameWorkName) {
		return project.file(this.binMainPath).absolutePath + '/' + frameWorkName
	}
}
