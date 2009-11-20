package bakersoftware.maven_replacer_plugin;

import org.apache.maven.plugin.logging.Log;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import bakersoftware.maven_replacer_plugin.file.FileUtils;

public class ReplacerFactoryTest {
	@Test
	public void shouldReturnReplacerWithLogAndFileUtilsAndTokenReplacer() {
		Log log = mock(Log.class);
		FileUtils fileUtils = mock(FileUtils.class);
		TokenReplacer tokenReplacer = mock(TokenReplacer.class);

		ReplacerFactory factory = new ReplacerFactory(log, fileUtils, tokenReplacer);
		Replacer replacer = factory.create();

		assertSame(replacer.getLog(), log);
		assertSame(replacer.getFileUtils(), fileUtils);
		assertSame(replacer.getTokenReplacer(), tokenReplacer);
	}
}
