package com.google.code.maven_replacer_plugin;

import java.util.List;
import java.util.regex.Pattern;

public class PatternFlagsFactory {
	public static final int NO_FLAGS = -1;

	public int buildFlags(List<String> flags) {
		if (flags == null || flags.isEmpty()) {
			return NO_FLAGS;
		}

		int value = 0;
		for (String flag : flags) {
			value |= getValueOf(flag);
		}
		return value;
	}

	private int getValueOf(String flag) {
		if ("CANON_EQ".equalsIgnoreCase(flag)) {
			return Pattern.CANON_EQ;
		}

		if ("CASE_INSENSITIVE".equalsIgnoreCase(flag)) {
			return Pattern.CASE_INSENSITIVE;
		}

		if ("COMMENTS".equalsIgnoreCase(flag)) {
			return Pattern.COMMENTS;
		}

		if ("DOTALL".equalsIgnoreCase(flag)) {
			return Pattern.DOTALL;
		}

		if ("LITERAL".equalsIgnoreCase(flag)) {
			return Pattern.LITERAL;
		}

		if ("MULTILINE".equalsIgnoreCase(flag)) {
			return Pattern.MULTILINE;
		}

		if ("UNICODE_CASE".equalsIgnoreCase(flag)) {
			return Pattern.UNICODE_CASE;
		}

		if ("UNIX_LINES".equalsIgnoreCase(flag)) {
			return Pattern.UNIX_LINES;
		}

		throw new IllegalArgumentException("Unknown regex flag: " + flag);
	}
}
