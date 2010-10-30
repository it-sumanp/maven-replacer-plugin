package com.google.code.maven_replacer_plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.google.code.maven_replacer_plugin.file.FileUtils;


public class TokenValueMapFactory {

	private static final String COMMENT_PREFIX = "#";

	private final FileUtils fileUtils;

	public TokenValueMapFactory(FileUtils fileUtils) {
		this.fileUtils = fileUtils;
	}

	public List<Replacement> contextsForFile(String tokenValueMapFile, boolean commentsEnabled) throws IOException {
		String contents = fileUtils.readFile(tokenValueMapFile);
		BufferedReader reader = new BufferedReader(new StringReader(contents));
		
		String token = null;
		List<Replacement> contexts = new ArrayList<Replacement>();
		while ((token = reader.readLine()) != null) {
			token = token.trim();
			if (ignoreLine(token, commentsEnabled)) {
				continue;
			}
			String value = reader.readLine();
			if (value == null) {
				throw new IllegalArgumentException("No value for token: " + token + ". Make sure that " +
						"tokens have values in pairs in the format: token (new line) value (new line) token (new line) value");
			}
			contexts.add(new Replacement(fileUtils, token, value));
		}
		return contexts;
	}

	private boolean ignoreLine(String line, boolean commentsEnabled) {
		return line.length() == 0 || (commentsEnabled && line.startsWith(COMMENT_PREFIX));
	}
}
