package de.sebastianruziczka


import org.gradle.api.*
import org.gradle.api.tasks.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.sebastianruziczka.buildcycle.CobolCompile
import de.sebastianruziczka.buildcycle.CobolConfiguration
import de.sebastianruziczka.buildcycle.CobolRun
import de.sebastianruziczka.cobolunit.CobolTestPair
import de.sebastianruziczka.cobolunit.CobolUnit

class Cobol implements Plugin<Project> {

	void apply(Project project) {
		Logger logger = LoggerFactory.getLogger('cobolCompile')
		def conf = project.extensions.create('cobol', CobolExtension)

		new CobolConfiguration().apply(project, conf)
		new CobolCompile().apply(project, conf)
		new CobolRun().apply(project, conf)

		project.task ('cobolClean', type: Delete){
			doFirst {
				delete project.file(conf.binMainPath).absolutePath
			}
		}

		project.task ('cobolUnit'){
			doLast {
				def testTree = project.fileTree(conf.srcTestPath).include(conf.unitTestFileTypePattern())
				def allTests = []

				testTree.each { File file ->
					allTests << file.absolutePath
				}

				def srcTree = project.fileTree(conf.srcMainPath).include(conf.filetypePattern())
				def allSrc = []
				srcTree.each { File file ->
					allSrc << file.absolutePath
				}


				def cobolTestPairs = []
				int unitTestFileEndingChars = conf.unitTestFileTypePattern().length() - '**/*'.length()
				allTests.each {
					int lastNameIndex = it.length() - unitTestFileEndingChars
					int firstNameIndex = conf.absoluteSrcTestPath(project).length()

					String moduleName = it.substring(firstNameIndex +1,lastNameIndex)

					String expectedSrcModulePath = project.file(conf.srcMainPath + '/' + moduleName + conf.srcFileType).absolutePath
					if (allSrc.contains(expectedSrcModulePath)) {
						allSrc.remove(expectedSrcModulePath)
						cobolTestPairs << new CobolTestPair(moduleName + conf.srcFileType, it.substring(firstNameIndex + 1))
					}
				}

				if (cobolTestPairs.size()== 0) {
					logger.warn('NO TESTS FOUND')
					return
				}
				logger.info('Number of Src<>Test pairs found: ' + cobolTestPairs.size())
				CobolUnit cobolUnit = new CobolUnit();
				cobolUnit.configure(conf, project);
				cobolUnit.prepare();
				cobolTestPairs.each{
					cobolUnit.test(it.srcFile(), it.testFile())
				}
			}
		}

		project.task ('cobolCheck', dependsOn: [
			'cobolConfiguration',
			'cobolCompile'
		]){ doLast { println 'check finished'
			} }
	}
}
