package de.sebastianruziczka


import java.lang.reflect.Constructor

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.reflections.Reflections
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.sebastianruziczka.api.CobolUnitFrameworkProvider
import de.sebastianruziczka.buildcycle.CobolCompile
import de.sebastianruziczka.buildcycle.CobolConfiguration
import de.sebastianruziczka.buildcycle.CobolRun
import de.sebastianruziczka.buildcycle.test.CobolTestPair
import de.sebastianruziczka.buildcycle.test.TestResult

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
			def allUnitTestFrameworks = []
			try {
				Reflections reflections = new Reflections("de");
				Set<Class<? extends CobolUnitFrameworkProvider>> cobolUnitFrameworks = reflections.getTypesAnnotatedWith(CobolUnitFrameworkProvider.class)
				cobolUnitFrameworks.each{
					logger.info('Detected framworks: ' + cobolUnitFrameworks)
					Constructor constructor = it.getDeclaredConstructors0(true)[0]
					allUnitTestFrameworks << constructor.newInstance()
				}
			} catch (Throwable t) {
				logger.error('Failed while searching for cobol unit frameworks')
				logger.error(e)
			}

			doLast {
				if (allUnitTestFrameworks.isEmpty()) {
					logger.info("No cobol unit framework found.")
					logger.info("Make sure your framwork class:")
					logger.info("\t 1. ... is in the classpath of this plugin (via buildscript dependencies)")
					logger.info("\t 2. ... is in the package de.*")
					logger.info("\t 3. ... implements the interface de.sebastianruziczka.CobolUnitFramwork")
					logger.info("\t 4. ... is annotated with @CobolUnitFrameworkProvider")
					println 'No unittest framework found. Use --info for more information'
				}
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
				allUnitTestFrameworks.each{
					it.configure(conf, project);
					it.prepare();
				}

				allUnitTestFrameworks.each{ framework ->
					println 'Starting Cobol-Unittest with framwork: ' + framework.getClass().getSimpleName()
					TestResult result = new TestResult()
					cobolTestPairs.each{
						result.addTest(framework.test(it.srcFile(), it.testFile()))
					}
					println 'Result: ' + result.successfullTests() + ' sucessfull tests, ' + result.failedTests() + ' tests failed'
				}
			}
		}

		project.task ('cobolCheck', dependsOn: [
			'cobolUnit',
			'cobolCompile',
			'cobolConfiguration'
		]){ doLast { println 'check finished'
			} }
	}
}
