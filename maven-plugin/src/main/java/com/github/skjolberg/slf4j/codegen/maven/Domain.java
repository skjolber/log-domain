package com.github.skjolberg.slf4j.codegen.maven;

import org.apache.maven.plugins.annotations.Parameter;

public class Domain {
	
	@Parameter(property = "types")
	private Types types;

	@Parameter(property = "path")
	private String path;
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return "Domain [path=" + path + "]";
	}
	
	public Types getTypes() {
		return types;
	}
	
	public void setTypes(Types types) {
		this.types = types;
	}
}
