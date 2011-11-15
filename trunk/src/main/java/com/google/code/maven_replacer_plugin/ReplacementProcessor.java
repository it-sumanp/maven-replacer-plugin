package com.google.code.maven_replacer_plugin;

import static org.apache.commons.lang.StringUtils.isEmpty;

import java.io.IOException;
import java.util.List;

import com.google.code.maven_replacer_plugin.file.FileUtils;

public class ReplacementProcessor {
	private final FileUtils fileUtils;
	private final ReplacerFactory replacerFactory;

	public ReplacementProcessor(FileUtils fileUtils, ReplacerFactory replacerFactory) {
		this.fileUtils = fileUtils;
		this.replacerFactory = replacerFactory;
	}
	
	public void replace(List<Replacement> contexts, boolean regex, String file,
			String outputFile, int regexFlags) throws IOException {
		String content = fileUtils.readFile(file);
		for (Replacement context : contexts) {
			content = replaceContent(regex, regexFlags, content, context);
		}

		fileUtils.writeToFile(outputFile, content);
	}

	private String replaceContent(boolean regex, int regexFlags, String content, Replacement context) {
		if (isEmpty(context.getToken())) {
			throw new IllegalArgumentException("Token or token file required");
		}

		Replacer replacer = replacerFactory.create(context);
		if (regex) {
			return replacer.replaceRegex(content, context.getToken(), context.getValue(), regexFlags);
		}
		return replacer.replaceNonRegex(content, context.getToken(), context.getValue());
	}
}
