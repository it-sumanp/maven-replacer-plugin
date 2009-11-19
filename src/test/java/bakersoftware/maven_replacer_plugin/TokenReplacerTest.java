package bakersoftware.maven_replacer_plugin;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import bakersoftware.maven_replacer_plugin.file.StreamFactory;

@RunWith(MockitoJUnitRunner.class)
public class TokenReplacerTest {
	private static final String SEPARATOR = System.getProperty("line.separator");

	@Mock
	private StreamFactory streamFactory;

	private TokenReplacer tokenReplacer;

	@Before
	public void setUp() {
		tokenReplacer = new TokenReplacer();
	}

	@Test
	public void shouldReplaceRegularTokens() throws Exception {
		InputStream inputStream = spy(new ByteArrayInputStream("some token data".getBytes()));
		ByteArrayOutputStream outputStream = spy(new ByteArrayOutputStream());

		when(streamFactory.getNewInputStream()).thenReturn(inputStream);
		when(streamFactory.getNewOutputStream()).thenReturn(outputStream);
		tokenReplacer.replaceTokens("token", "value", false, streamFactory);

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
		tokenReplacer.replaceTokens("to[a-z]en", "value", true, streamFactory);

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
		tokenReplacer.replaceTokens("$token$", "value", false, streamFactory);

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
		tokenReplacer.replaceTokens("$token$", "", false, streamFactory);

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
		tokenReplacer.replaceTokens("$token$", null, false, streamFactory);

		assertEquals("some  data" + SEPARATOR, new String(outputStream.toByteArray()));
		verify(inputStream).close();
		verify(outputStream).close();
	}

	@Test
	public void shouldReplaceTokenWithValueContainingBackslashes() throws Exception {
		InputStream inputStream = spy(new ByteArrayInputStream("some token data".getBytes()));
		ByteArrayOutputStream outputStream = spy(new ByteArrayOutputStream());

		when(streamFactory.getNewInputStream()).thenReturn(inputStream);
		when(streamFactory.getNewOutputStream()).thenReturn(outputStream);

		tokenReplacer.replaceTokens("token", "\\value\\", false, streamFactory);
		assertEquals("some \\value\\ data" + SEPARATOR, new String(outputStream.toByteArray()));
		verify(inputStream).close();
		verify(outputStream).close();
	}
}
