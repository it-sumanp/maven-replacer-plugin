package com.google.code.maven_replacer_plugin;

import static org.junit.Assert.assertEquals;

import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

public class TokenReplacerTest {
	private static final int NO_FLAGS = -1;
	
	private TokenReplacer replacer;

	@Before
	public void setUp() {
		replacer = new TokenReplacer();
	}

	@Test
	public void shouldReplaceNonRegexTokenWithValue() throws Exception {
		String results = replacer.replaceNonRegex("some $token$", "$token$", "value");
		assertEquals("some value", results);
	}

	@Test
	public void shouldReplaceRegexTokenWithValue() throws Exception {
		String results = replacer.replaceRegex("some token", "t.k.n", "value", NO_FLAGS);
		assertEquals("some value", results);
	}

	@Test
	public void shouldReplaceTokenWithEmptyValue() throws Exception {
		String results = replacer.replaceRegex("some token", "t.k.n", null, NO_FLAGS);
		assertEquals("some ", results);
	}

	@Test
	public void shouldReplaceTokenInMulipleLines() throws Exception {
		String results = replacer.replaceRegex("some\ntoken", "t.k.n", null, NO_FLAGS);
		assertEquals("some\n", results);
	}
	
	@Test
	public void shouldReplaceTokenOnCompleteLine() throws Exception {
		String results = replacer.replaceRegex("some\nreplace=token\nnext line", "^replace=.*$", "replace=value", Pattern.MULTILINE);
		assertEquals("some\nreplace=value\nnext line", results);
	}
	
	@Test
	public void shouldReplaceTokenWithCaseInsensitivity() throws Exception {
		String results = replacer.replaceRegex("test", "TEST", "value", Pattern.CASE_INSENSITIVE);
		assertEquals("value", results);
	}

	@Test
	public void shouldHandleEmptyContentsGracefully() {
		String results = replacer.replaceRegex("", "anything", "anything", NO_FLAGS);
		assertEquals("", results);

		results = replacer.replaceNonRegex("", "anything", "anything");
		assertEquals("", results);
	}
	
	@Test
	public void shouldHandleEmptyValueForNonRegex() throws Exception {
		String results = replacer.replaceNonRegex("some token", "token", null);
		assertEquals("some ", results);
	}
	
	@Test
	public void shouldReplaceWithGroups() throws Exception {
		String results = replacer.replaceRegex("test 123 number", "test (.*) number", "group $1 replaced", NO_FLAGS);
		assertEquals("group 123 replaced", results);
	}
}
