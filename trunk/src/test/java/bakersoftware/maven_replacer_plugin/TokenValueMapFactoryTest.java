package bakersoftware.maven_replacer_plugin;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.junit.Test;

import bakersoftware.maven_replacer_plugin.file.FileUtils;

public class TokenValueMapFactoryTest {
	private static final String TOKEN1 = "token1";
	private static final String VALUE1 = "value1";
	private static final String TOKEN2 = "token2";
	private static final String VALUE2 = "value2";

	private static final String TOKEN_VALUE_MAP = TOKEN1 + "=" + VALUE1 + "\n" + TOKEN2 + "="
			+ VALUE2;

	@Test
	public void shouldCreateListOfReplacerContextsFromFile() throws Exception {
		FileUtils fileUtils = mock(FileUtils.class);

		TokenValueMapFactory factory = new TokenValueMapFactory(fileUtils);
		String tokenValueMapFile = "token value map file";
		Log log = mock(Log.class);
		String file = "file";

		when(fileUtils.readFile(tokenValueMapFile)).thenReturn(TOKEN_VALUE_MAP);
		String outputFile = "outputFile";
		List<ReplacerContext> contexts = factory.contextsForFile(tokenValueMapFile, log, file,
				outputFile);

		assertEquals(2, contexts.size());
		Collections.sort(contexts, new Comparator<ReplacerContext>() {
			public int compare(ReplacerContext o1, ReplacerContext o2) {
				return o1.getToken().compareTo(o2.getToken());
			}
		});
		assertEquals(outputFile, contexts.get(0).getOutputFile());
		assertEquals(outputFile, contexts.get(1).getOutputFile());
		assertEquals(TOKEN1, contexts.get(0).getToken());
		assertEquals(VALUE1, contexts.get(0).getValue());
		assertEquals(TOKEN2, contexts.get(1).getToken());
		assertEquals(VALUE2, contexts.get(1).getValue());
	}
}
