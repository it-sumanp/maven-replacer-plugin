package com.google.code.maven_replacer_plugin;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

import com.google.code.maven_replacer_plugin.TokenReplacer;

public class TokenReplacerTest {
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
		String results = replacer.replaceRegex("some token", "t.k.n", "value", -1);
		assertEquals("some value", results);
	}

	@Test
	public void shouldReplaceTokenWithEmptyValue() throws Exception {
		String results = replacer.replaceRegex("some token", "t.k.n", null, -1);
		assertEquals("some ", results);
	}

	@Test
	public void shouldReplaceTokenInMulipleLines() throws Exception {
		String results = replacer.replaceRegex("some\ntoken", "t.k.n", null, -1);
		assertEquals("some\n", results);
	}
	
	@Test
	public void shouldReplaceTokenWithCaseInsensitivity() throws Exception {
		String results = replacer.replaceRegex("test", "TEST", "value", Pattern.CASE_INSENSITIVE);
		assertEquals("value", results);
	}

	@Test
	public void shouldHandleEmptyContentsGracefully() {
		String results = replacer.replaceRegex("", "anything", "anything", -1);
		assertEquals("", results);

		results = replacer.replaceNonRegex("", "anything", "anything");
		assertEquals("", results);
	}
	
	@Test
	public void shouldHandleEmptyValueForNonRegex() throws Exception {
		String results = replacer.replaceNonRegex("some token", "token", null);
		assertEquals("some ", results);
	}
}
