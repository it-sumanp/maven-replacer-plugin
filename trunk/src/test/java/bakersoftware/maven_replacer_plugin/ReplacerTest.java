package bakersoftware.maven_replacer_plugin;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import bakersoftware.maven_replacer_plugin.file.FileStreamFactory;
import bakersoftware.maven_replacer_plugin.file.FileUtils;

@RunWith(MockitoJUnitRunner.class)
public class ReplacerTest {
	private static final String TOKEN = "token";
	private static final String VALUE = "value";

	@Mock
	private Log log;
	@Mock
	private FileUtils fileUtils;
	@Mock
	private TokenReplacer tokenReplacer;
	@Mock
	private ReplacerContext context;

	private Replacer replacer;

	@Before
	public void setUp() {
		when(context.getToken()).thenReturn(TOKEN);
		when(context.getValue()).thenReturn(VALUE);

		replacer = new Replacer(log, fileUtils, tokenReplacer);
	}

	@Test
	public void shouldIgnoreMissingFileAndReturnImmediately() throws Exception {
		when(context.getFile()).thenReturn("some missing file");

		replacer.replace(context, true, false);
		verify(fileUtils).fileExists("some missing file");
		verifyZeroInteractions(tokenReplacer);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfNoTokenOrTokenFileSupplied()
			throws MojoExecutionException, Exception {
		when(context.getToken()).thenReturn(null);
		when(context.getTokenFile()).thenReturn(null);
		replacer.replace(context, false, false);

		verifyZeroInteractions(tokenReplacer);
	}

	@Test
	public void shouldUseTokenInFileIfTokenFileSupplied() throws Exception {
		String file = "tokenFile";
		when(fileUtils.readFile(file)).thenReturn(TOKEN);

		when(context.getToken()).thenReturn(null);
		when(context.getTokenFile()).thenReturn(file);

		replacer.replace(context, false, false);
		verify(tokenReplacer).replaceTokens(eq(TOKEN), eq(VALUE), anyBoolean(),
				isA(FileStreamFactory.class));
	}

	@Test
	public void shouldUseValueInFileIfValueFileSupplied() throws Exception {
		String file = "valueFile";
		when(fileUtils.readFile(file)).thenReturn(VALUE);

		when(context.getValue()).thenReturn(null);
		when(context.getValueFile()).thenReturn(file);

		replacer.replace(context, false, false);
		verify(tokenReplacer).replaceTokens(eq(TOKEN), eq(VALUE), anyBoolean(),
				isA(FileStreamFactory.class));
	}
}
