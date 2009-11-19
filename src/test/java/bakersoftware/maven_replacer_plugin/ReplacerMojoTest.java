package bakersoftware.maven_replacer_plugin;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
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

	@Mock
	private ReplacerFactory replacerFactory;

	@Mock
	private Replacer replacer;

	private ReplacerMojo replacerMojo;

	@Before
	public void setUp() {
		when(replacerFactory.create()).thenReturn(replacer);

		replacerMojo = new ReplacerMojo(fileUtils, tokenReplacer, replacerFactory);
		replacerMojo.setToken(TOKEN);
		replacerMojo.setValue(VALUE);
	}

	@Test
	public void shouldReplaceRegexTokensInFile() throws Exception {
		replacerMojo.setRegex(true);
		replacerMojo.execute();
		verify(replacer).replace(argThat(new ContextMatcher(TOKEN, VALUE, true)));
	}

	@Test
	public void shouldReplaceNonRegexTokensInFile() throws Exception {
		replacerMojo.setRegex(false);
		replacerMojo.execute();
		verify(replacer).replace(argThat(new ContextMatcher(TOKEN, VALUE, false)));
	}

	@Test
	public void shouldReplaceBackslashContainingValueInFile() throws Exception {
		replacerMojo.setRegex(true);
		replacerMojo.setValue("some\\value");
		replacerMojo.execute();
		verify(replacer).replace(argThat(new ContextMatcher(TOKEN, "some\\value", true)));
	}

	@Test(expected = MojoExecutionException.class)
	public void shouldThrowMojoExceptionWhenIOException() throws MojoExecutionException,
			IOException {
		doThrow(new IOException()).when(replacer).replace(isA(ReplacerContext.class));

		replacerMojo.execute();
		verify(replacer).replace(isA(ReplacerContext.class));
	}

	private static class ContextMatcher extends ArgumentMatcher<ReplacerContext> {
		private final String token;
		private final String value;
		private final boolean isRegex;

		public ContextMatcher(String token, String value, boolean isRegex) {
			this.token = token;
			this.value = value;
			this.isRegex = isRegex;
		}

		@Override
		public boolean matches(Object argument) {
			ReplacerContext context = (ReplacerContext) argument;
			return context.getToken().equals(token) && context.getValue().equals(value)
					&& context.isRegex() == isRegex;
		}

	}
}
