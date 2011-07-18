package com.google.code.maven_replacer_plugin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

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
		assertThat(results, equalTo("some value"));
	}

	@Test
	public void shouldReplaceRegexTokenWithValue() throws Exception {
		String results = replacer.replaceRegex("some token", "t.k.n", "value", NO_FLAGS);
		assertThat(results, equalTo("some value"));
	}

	@Test
	public void shouldReplaceTokenWithEmptyValue() throws Exception {
		String results = replacer.replaceRegex("some token", "t.k.n", null, NO_FLAGS);
		assertThat(results, equalTo("some "));
	}

	@Test
	public void shouldReplaceTokenInMulipleLines() throws Exception {
		String results = replacer.replaceRegex("some\ntoken", "t.k.n", null, NO_FLAGS);
		assertThat(results, equalTo("some\n"));
	}
	
	@Test
	public void shouldReplaceTokenOnCompleteLine() throws Exception {
		String results = replacer.replaceRegex("some\nreplace=token\nnext line", "^replace=.*$", "replace=value", Pattern.MULTILINE);
		assertThat(results, equalTo("some\nreplace=value\nnext line"));
	}
	
	@Test
	public void shouldReplaceTokenWithCaseInsensitivity() throws Exception {
		String results = replacer.replaceRegex("test", "TEST", "value", Pattern.CASE_INSENSITIVE);
		assertThat(results, equalTo("value"));
	}

	@Test
	public void shouldHandleEmptyContentsGracefully() {
		String results = replacer.replaceRegex("", "anything", "anything", NO_FLAGS);
		assertThat(results, equalTo(""));

		results = replacer.replaceNonRegex("", "anything", "anything");
		assertThat(results, equalTo(""));
	}
	
	@Test
	public void shouldHandleEmptyValueForNonRegex() throws Exception {
		String results = replacer.replaceNonRegex("some token", "token", null);
		assertThat(results, equalTo("some "));
	}
	
	@Test
	public void shouldReplaceWithGroups() throws Exception {
		String results = replacer.replaceRegex("test 123 number", "test (.*) number", "group $1 replaced", NO_FLAGS);
		assertThat(results, equalTo("group 123 replaced"));
	}
}
