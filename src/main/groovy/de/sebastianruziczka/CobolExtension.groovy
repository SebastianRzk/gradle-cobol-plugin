package de.sebastianruziczka

import org.gradle.api.Project

class CobolExtension {
	String srcFileType = '.cbl'
	String srcMain = ''

	String srcMainPath = 'src/main/cobol'
	String binMainPath = 'build/bin/main/cobol'
	String resMainPath = 'res/main/cobol'
	String srcTestPath = 'src/test/cobol'

	String fileFormat = 'fixed'

	String terminal = 'xterm'
	String customTerminal = ''


	def filetypePattern(){
		'**/*' + srcFileType
	}

	def absoluteSrcMainModulePath(Project project){
		return project.file(srcMainPath + '/' + srcMain + srcFileType).getParent()
	}

	def absoluteSrcMainPath(Project project){
		return project.file(srcMainPath + '/' + srcMain + srcFileType).absolutePath
	}

	def absoluteBinMainPath(Project project){
		return project.file(binMainPath + '/' +  srcMain).absolutePath
	}

	def absoluteSrcTestPath(Project project) {
		return project.file(this.srcTestPath).absolutePath
	}


	def absoluteUnitTestFramworkPath(Project project, String frameWorkName) {
		return project.file(this.binMainPath).absolutePath + '/' + frameWorkName
	}
}
