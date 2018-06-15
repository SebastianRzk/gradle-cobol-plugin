package de.sebastianruziczka.buildcycle.test

import java.lang.reflect.Constructor

import org.gradle.api.Project
import org.reflections.Reflections
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.sebastianruziczka.CobolExtension

class FrameworkResolver {
	Logger logger = LoggerFactory.getLogger('FramworkResolver')
	private String basicPackage;
	public FrameworkResolver(String basicPackage) {
		this.basicPackage = basicPackage
	}


	public <T> Collection<T> resolve(Class<?> annotation,Class<T> instancePrototype, CobolExtension configuration, Project project){
		logger.info('Resolving new frameworks with annotation: ' + annotation)
		def allUnitTestFrameworks = []
		try {
			Reflections reflections = new Reflections(this.basicPackage);
			Set<Class<? extends T>> cobolUnitFrameworks = reflections.getTypesAnnotatedWith(annotation)
			cobolUnitFrameworks.each{
				logger.info('\tDetected frameworks: ' + cobolUnitFrameworks)
				Constructor constructor = it.getDeclaredConstructors0(true)[0]
				def cobolUnitInstance = constructor.newInstance()
				allUnitTestFrameworks << cobolUnitInstance
				cobolUnitInstance.configure(configuration, project);
			}
		} catch (Throwable t) {
			logger.error('Failed while searching for cobol unit frameworks', t)
			logger.error(t.message)
			t.printStackTrace()
		}
		return allUnitTestFrameworks
	}
}
