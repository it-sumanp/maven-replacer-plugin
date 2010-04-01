package com.google.code.maven_replacer_plugin.include;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class FileSelectorTest {
	private static final String PATH_SEPARATOR = File.separator;
	
	@Test
	public void shouldReturnMultipleFilesToInclude() {
		FileSelector selector = new FileSelector();
		List<String> files = selector.listIncludes(asList("test/include1", "test/file*"), asList("test/file3"));
		assertEquals(3, files.size());
		assertEquals("test" + PATH_SEPARATOR + "file1", files.get(0));
		assertEquals("test" + PATH_SEPARATOR + "file2", files.get(1));
		assertEquals("test" + PATH_SEPARATOR + "include1", files.get(2));
	}
}
