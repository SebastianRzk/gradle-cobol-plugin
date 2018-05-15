package de.sebastianruziczka.buildcycle

import org.gradle.api.Project
import org.gradle.api.tasks.*
import org.gradle.util.GradleVersion

import de.sebastianruziczka.CobolExtension
import de.sebastianruziczka.metainf.MetaInfPropertyResolver

class CobolConfiguration {
	void apply(Project project, CobolExtension conf) {

		project.task ('cobolPluginVersion'){
			group 'COBOL Configuration'
			description 'Returns version of this plugin'
			doLast {
				MetaInfPropertyResolver resolver = new MetaInfPropertyResolver("gradle-cobol-plugin")
				println '\tPlugin-Version: ' +  resolver.get('Implementation-Version').orElse('No version found!')
				println '\tPlugin-Build-Date: '+ resolver.get('Build-Date').orElse('No date found!')
			}
		}

		project.task ('cobolCompilerVersion', type: Exec){
			group 'COBOL Configuration'
			description 'Returns version of used GnuCobolCompiler'
			commandLine = 'cobc'
			args = ['--version']
		}

		project.task ('cobolGradleVersion'){
			group 'COBOL Configuration'
			description 'returns version of used gradle instance'
			doLast {
				GradleVersion gradleVersion = GradleVersion.current()
				println gradleVersion.properties.collect{ '\t'+it }.join('\n')
			}
		}

		project.task ('cobolGradleConfiguration') {
			group 'COBOL Configuration'
			description 'Returns project configuration'
			doFirst {
				println conf.properties.collect{ '\t'+it }.join('\n')
				println '\t###Computed paths:###'
				println '\tabsoluteSrcMainModulePath: ' + conf.absoluteSrcMainModulePath()
				println '\tabsoluteSrcMainPath: ' + conf.absoluteSrcMainPath()
				println '\tabsoluteBinMainPath: ' + conf.absoluteBinMainPath()
			}
		}

		project.task ('cobolConfiguration', dependsOn: [
			'cobolGradleVersion',
			'cobolCompilerVersion',
			'cobolPluginVersion',
			'cobolGradleConfiguration',
			'testUnitCobolConfiguration'
		]){
			group 'COBOL Configuration'
			description 'Returns full configuration of the project and plugin and their versions'
			doFirst { println 'DONE' }
		}
	}
}
