package com.google.code.maven_replacer_plugin;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ReplacerFactoryTest {
	@Mock
	private Replacement replacement;

	@Test
	public void shouldReturnReplacerWithFileUtilsAndTokenReplacer() {
		ReplacerFactory factory = new ReplacerFactory();

		Replacer replacer = factory.create(replacement);
		assertNotNull(replacer);
	}
}
