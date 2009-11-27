package bakersoftware.maven_replacer_plugin;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import bakersoftware.maven_replacer_plugin.file.FileUtils;

@RunWith(MockitoJUnitRunner.class)
public class ReplacerFactoryTest {
	@Mock
	private FileUtils fileUtils;

	@Mock
	private TokenReplacer tokenReplacer;

	@Test
	public void shouldReturnReplacerWithFileUtilsAndTokenReplacer() {
		ReplacerFactory factory = new ReplacerFactory(fileUtils, tokenReplacer);

		Replacer replacer = factory.create();
		assertNotNull(replacer);
		assertSame(fileUtils, replacer.getFileUtils());
		assertSame(tokenReplacer, replacer.getTokenReplacer());
	}
}
