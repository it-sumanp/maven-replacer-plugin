package bakersoftware.maven_replacer_plugin;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

public class PatternFlagsFactoryTest {
	private PatternFlagsFactory factory;

	@Before
	public void setUp() {
		factory = new PatternFlagsFactory();
	}
	
	@Test
	public void shouldReturnMultiline() throws Exception {
		assertEquals(Pattern.MULTILINE, factory.buildFlags(null));
		assertEquals(Pattern.MULTILINE, factory.buildFlags(new ArrayList<String>()));
	}
}
