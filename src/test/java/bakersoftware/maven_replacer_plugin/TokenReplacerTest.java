package bakersoftware.maven_replacer_plugin;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

public class TokenReplacerTest {
	private static final String SEPARATOR = "separator";
	
	private TokenReplacer tokenReplacer;
	private StreamFactory streamFactory;
	private String lineSeparator;

	@Before
	public void setUp() {
		streamFactory = mock(StreamFactory.class);
		lineSeparator = SEPARATOR;
		tokenReplacer = new TokenReplacer(streamFactory, lineSeparator);
	}

	@Test
	public void shouldReplaceTokens() throws IOException {
		InputStream inputStream = spy(new ByteArrayInputStream("some token data".getBytes()));
		ByteArrayOutputStream outputStream = spy(new ByteArrayOutputStream());
		
		when(streamFactory.getNewInputStream()).thenReturn(inputStream);
		when(streamFactory.getNewOutputStream()).thenReturn(outputStream);
		tokenReplacer.replaceTokens("token", "value");
		
		assertEquals("some value data" + SEPARATOR, new String(outputStream.toByteArray()));
		verify(inputStream).close();
		verify(outputStream).close();
	}	
}
