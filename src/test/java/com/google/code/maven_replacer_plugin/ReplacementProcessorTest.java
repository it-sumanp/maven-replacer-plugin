package com.google.code.maven_replacer_plugin;


import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.code.maven_replacer_plugin.file.FileUtils;

@RunWith(MockitoJUnitRunner.class)
public class ReplacementProcessorTest {

	private static final String FILE = "file";
	private static final String OUTPUT_FILE = "outputFile";
	private static final String NEW_CONTENT = "new content";
	private static final int REGEX_FLAGS = 0;
	private static final boolean USE_REGEX = true;
	private static final boolean NO_REGEX = false;
	private static final String TOKEN = "token";
	private static final String CONTENT = "content";
	private static final String VALUE = "value";
	
	@Mock
	private FileUtils fileUtils;
	@Mock
	private Replacer replacer;
	@Mock
	private Replacement context;
	@Mock
	private ReplacerFactory replacerFactory;
	
	private ReplacementProcessor processor;

	@Before
	public void setUp() throws Exception {
		when(fileUtils.readFile(FILE)).thenReturn(CONTENT);
		when(context.getToken()).thenReturn(TOKEN);
		when(context.getValue()).thenReturn(VALUE);
		when(replacerFactory.create(context)).thenReturn(replacer);
		
		processor = new ReplacementProcessor(fileUtils, replacerFactory);
	}
	
	@Test
	public void shouldWriteReplacedRegexTextToFile() throws Exception {
		when(replacer.replaceRegex(CONTENT, TOKEN, VALUE, REGEX_FLAGS)).thenReturn(NEW_CONTENT);
		
		processor.replace(asList(context), USE_REGEX, FILE, OUTPUT_FILE, REGEX_FLAGS);
		verify(fileUtils).writeToFile(OUTPUT_FILE, NEW_CONTENT);
	}
	
	@Test
	public void shouldWriteReplacedNonRegexTextToFile() throws Exception {
		when(replacer.replaceNonRegex(CONTENT, TOKEN, VALUE)).thenReturn(NEW_CONTENT);
		
		processor.replace(asList(context), NO_REGEX, FILE, OUTPUT_FILE, REGEX_FLAGS);
		verify(fileUtils).writeToFile(OUTPUT_FILE, NEW_CONTENT);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void shouldThrowExceptionIfNoToken() throws Exception {
		when(context.getToken()).thenReturn(null);
		
		processor.replace(asList(context), USE_REGEX, FILE, OUTPUT_FILE, REGEX_FLAGS);
		verifyZeroInteractions(fileUtils);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void shouldThrowExceptionIfEmptyToken() throws Exception {
		when(context.getToken()).thenReturn("");
		
		processor.replace(asList(context), USE_REGEX, FILE, OUTPUT_FILE, REGEX_FLAGS);
		verifyZeroInteractions(fileUtils);
	}
}
