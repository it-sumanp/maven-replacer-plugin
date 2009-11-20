package bakersoftware.maven_replacer_plugin;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

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

	@Mock
	private TokenValueMapFactory tokenValueMapFactory;

	private ReplacerMojo replacerMojo;

	@Before
	public void setUp() {
		when(replacerFactory.create()).thenReturn(replacer);

		replacerMojo = new ReplacerMojo(fileUtils, tokenReplacer, replacerFactory,
				tokenValueMapFactory);
		replacerMojo.setToken(TOKEN);
		replacerMojo.setValue(VALUE);
	}

	@Test
	public void shouldReplaceRegexTokensInFile() throws Exception {
		replacerMojo.setRegex(true);
		replacerMojo.execute();
		verify(replacer).replace(argThat(new ContextMatcher(TOKEN, VALUE)), anyBoolean(), eq(true));
	}

	@Test
	public void shouldReplaceNonRegexTokensInFile() throws Exception {
		replacerMojo.setRegex(false);
		replacerMojo.execute();
		verify(replacer)
				.replace(argThat(new ContextMatcher(TOKEN, VALUE)), anyBoolean(), eq(false));
	}

	@Test
	public void shouldReplaceBackslashContainingValueInFile() throws Exception {
		replacerMojo.setRegex(true);
		replacerMojo.setValue("some\\value");
		replacerMojo.execute();
		verify(replacer).replace(argThat(new ContextMatcher(TOKEN, "some\\value")), anyBoolean(),
				eq(true));
	}

	@Test(expected = MojoExecutionException.class)
	public void shouldThrowMojoExceptionWhenIOException() throws MojoExecutionException,
			IOException {
		doThrow(new IOException()).when(replacer).replace(isA(ReplacerContext.class), anyBoolean(),
				anyBoolean());

		replacerMojo.execute();
		verify(replacer).replace(isA(ReplacerContext.class), anyBoolean(), anyBoolean());
	}

	@Test
	public void shouldReplaceTokensWithRespectiveValuesInPropertiesFile() throws Exception {
		replacerMojo.setTokenValueMap("tokenValueMap");

		ReplacerContext context1 = mock(ReplacerContext.class);
		when(context1.getToken()).thenReturn(TOKEN);
		when(context1.getValue()).thenReturn(VALUE);
		ReplacerContext context2 = mock(ReplacerContext.class);
		when(context2.getToken()).thenReturn("other token");
		when(context2.getValue()).thenReturn("other value");

		List<ReplacerContext> contexts = asList(context1, context2);
		when(tokenValueMapFactory.contextsForFile("tokenValueMap", replacerMojo.getLog(), null))
				.thenReturn(contexts);

		replacerMojo.execute();
		verify(replacer).replace(argThat(new ContextMatcher(TOKEN, VALUE)), anyBoolean(), eq(true));
		verify(replacer).replace(argThat(new ContextMatcher("other token", "other value")),
				anyBoolean(), eq(true));
	}

	private static class ContextMatcher extends ArgumentMatcher<ReplacerContext> {
		private final String token;
		private final String value;

		public ContextMatcher(String token, String value) {
			this.token = token;
			this.value = value;
		}

		@Override
		public boolean matches(Object argument) {
			ReplacerContext context = (ReplacerContext) argument;
			return context.getToken().equals(token) && context.getValue().equals(value);
		}

	}
}
