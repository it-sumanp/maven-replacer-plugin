package com.google.code.maven_replacer_plugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

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
	private static final boolean COMMENTS_ENABLED = true;
	private static final boolean COMMENTS_DISABLED = false;

	@Mock
	private FileUtils fileUtils;

	private TokenValueMapFactory factory;

	@Before
	public void setUp() {
		factory = new TokenValueMapFactory(fileUtils);
	}

	@Test
	public void shouldReturnContextsFromFileAndIgnoreBlankLinesAndComments() throws Exception {
		when(fileUtils.readFile(FILENAME)).thenReturn("\n  \ntoken1=value1\ntoken2 = value2\n#some comment\n");
		
		List<Replacement> contexts = factory.contextsForFile(FILENAME, COMMENTS_ENABLED);
		assertNotNull(contexts);
		assertEquals(2, contexts.size());
		assertEquals("token1", contexts.get(0).getToken());
		assertEquals("value1", contexts.get(0).getValue());
		assertEquals("token2", contexts.get(1).getToken());
		assertEquals("value2", contexts.get(1).getValue());
	}
	
	@Test
	public void shouldReturnContextsFromFileAndIgnoreBlankLinesUsingCommentLinesIfCommentsDisabled() throws Exception {
		when(fileUtils.readFile(FILENAME)).thenReturn("\n  \ntoken1=value1\ntoken2=value2\n#some=#comment\n");
		
		List<Replacement> contexts = factory.contextsForFile(FILENAME, COMMENTS_DISABLED);
		assertNotNull(contexts);
		assertEquals(3, contexts.size());
		assertEquals("token1", contexts.get(0).getToken());
		assertEquals("value1", contexts.get(0).getValue());
		assertEquals("token2", contexts.get(1).getToken());
		assertEquals("value2", contexts.get(1).getValue());
		assertEquals("#some", contexts.get(2).getToken());
		assertEquals("#comment", contexts.get(2).getValue());
	}
	
	@Test
	public void shouldIgnoreTokensWithNoSeparatedValue() throws Exception {
		when(fileUtils.readFile(FILENAME)).thenReturn("#comment\ntoken2");
		List<Replacement> contexts = factory.contextsForFile(FILENAME, COMMENTS_DISABLED);
		assertNotNull(contexts);
		assertTrue(contexts.isEmpty());
	}
	
	@Test
	public void shouldReturnRegexContextsFromFile() throws Exception {
		when(fileUtils.readFile(FILENAME)).thenReturn("\\=tok\\=en1=val\\=ue1\nto$ke..n2=value2");
		
		List<Replacement> contexts = factory.contextsForFile(FILENAME, COMMENTS_ENABLED);
		assertNotNull(contexts);
		assertEquals(2, contexts.size());
		assertEquals("\\=tok\\=en1", contexts.get(0).getToken());
		assertEquals("val\\=ue1", contexts.get(0).getValue());
		assertEquals("to$ke..n2", contexts.get(1).getToken());
		assertEquals("value2", contexts.get(1).getValue());
	}
}
