package bakersoftware.maven_replacer_plugin;

import java.io.IOException;

import org.apache.maven.plugin.logging.Log;

import bakersoftware.maven_replacer_plugin.file.FileStreamFactory;
import bakersoftware.maven_replacer_plugin.file.FileUtils;

public class Replacer {
	private final FileUtils fileUtils;
	private final TokenReplacer tokenReplacer;
	private final Log log;

	public Replacer(Log log, FileUtils fileUtils, TokenReplacer tokenReplacer) {
		this.log = log;
		this.fileUtils = fileUtils;
		this.tokenReplacer = tokenReplacer;
	}

	public void replace(ReplacerContext context, boolean ignoreMissingFile, boolean regex)
			throws IOException {
		if (context.getToken() == null && context.getTokenFile() == null) {
			throw new IllegalArgumentException("Token or token file required");
		}

		if (ignoreMissingFile && !fileUtils.fileExists(context.getFile())) {
			log.info("Ignoring missing file");
			return;
		}

		String token = context.getToken();
		if (token == null) {
			token = fileUtils.readFile(context.getTokenFile()).trim();
		}

		String value = context.getValue();
		if (value == null && context.getValueFile() != null) {
			value = fileUtils.readFile(context.getValueFile()).trim();
		}
		log.info("Replacing content in " + context.getFile());

		tokenReplacer.replaceTokens(token, value, regex, new FileStreamFactory(context, fileUtils));
	}

	public Log getLog() {
		return log;
	}

	public FileUtils getFileUtils() {
		return fileUtils;
	}

	public TokenReplacer getTokenReplacer() {
		return tokenReplacer;
	}
}
