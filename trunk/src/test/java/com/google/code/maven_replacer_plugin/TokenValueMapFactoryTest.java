package com.google.code.maven_replacer_plugin;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.code.maven_replacer_plugin.file.FileUtils;

@RunWith(MockitoJUnitRunner.class)
public class TokenValueMapFactoryTest {
	@Mock
	private FileUtils fileUtils;

	private TokenValueMapFactory factory;

	@Before
	public void setUp() {
		factory = new TokenValueMapFactory(fileUtils);
	}

	@Test
	public void shouldReturnContextsFromProperties() throws Exception {
		String file = "some file";
		when(fileUtils.readFile(file)).thenReturn("token1=value1\ntoken2=value2");
		
		List<ReplacerContext> contexts = factory.contextsForFile(file);
		Collections.sort(contexts, new Comparator<ReplacerContext>() {
			public int compare(ReplacerContext c1, ReplacerContext c2) {
				return c1.getToken().compareTo(c2.getToken());
			}
		});
		assertNotNull(contexts);
		assertEquals(2, contexts.size());
		assertEquals("token1", contexts.get(0).getToken());
		assertEquals("value1", contexts.get(0).getValue());
		assertEquals("token2", contexts.get(1).getToken());
		assertEquals("value2", contexts.get(1).getValue());
	}
}
