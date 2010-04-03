package com.google.code.maven_replacer_plugin.include;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class FileSelectorTest {
	@Test
	public void shouldReturnMultipleFilesToInclude() {
		FileSelector selector = new FileSelector();
		List<String> files = selector.listIncludes("test", asList("include1", "file*"), asList("file3"));
		assertEquals(3, files.size());
		assertEquals("file1", files.get(0));
		assertEquals("file2", files.get(1));
		assertEquals("include1", files.get(2));
	}
}
