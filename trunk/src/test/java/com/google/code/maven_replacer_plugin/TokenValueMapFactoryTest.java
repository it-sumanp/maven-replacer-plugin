package com.google.code.maven_replacer_plugin;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
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
	public void shouldReturnContextsFromFile() throws Exception {
		when(fileUtils.readFile(FILENAME)).thenReturn("token=value");
		
		List<Replacement> contexts = factory.contextsForFile(FILENAME, COMMENTS_DISABLED, false);
		assertNotNull(contexts);
		assertEquals(1, contexts.size());
		assertEquals("token", contexts.get(0).getToken());
		assertEquals("value", contexts.get(0).getValue());
	}

	@Test
	public void shouldReturnContextsFromFileAndIgnoreBlankLinesAndComments() throws Exception {
		when(fileUtils.readFile(FILENAME)).thenReturn("\n  \ntoken1=value1\ntoken2 = value2\n#some comment\n");
		
		List<Replacement> contexts = factory.contextsForFile(FILENAME, COMMENTS_ENABLED, false);
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
		
		List<Replacement> contexts = factory.contextsForFile(FILENAME, COMMENTS_DISABLED, false);
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
		List<Replacement> contexts = factory.contextsForFile(FILENAME, COMMENTS_DISABLED, false);
		assertNotNull(contexts);
		assertTrue(contexts.isEmpty());
	}
	
	@Test
	public void shouldReturnRegexContextsFromFile() throws Exception {
		when(fileUtils.readFile(FILENAME)).thenReturn("\\=tok\\=en1=val\\=ue1\nto$ke..n2=value2");
		
		List<Replacement> contexts = factory.contextsForFile(FILENAME, COMMENTS_ENABLED, false);
		assertNotNull(contexts);
		assertEquals(2, contexts.size());
		assertEquals("\\=tok\\=en1", contexts.get(0).getToken());
		assertEquals("val\\=ue1", contexts.get(0).getValue());
		assertEquals("to$ke..n2", contexts.get(1).getToken());
		assertEquals("value2", contexts.get(1).getValue());
	}
	
	@Test
	public void shouldReturnRegexContextsFromFileUnescaping() throws Exception {
		when(fileUtils.readFile(FILENAME)).thenReturn("\\\\=tok\\\\=en1=val\\\\=ue1\nto$ke..n2=value2");
		
		List<Replacement> contexts = factory.contextsForFile(FILENAME, COMMENTS_ENABLED, true);
		assertNotNull(contexts);
		assertEquals(2, contexts.size());
		assertEquals("\\=tok\\=en1", contexts.get(0).getToken());
		assertEquals("val\\=ue1", contexts.get(0).getValue());
		assertEquals("to$ke..n2", contexts.get(1).getToken());
		assertEquals("value2", contexts.get(1).getValue());
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void shouldThrowExceptionIfNoTokenForValue() throws Exception {
		when(fileUtils.readFile(FILENAME)).thenReturn("=value");
		factory.contextsForFile(FILENAME, COMMENTS_DISABLED, false);
	}
	
	@Test
	public void shouldSupportEmptyFileAndReturnNoReplacements() throws Exception {
		when(fileUtils.readFile(FILENAME)).thenReturn("");
		assertTrue(factory.contextsForFile(FILENAME, COMMENTS_DISABLED, false).isEmpty());
	}
	
	@Test
	public void shouldReturnListOfContextsFromVariable() {
		List<Replacement> contexts = factory.contextsForVariable("token1=value1,token2=value2", false, false);
		assertThat(contexts, hasItem(contextWith("token1", "value1")));
		assertThat(contexts, hasItem(contextWith("token2", "value2")));
	}

	private Matcher<Replacement> contextWith(final String token, final String value) {
		return new BaseMatcher<Replacement>() {
			public boolean matches(Object o) {
				Replacement replacement = (Replacement)o;
				return token.equals(replacement.getToken()) && value.equals(replacement.getValue());
			}

			public void describeTo(Description desc) {
				desc.appendText("token=" + token + ", value=" + value);
			}
		};
	}
}
