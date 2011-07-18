package com.google.code.maven_replacer_plugin;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.code.maven_replacer_plugin.Replacer;
import com.google.code.maven_replacer_plugin.ReplacerFactory;
import com.google.code.maven_replacer_plugin.TokenReplacer;
import com.google.code.maven_replacer_plugin.file.FileUtils;


@RunWith(MockitoJUnitRunner.class)
public class ReplacerFactoryTest {
	@Mock
	private FileUtils fileUtils;

	@Mock
	private TokenReplacer tokenReplacer;

	@Test
	public void shouldReturnReplacerWithFileUtilsAndTokenReplacer() {
		ReplacerFactory factory = new ReplacerFactory(fileUtils, tokenReplacer);

		Replacer replacer = factory.create();
		assertNotNull(replacer);
		assertThat(replacer.getFileUtils(), is(fileUtils));
		assertThat(replacer.getTokenReplacer(), is(tokenReplacer));
	}
}
