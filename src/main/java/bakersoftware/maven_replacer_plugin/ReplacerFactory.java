package bakersoftware.maven_replacer_plugin;

import org.apache.maven.plugin.logging.Log;

import bakersoftware.maven_replacer_plugin.file.FileUtils;

public class ReplacerFactory {
	private final Log log;
	private final FileUtils fileUtils;
	private final TokenReplacer tokenReplacer;

	public ReplacerFactory(Log log, FileUtils fileUtils, TokenReplacer tokenReplacer) {
		this.log = log;
		this.fileUtils = fileUtils;
		this.tokenReplacer = tokenReplacer;

	}

	public Replacer create() {
		return new Replacer(log, fileUtils, tokenReplacer);
	}

}
