package com.google.code.maven_replacer_plugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.code.maven_replacer_plugin.file.FileUtils;

@RunWith(MockitoJUnitRunner.class)
public class TokenValueMapFactoryTest {
	private static final String FILENAME = "some file";

	@Mock
	private FileUtils fileUtils;

	private TokenValueMapFactory factory;

	@Before
	public void setUp() {
		factory = new TokenValueMapFactory(fileUtils);
	}

	@Test
	public void shouldReturnContextsFromFile() throws Exception {
		when(fileUtils.readFile(FILENAME)).thenReturn("token1\nvalue1\ntoken2\nvalue2");
		
		List<Replacement> contexts = factory.contextsForFile(FILENAME);
		Collections.sort(contexts, new Comparator<Replacement>() {
			public int compare(Replacement c1, Replacement c2) {
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
	
	@Test
	public void shouldReturnRegexContextsFromFile() throws Exception {
		when(fileUtils.readFile(FILENAME)).thenReturn("\\=tok\\=en1\nvalue1\nto$ke..n2\nvalue2");
		
		List<Replacement> contexts = factory.contextsForFile(FILENAME);
		Collections.sort(contexts, new Comparator<Replacement>() {
			public int compare(Replacement c1, Replacement c2) {
				return c1.getToken().compareTo(c2.getToken());
			}
		});
		assertNotNull(contexts);
		assertEquals(2, contexts.size());
		assertEquals("\\=tok\\=en1", contexts.get(0).getToken());
		assertEquals("value1", contexts.get(0).getValue());
		assertEquals("to$ke..n2", contexts.get(1).getToken());
		assertEquals("value2", contexts.get(1).getValue());
	}
}
