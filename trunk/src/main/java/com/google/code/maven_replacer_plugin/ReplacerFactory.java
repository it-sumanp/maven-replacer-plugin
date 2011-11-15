package com.google.code.maven_replacer_plugin;


public class ReplacerFactory {
	public Replacer create(Replacement replacement) {
		return new TokenReplacer();
	}

}
