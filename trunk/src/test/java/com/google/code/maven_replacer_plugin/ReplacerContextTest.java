package com.google.code.maven_replacer_plugin;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.code.maven_replacer_plugin.ReplacerContext;
import com.google.code.maven_replacer_plugin.file.FileUtils;


@RunWith(MockitoJUnitRunner.class)
public class ReplacerContextTest {
	private static final String FILE = "some file";
	private static final String VALUE = "value";
	private static final String TOKEN = "token";
	@Mock
	private FileUtils fileUtils;

	@Test
	public void shouldReturnGivenParameters() throws Exception {
		ReplacerContext context = new ReplacerContext(fileUtils, TOKEN, VALUE);
		assertEquals(TOKEN, context.getToken());
		assertEquals(VALUE, context.getValue());
		verifyZeroInteractions(fileUtils);
	}

	@Test
	public void shouldUseTokenFromFileUtilsIfGivenTokenIsNull() throws Exception {
		when(fileUtils.readFile(FILE)).thenReturn(TOKEN);

		ReplacerContext context = new ReplacerContext(fileUtils, null, VALUE);
		context.setTokenFile(FILE);
		assertEquals(TOKEN, context.getToken());
		assertEquals(VALUE, context.getValue());
	}

	@Test
	public void shouldUseValueFromFileUtilsIfGivenValueIsNull() throws Exception {
		when(fileUtils.readFile(FILE)).thenReturn(VALUE);

		ReplacerContext context = new ReplacerContext(fileUtils, TOKEN, null);
		context.setValueFile(FILE);
		assertEquals(TOKEN, context.getToken());
		assertEquals(VALUE, context.getValue());
	}
}
