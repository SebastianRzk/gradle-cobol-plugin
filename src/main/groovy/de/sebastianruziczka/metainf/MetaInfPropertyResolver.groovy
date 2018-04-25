package de.sebastianruziczka.metainf

class MetaInfPropertyResolver {
	private static final String implementationTitleKey = "Implementation-Title"
	private Properties properties = new Properties()
	public MetaInfPropertyResolver(String implementationTitle) {
		def allMetaInfFiles =  getClass().getClassLoader().getResources("META-INF/MANIFEST.MF")
		while (allMetaInfFiles.hasMoreElements()) {
			Properties manifest = new Properties()
			manifest.load(allMetaInfFiles.nextElement().openStream());
			if (manifest.keySet().contains(implementationTitleKey) && manifest.get(implementationTitleKey) == implementationTitle) {
				this.properties = manifest
			}
		}
	}

	public Optional<String> get(String key){
		return Optional.ofNullable(this.properties.get(key))
	}
}
