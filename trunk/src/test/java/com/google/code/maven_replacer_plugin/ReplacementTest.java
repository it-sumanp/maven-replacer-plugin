package com.google.code.maven_replacer_plugin;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.code.maven_replacer_plugin.Replacement;
import com.google.code.maven_replacer_plugin.file.FileUtils;


@RunWith(MockitoJUnitRunner.class)
public class ReplacementTest {
	private static final String FILE = "some file";
	private static final String VALUE = "value";
	private static final String TOKEN = "token";
	@Mock
	private FileUtils fileUtils;

	@Test
	public void shouldReturnGivenParameters() throws Exception {
		Replacement context = new Replacement(fileUtils, TOKEN, VALUE);
		assertEquals(TOKEN, context.getToken());
		assertEquals(VALUE, context.getValue());
		verifyZeroInteractions(fileUtils);
	}

	@Test
	public void shouldUseTokenFromFileUtilsIfGivenTokenIsNull() throws Exception {
		when(fileUtils.readFile(FILE)).thenReturn(TOKEN);

		Replacement context = new Replacement(fileUtils, null, VALUE);
		context.setTokenFile(FILE);
		assertEquals(TOKEN, context.getToken());
		assertEquals(VALUE, context.getValue());
	}

	@Test
	public void shouldUseValueFromFileUtilsIfGivenValueIsNull() throws Exception {
		when(fileUtils.readFile(FILE)).thenReturn(VALUE);

		Replacement context = new Replacement(fileUtils, TOKEN, null);
		context.setValueFile(FILE);
		assertEquals(TOKEN, context.getToken());
		assertEquals(VALUE, context.getValue());
	}
}
