package com.google.code.maven_replacer_plugin;


import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.code.maven_replacer_plugin.file.FileUtils;

public class ReplacerTest {

	private static final String FILE = "file";
	private static final String OUTPUT_FILE = "outputFile";
	private static final String NEW_CONTENT = "new content";
	private static final int REGEX_FLAGS = 0;
	private static final boolean USE_REGEX = true;
	private static final boolean NO_REGEX = false;
	private static final String TOKEN = "token";
	private static final String CONTENT = "content";
	private static final String VALUE = "value";
	
	private FileUtils fileUtils;
	private TokenReplacer tokenReplacer;
	private Replacer replacer;

	@Before
	public void setUp() throws Exception {
		fileUtils = mock(FileUtils.class);
		when(fileUtils.readFile(FILE)).thenReturn(CONTENT);
		
		tokenReplacer = mock(TokenReplacer.class);
		
		replacer = new Replacer(fileUtils, tokenReplacer);
	}
	
	@Test
	public void shouldWriteReplacedRegexTextToFile() throws Exception {
		Replacement context = mock(Replacement.class);
		when(context.getToken()).thenReturn(TOKEN);
		when(context.getValue()).thenReturn(VALUE);
		List<Replacement> contexts = Arrays.asList(context);
		
		when(tokenReplacer.replaceRegex(CONTENT, TOKEN, VALUE, REGEX_FLAGS)).thenReturn(NEW_CONTENT);
		
		replacer.replace(contexts, USE_REGEX, FILE, OUTPUT_FILE, REGEX_FLAGS);
		verify(fileUtils).writeToFile(OUTPUT_FILE, NEW_CONTENT);
	}
	
	@Test
	public void shouldWriteReplacedNonRegexTextToFile() throws Exception {
		Replacement context = mock(Replacement.class);
		when(context.getToken()).thenReturn(TOKEN);
		when(context.getValue()).thenReturn(VALUE);
		List<Replacement> contexts = Arrays.asList(context);
		
		when(tokenReplacer.replaceNonRegex(CONTENT, TOKEN, VALUE)).thenReturn(NEW_CONTENT);
		
		replacer.replace(contexts, NO_REGEX, FILE, OUTPUT_FILE, REGEX_FLAGS);
		verify(fileUtils).writeToFile(OUTPUT_FILE, NEW_CONTENT);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void shouldThrowExceptionIfNoToken() throws Exception {
		Replacement context = mock(Replacement.class);
		List<Replacement> contexts = Arrays.asList(context);
		
		replacer.replace(contexts, USE_REGEX, FILE, OUTPUT_FILE, REGEX_FLAGS);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void shouldThrowExceptionIfEmptyToken() throws Exception {
		Replacement context = mock(Replacement.class);
		when(context.getToken()).thenReturn(" ");
		List<Replacement> contexts = Arrays.asList(context);
		
		replacer.replace(contexts, USE_REGEX, FILE, OUTPUT_FILE, REGEX_FLAGS);
	}

	@Test
	public void shouldReturnSameInstancesAsGivenInConstructor() {
		assertSame(replacer.getFileUtils(), fileUtils);
		assertSame(replacer.getTokenReplacer(), tokenReplacer);
	}
}
