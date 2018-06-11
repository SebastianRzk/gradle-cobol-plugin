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

	public String getModulePath(CobolCodeType type) {
		if (type  == CobolCodeType.test) {
			return this.mainFileModule + this.configuration.unittestPostfix + this.configuration.srcFileType
		}else if (type == CobolCodeType.source) {
			return this.mainFileModule + this.configuration.srcFileType
		}
		return null
	}

	public String getAbsolutePath(CobolCodeType type) {
		return this.configuration.projectFileResolver(this.getModulePath(type))
	}

	public void setMeta(String key, String value) {
		this.meta.put(key, value)
	}

	public String getMeta(String key) {
		return this.meta.get(key)
	}
}
