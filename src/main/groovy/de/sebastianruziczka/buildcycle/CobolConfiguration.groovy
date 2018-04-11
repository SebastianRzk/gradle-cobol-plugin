package de.sebastianruziczka.buildcycle

import org.gradle.api.Project
import org.gradle.api.tasks.*
import org.gradle.util.GradleVersion

import de.sebastianruziczka.CobolExtension

class CobolConfiguration {
	void apply(Project project, CobolExtension conf) {

		project.task ('cobolPluginVersion'){
			doLast {
				URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
				URL manifestUrl = urlClassLoader.findResource("META-INF/MANIFEST.MF");
				InputStream is = manifestUrl.openStream();
				Properties props = new Properties();
				props.load(is);
				is.close();
				println '\tPlugin-Version: ' +  props.get('Implementation-Version')
				println '\tPlugin-Build-Date: '+ props.get('Build-Date')
			}
		}

		project.task ('cobolCompilerVersion', type: Exec){
			commandLine = 'cobc'
			args = ['--version']
		}

		project.task ('cobolGradleVersion'){
			doLast {
				GradleVersion gradleVersion = GradleVersion.current()
				println gradleVersion.properties.collect{ '\t'+it }.join('\n')
			}
		}

		project.task ('cobolGradleConfiguration') {
			doFirst {
				println conf.properties.collect{ '\t'+it }.join('\n')
				println '\t###Computed paths:###'
				println '\tabsoluteSrcMainModulePath: ' + conf.absoluteSrcMainModulePath(project)
				println '\tabsoluteSrcMainPath: ' + conf.absoluteSrcMainPath(project)
				println '\tabsoluteBinMainPath: ' + conf.absoluteBinMainPath(project)
			}
		}

		project.task ('cobolConfiguration', dependsOn: [
			'cobolGradleVersion',
			'cobolCompilerVersion',
			'cobolPluginVersion',
			'cobolGradleConfiguration'
		]){ doFirst { println 'DONE'
			} }
	}
}
