package com.google.code.maven_replacer_plugin;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import com.google.code.maven_replacer_plugin.file.FileUtils;


public class Replacer {
	private final FileUtils fileUtils;
	private final TokenReplacer tokenReplacer;

	public Replacer(FileUtils fileUtils, TokenReplacer tokenReplacer) {
		this.fileUtils = fileUtils;
		this.tokenReplacer = tokenReplacer;
	}

	public void replace(List<Replacement> contexts, boolean regex, String file,
			String outputFile, int regexFlags) throws IOException {
		String content = fileUtils.readFile(file);
		for (Replacement context : contexts) {
			content = replaceContent(regex, regexFlags, content, context);
		}

		fileUtils.ensureFolderStructureExists(outputFile);
		Writer writer = new OutputStreamWriter(new FileOutputStream(outputFile));
		writer.write(content);
		writer.close();
	}

	private String replaceContent(boolean regex, int regexFlags, String content,
			Replacement context) {
		if (context.getToken() == null || context.getToken().trim().length() == 0) {
			throw new IllegalArgumentException("Token or token file required");
		}

		if (regex) {
			return tokenReplacer.replaceRegex(content, context.getToken(), context.getValue(), regexFlags);
		}
		return tokenReplacer.replaceNonRegex(content, context.getToken(), context.getValue());
	}

	public FileUtils getFileUtils() {
		return fileUtils;
	}

	public TokenReplacer getTokenReplacer() {
		return tokenReplacer;
	}
}
