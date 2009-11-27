package bakersoftware.maven_replacer_plugin;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TokenReplacerTest {
	private TokenReplacer replacer;

	@Before
	public void setUp() {
		replacer = new TokenReplacer();
	}

	@Test
	public void shouldReplaceNonRegexTokenWithValue() throws Exception {
		String results = replacer.replaceContents("some $token$", "$token$", "value", false);
		assertEquals(results, "some value");
	}

	@Test
	public void shouldReplaceRegexTokenWithValue() throws Exception {
		String results = replacer.replaceContents("some token", "t.k.n", "value", true);
		assertEquals("some value", results);
	}

	@Test
	public void shouldReplaceTokenWithEmptyValue() throws Exception {
		String results = replacer.replaceContents("some token", "t.k.n", null, true);
		assertEquals("some ", results);
	}

	@Test
	public void shouldReplaceTokenInMulipleLines() throws Exception {
		String results = replacer.replaceContents("some\ntoken", "t.k.n", null, true);
		assertEquals("some\n", results);
	}

	@Test
	public void shouldHandleEmptyContentsGracefully() {
		String results = replacer.replaceContents("", "anything", "anything", true);
		assertEquals("", results);

		results = replacer.replaceContents("", "anything", "anything", false);
		assertEquals("", results);
	}
}
