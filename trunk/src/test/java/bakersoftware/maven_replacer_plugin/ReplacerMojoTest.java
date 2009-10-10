package bakersoftware.maven_replacer_plugin;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import bakersoftware.maven_replacer_plugin.file.FileUtils;

@RunWith(MockitoJUnitRunner.class)
public class ReplacerMojoTest {
	private static final String TOKEN = "token";
	private static final String VALUE = "value";

	@Mock
	private TokenReplacer tokenReplacer;

	@Mock
	private FileUtils fileUtils;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private ReplacerMojo replacer;

	@Before
	public void setUp() {
		replacer = new ReplacerMojo(tokenReplacer, fileUtils);
		replacer.setToken(TOKEN);
		replacer.setValue(VALUE);
	}

	@Test
	public void shouldReplaceRegexTokensInFile() throws Exception {
		replacer.setRegex(true);
		replacer.execute();
		verify(tokenReplacer).replaceTokens(TOKEN, VALUE, true);
	}

	@Test
	public void shouldReplaceNonRegexTokensInFile() throws Exception {
		replacer.setRegex(false);
		replacer.execute();
		verify(tokenReplacer).replaceTokens(TOKEN, VALUE, false);
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
	public void shouldIgnoreMissingFileAndReturnImmediately() throws MojoExecutionException {
		replacer.setFile("some missing file");
		replacer.setIgnoreMissingFile(true);

		replacer.execute();
		verify(fileUtils).fileExists("some missing file");
		verifyZeroInteractions(tokenReplacer);
	}

	@Test(expected = MojoExecutionException.class)
	public void shouldThrowMojoExceptionIfNoTokenOrTokenFileSupplied()
			throws MojoExecutionException {
		replacer.setToken(null);
		replacer.setTokenFile(null);
		replacer.execute();

		verifyZeroInteractions(tokenReplacer);
	}

	@Test
	public void shouldUseTokenInFileIfTokenFileSupplied() throws Exception {
		String tokenFile = "tokenFile";

		File file = folder.newFile(tokenFile);
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		out.write(TOKEN);
		out.close();

		replacer.setToken(null);
		replacer.setTokenFile(file.getAbsolutePath());
		replacer.execute();
		verify(tokenReplacer).replaceTokens(eq(TOKEN), eq(VALUE), anyBoolean());
	}

	@Test
	public void shouldUseValueInFileIfValueFileSupplied() throws Exception {
		String valueFile = "tokenFile";

		File file = folder.newFile(valueFile);
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		out.write(VALUE);
		out.close();

		replacer.setValue(null);
		replacer.setValueFile(file.getAbsolutePath());
		replacer.execute();
		verify(tokenReplacer).replaceTokens(eq(TOKEN), eq(VALUE), anyBoolean());
	}
}
