package com.google.code.maven_replacer_plugin;

import java.util.regex.Pattern;

public class TokenReplacer {
	public String replaceRegex(String contents, String token, String value, int flags) {
		String valueToReplaceWith = value == null ? "" : value;
		
		final Pattern compiledPattern;
		if (flags == PatternFlagsFactory.NO_FLAGS) {
			compiledPattern = Pattern.compile(token);
		} else {
			compiledPattern = Pattern.compile(token, flags);
		}
		
		return compiledPattern.matcher(contents).replaceAll(valueToReplaceWith);
	}

	public String replaceNonRegex(String input, String token, String value) {
		String valueToReplaceWith = value == null ? "" : value;
		if (input.length() == 0) {
			return input;
		}

		StringBuffer buffer = new StringBuffer();
		int start = 0;
		int end = 0;
		while ((end = input.indexOf(token, start)) >= 0) {
			buffer.append(input.substring(start, end));
			buffer.append(valueToReplaceWith);
			start = end + token.length();
		}
		return buffer.append(input.substring(start)).toString();
	}

}
