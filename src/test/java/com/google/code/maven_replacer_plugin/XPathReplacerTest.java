package com.google.code.maven_replacer_plugin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

public class XPathReplacerTest {
	private static final int NO_FLAGS = -1;
	
	private Replacement replacement;
	private TokenReplacer tokenReplacer;
	private XPathReplacer replacer;
	
	@Before
	public void setUp() {
		replacement = mock(Replacement.class);
		tokenReplacer = mock(TokenReplacer.class);
		replacer = new XPathReplacer(tokenReplacer);
	}

	@Test
	public void shouldReplaceNodeStringLocatedByXpath() throws Exception {
		when(replacement.getXpath()).thenReturn("//test");
		when(replacement.getToken()).thenReturn("token");
		when(replacement.getValue()).thenReturn("value");
		
		when(tokenReplacer.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?><test>token</test>", 
				replacement, false, NO_FLAGS)).thenReturn("<test>value</test>");
		
		String result = replacer.replace("<parent><test>token</test></parent>", replacement, false, NO_FLAGS);
		assertThat(result, containsString("<parent><test>value</test></parent>"));
	}
}
