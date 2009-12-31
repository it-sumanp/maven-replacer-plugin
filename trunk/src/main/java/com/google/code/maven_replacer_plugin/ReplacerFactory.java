package com.google.code.maven_replacer_plugin;

import com.google.code.maven_replacer_plugin.file.FileUtils;

public class ReplacerFactory {
	private final FileUtils fileUtils;
	private final TokenReplacer tokenReplacer;

	public ReplacerFactory(FileUtils fileUtils, TokenReplacer tokenReplacer) {
		this.fileUtils = fileUtils;
		this.tokenReplacer = tokenReplacer;

	}

	public Replacer create() {
		return new Replacer(fileUtils, tokenReplacer);
	}

}
