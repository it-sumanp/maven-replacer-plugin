package com.google.code.maven_replacer_plugin;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.code.maven_replacer_plugin.file.FileUtils;


@RunWith(MockitoJUnitRunner.class)
public class ReplacementTest {
	private static final String UNESCAPED = "test\\n123\\t456";
	private static final String ESCAPED = "test\n123\t456";
	private static final String FILE = "some file";
	private static final String VALUE = "value";
	private static final String TOKEN = "token";
	
	@Mock
	private FileUtils fileUtils;
	@Mock
	private DelimiterBuilder delimiter;

	@Test
	public void shouldReturnGivenParameters() throws Exception {
		Replacement context = new Replacement(fileUtils, TOKEN, VALUE, false);
		assertEquals(TOKEN, context.getToken());
		assertEquals(VALUE, context.getValue());
		verifyZeroInteractions(fileUtils);
	}
	
	@Test
	public void shouldApplyToTokenDelimeterIfExists() throws Exception {
		when(delimiter.apply(TOKEN)).thenReturn("new token");
		Replacement context = new Replacement(fileUtils, TOKEN, VALUE, false).withDelimiter(delimiter);
		
		assertEquals("new token", context.getToken());
		assertEquals(VALUE, context.getValue());
		verifyZeroInteractions(fileUtils);
	}
	
	@Test
	public void shouldUseEscapedTokensAndValues() {
		Replacement context = new Replacement(fileUtils, UNESCAPED, UNESCAPED, true);
		assertEquals(ESCAPED, context.getToken());
		assertEquals(ESCAPED, context.getValue());
		verifyZeroInteractions(fileUtils);
	}
	
	@Test
	public void shouldUseEscapedTokensAndValuesFromFiles() throws Exception {
		when(fileUtils.readFile(FILE)).thenReturn(UNESCAPED);

		Replacement context = new Replacement(fileUtils, null, null, true);
		context.setTokenFile(FILE);
		context.setValueFile(FILE);
		
		assertEquals(ESCAPED, context.getToken());
		assertEquals(ESCAPED, context.getValue());
	}

	@Test
	public void shouldUseTokenFromFileUtilsIfGiven() throws Exception {
		when(fileUtils.readFile(FILE)).thenReturn(TOKEN);

		Replacement context = new Replacement(fileUtils, null, VALUE, false);
		context.setTokenFile(FILE);
		assertEquals(TOKEN, context.getToken());
		assertEquals(VALUE, context.getValue());
	}

	@Test
	public void shouldUseValueFromFileUtilsIfGiven() throws Exception {
		when(fileUtils.readFile(FILE)).thenReturn(VALUE);

		Replacement context = new Replacement(fileUtils, TOKEN, null, false);
		context.setValueFile(FILE);
		assertEquals(TOKEN, context.getToken());
		assertEquals(VALUE, context.getValue());
	}
	
	@Test
	public void shouldSetAndGetSameValues() {
		Replacement context = new Replacement();
		
		context.setToken(TOKEN);
		assertEquals(TOKEN, context.getToken());
		context.setValue(VALUE);
		assertEquals(VALUE, context.getValue());
	}
}
