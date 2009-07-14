package bakersoftware.maven_replacer_plugin;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Before;
import org.junit.Test;


public class ReplacerMojoTest {
	private final TokenReplacer tokenReplacer = mock(TokenReplacer.class);

	private ReplacerMojo replacer;

	@Before
	public void setUp() {
		replacer = new ReplacerMojo() {
			@Override
			public TokenReplacer getTokenReplacer() {
				return tokenReplacer;
			}
		};
	}
		
	@Test	
	public void shouldReplaceTokensInFile() throws Exception {
		String token = "token";
		String value = "value";
		
		replacer.setToken(token);
		replacer.setValue(value);
		replacer.execute();
		verify(tokenReplacer).replaceTokens(token, value);
	}
	
	@Test
	public void shouldReturnFileStreams() throws IOException {
		File file = new File("/tmp/tmpfile");
		try {
			file.createNewFile();
			replacer.setFile("/tmp/tmpfile");
			assertTrue(replacer.getNewInputStream() instanceof FileInputStream);
			assertTrue(replacer.getNewOutputStream() instanceof FileOutputStream);
		} finally {
			file.delete();
		}
	}
	
	@Test(expected = MojoExecutionException.class)	
	public void shouldThrowMojoExceptionWhenIOException() throws MojoExecutionException, IOException {
		doThrow(new IOException()).when(tokenReplacer).replaceTokens(anyString(), anyString());
		
		replacer.execute();
		verify(tokenReplacer).replaceTokens(anyString(), anyString());
	}
	
	@Test
	public void shouldIgnoreMissingFile() throws MojoExecutionException {
		replacer.setFile("some missing file");
		replacer.setIgnoreMissingFile(true);
		
		replacer.execute();
		verifyZeroInteractions(tokenReplacer);
	}
}
