package com.google.code.maven_replacer_plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.google.code.maven_replacer_plugin.file.FileUtils;


public class TokenValueMapFactory {

	private static final char SEPARATOR_ESCAPER = '\\';

	private static final char SEPARATOR = '=';

	private static final String COMMENT_PREFIX = "#";

	private final FileUtils fileUtils;

	public TokenValueMapFactory(FileUtils fileUtils) {
		this.fileUtils = fileUtils;
	}

	public List<Replacement> contextsForFile(String tokenValueMapFile, boolean commentsEnabled) throws IOException {
		String contents = fileUtils.readFile(tokenValueMapFile);
		BufferedReader reader = new BufferedReader(new StringReader(contents));
		
		String line = null;
		List<Replacement> contexts = new ArrayList<Replacement>();
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (ignoreLine(line, commentsEnabled)) {
				continue;
			}
			
			StringBuilder token = new StringBuilder();
			String value = "";
			boolean settingToken = true;
			for (int i=0; i < line.length(); i++) {
				if (i == 0 && line.charAt(0) == SEPARATOR) {
					throw new IllegalArgumentException(getNoValueErrorMsgFor(line) + "1");
				}
				
				if (settingToken && !isSeparatorAt(i, line)) {
					token.append(line.charAt(i));
				} else if (isSeparatorAt(i, line)) {
					settingToken = false;
					continue;
				} else {
					value = line.substring(i);
					break;
				}
			}
			
			String tokenVal = token.toString().trim();
			if (tokenVal.length() == 0 || settingToken) {
				continue;
			}
			value = value.trim();
			contexts.add(new Replacement(fileUtils, tokenVal, value));
		}
		return contexts;
	}

	private boolean isSeparatorAt(int i, String line) {
		return line.charAt(i) == SEPARATOR && line.charAt(i - 1) != SEPARATOR_ESCAPER;
	}

	private boolean isNotSeparatorCharAt(int i, String line) {
		return line.charAt(i) != SEPARATOR || (i > 0 && line.charAt(i) == SEPARATOR && line.charAt(i - 1) == SEPARATOR_ESCAPER);
	}

	private String getNoValueErrorMsgFor(String line) {
		return "No value for token: " + line + ". Make sure that " +
				"tokens have values in pairs in the format: token=value";
	}

	private boolean ignoreLine(String line, boolean commentsEnabled) {
		return line.length() == 0 || (commentsEnabled && line.startsWith(COMMENT_PREFIX));
	}
}
