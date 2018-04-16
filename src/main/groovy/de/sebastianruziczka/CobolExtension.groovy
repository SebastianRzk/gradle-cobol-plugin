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

	String fileFormat = 'fixed'

	String terminal = 'xterm'
	String customTerminal = ''


	String filetypePattern(){
		'**/*' + this.srcFileType
	}

	String unitTestFileTypePattern() {
		return '**/*' + this.unittestPostfix + srcFileType
	}

	String absoluteSrcMainModulePath(Project project){
		return project.file(srcMainPath + '/' + srcMain + srcFileType).getParent()
	}

	String absoluteSrcMainPath(Project project){
		return project.file(srcMainPath + '/' + srcMain + srcFileType).absolutePath
	}

	String absoluteBinMainPath(Project project){
		return project.file(binMainPath + '/' +  srcMain).absolutePath
	}

	String absoluteSrcTestPath(Project project) {
		return project.file(this.srcTestPath).absolutePath
	}


	String absoluteUnitTestFramworkPath(Project project, String frameWorkName) {
		return project.file(this.binMainPath).absolutePath + '/' + frameWorkName
	}
}
