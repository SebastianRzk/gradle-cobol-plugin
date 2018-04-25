class GradlePropertyFilter {

	private def map
	public GradlePropertyFilter(def map) {
		this.map = map
	}

	public String filter(String line) {
		for(String key : this.map.keySet()) {
			line = line.replaceAll(key, this.map.get(key))
		}
		return line
	}
}
