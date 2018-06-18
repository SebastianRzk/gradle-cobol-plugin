package de.sebastianruziczka.api

import de.sebastianruziczka.CobolExtension

class CobolSourceFile {
	private CobolExtension configuration
	private String mainFileModule
	private Map<String, String> meta = new HashMap<>()

	public CobolSourceFile(CobolExtension configuration, String mainFileModule) {
		this.configuration = configuration

		if (mainFileModule.endsWith(this.configuration.srcFileType)) {
			this.mainFileModule = mainFileModule.replaceAll(this.configuration.srcFileType, '')
		}else {
			this.mainFileModule = mainFileModule
		}
	}

	public String getRelativePath(CobolCodeType type) {
		if (type  == CobolCodeType.unit_test) {
			return this.configuration.srcTestPath + '/' + this.mainFileModule + this.configuration.unittestPostfix + this.configuration.srcFileType
		}else if (type == CobolCodeType.source) {
			return this.configuration.srcMainPath + '/' + this.mainFileModule + this.configuration.srcFileType
		}else if (type == CobolCodeType.integration_test) {
			return this.configuration.srcTestPath + '/' + this.mainFileModule + this.configuration.integrationtestPostfix + this.configuration.srcFileType
		}else if (type == CobolCodeType.integration_test_ressources) {
			return this.configuration.resIntegrationTest + '/' + this.mainFileModule
		}
		return null
	}

	public String getAbsoluteModulePath(CobolCodeType type) {
		return this.configuration.projectFileResolver(this.getAbsolutePath(type)).getParent()
	}

	public String baseFileName() {
		return new File(this.mainFileModule).getName()
	}

	public String getAbsolutePath(CobolCodeType type) {
		return this.configuration.projectFileResolver(this.getRelativePath(type))
	}

	public void setMeta(String key, String value) {
		this.meta.put(key, value)
	}

	public String getMeta(String key) {
		return this.meta.get(key)
	}
}
