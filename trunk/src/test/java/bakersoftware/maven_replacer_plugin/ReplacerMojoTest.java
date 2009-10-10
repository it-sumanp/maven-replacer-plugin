package bakersoftware.maven_replacer_plugin;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import bakersoftware.maven_replacer_plugin.file.FileUtils;

@RunWith(MockitoJUnitRunner.class)
public class ReplacerMojoTest {
	@Mock
	private TokenReplacer tokenReplacer;

	@Mock
	private FileUtils fileUtils;

	private ReplacerMojo replacer;

	@Before
	public void setUp() {
		replacer = new ReplacerMojo(tokenReplacer, fileUtils);
	}

	@Test
	public void shouldReplaceTokensInFile() throws Exception {
		String token = "token";
		String value = "value";

		replacer.setRegex(true);
		replacer.setToken(token);
		replacer.setValue(value);
		replacer.execute();
		verify(tokenReplacer).replaceTokens(token, value, true);
	}

	@Test(expected = MojoExecutionException.class)
	public void shouldThrowMojoExceptionWhenIOException() throws MojoExecutionException,
			IOException {
		doThrow(new IOException()).when(tokenReplacer).replaceTokens(anyString(), anyString(),
				anyBoolean());

		replacer.execute();
		verify(tokenReplacer).replaceTokens(anyString(), anyString(), anyBoolean());
	}

	@Test
	public void shouldIgnoreMissingFile() throws MojoExecutionException {
		replacer.setFile("some missing file");
		replacer.setIgnoreMissingFile(true);

		replacer.execute();
		verify(fileUtils).fileExists("some missing file");
		verifyZeroInteractions(tokenReplacer);
	}
}
