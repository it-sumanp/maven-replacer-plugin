package bakersoftware.maven_replacer_plugin;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

public class TokenReplacerTest {
	private static final String SEPARATOR = "separator";

	private TokenReplacer tokenReplacer;
	private StreamFactory streamFactory;

	@Before
	public void setUp() {
		streamFactory = mock(StreamFactory.class);
		tokenReplacer = new TokenReplacer(streamFactory, SEPARATOR);
	}

	@Test
	public void shouldReplaceRegularTokens() throws Exception {
		InputStream inputStream = spy(new ByteArrayInputStream("some token data".getBytes()));
		ByteArrayOutputStream outputStream = spy(new ByteArrayOutputStream());

		when(streamFactory.getNewInputStream()).thenReturn(inputStream);
		when(streamFactory.getNewOutputStream()).thenReturn(outputStream);
		tokenReplacer.replaceTokens("token", "value", false);

		assertEquals("some value data" + SEPARATOR, new String(outputStream.toByteArray()));
		verify(inputStream).close();
		verify(outputStream).close();
	}

	@Test
	public void shouldReplaceRegexTokens() throws Exception {
		InputStream inputStream = spy(new ByteArrayInputStream("some token data".getBytes()));
		ByteArrayOutputStream outputStream = spy(new ByteArrayOutputStream());

		when(streamFactory.getNewInputStream()).thenReturn(inputStream);
		when(streamFactory.getNewOutputStream()).thenReturn(outputStream);
		tokenReplacer.replaceTokens("to[a-z]en", "value", true);

		assertEquals("some value data" + SEPARATOR, new String(outputStream.toByteArray()));
		verify(inputStream).close();
		verify(outputStream).close();
	}

	@Test
	public void shouldIgnoreRegexChars() throws Exception {
		InputStream inputStream = spy(new ByteArrayInputStream("some $token$ data".getBytes()));
		ByteArrayOutputStream outputStream = spy(new ByteArrayOutputStream());

		when(streamFactory.getNewInputStream()).thenReturn(inputStream);
		when(streamFactory.getNewOutputStream()).thenReturn(outputStream);
		tokenReplacer.replaceTokens("$token$", "value", false);

		assertEquals("some value data" + SEPARATOR, new String(outputStream.toByteArray()));
		verify(inputStream).close();
		verify(outputStream).close();
	}

	@Test
	public void shouldReplaceTokenWithEmptyIfValueBlank() throws Exception {
		InputStream inputStream = spy(new ByteArrayInputStream("some $token$ data".getBytes()));
		ByteArrayOutputStream outputStream = spy(new ByteArrayOutputStream());

		when(streamFactory.getNewInputStream()).thenReturn(inputStream);
		when(streamFactory.getNewOutputStream()).thenReturn(outputStream);
		tokenReplacer.replaceTokens("$token$", "", false);

		assertEquals("some  data" + SEPARATOR, new String(outputStream.toByteArray()));
		verify(inputStream).close();
		verify(outputStream).close();
	}

	@Test
	public void shouldReplaceTokenWithEmptyIfValueNull() throws Exception {
		InputStream inputStream = spy(new ByteArrayInputStream("some $token$ data".getBytes()));
		ByteArrayOutputStream outputStream = spy(new ByteArrayOutputStream());

		when(streamFactory.getNewInputStream()).thenReturn(inputStream);
		when(streamFactory.getNewOutputStream()).thenReturn(outputStream);
		tokenReplacer.replaceTokens("$token$", null, false);

		assertEquals("some  data" + SEPARATOR, new String(outputStream.toByteArray()));
		verify(inputStream).close();
		verify(outputStream).close();
	}
}
