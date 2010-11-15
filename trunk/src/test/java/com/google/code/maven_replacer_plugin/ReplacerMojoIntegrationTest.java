package com.google.code.maven_replacer_plugin;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Before;
import org.junit.Test;

public class ReplacerMojoIntegrationTest {
	private static final String TOKEN = "token";
	private static final String VALUE = "value";
	private static final String OUTPUT_DIR = "target/outputdir/";
	
	private ReplacerMojo mojo;
	private String filenameAndPath;

	@Before
	public void setUp() throws Exception {
		filenameAndPath = createTempFile(TOKEN);
		
		mojo = new ReplacerMojo();
	}
	
	@Test
	public void shouldReplaceContentsInFile() throws Exception {
		mojo.setFile(filenameAndPath);
		mojo.setToken(TOKEN);
		mojo.setValue(VALUE);
		mojo.execute();
		
		String results = FileUtils.readFileToString(new File(filenameAndPath));
		assertThat(results, equalTo(VALUE));
	}
	
	@Test
	public void shouldReplaceRegexCharContentsInFile() throws Exception {
		filenameAndPath = createTempFile("$to*ken+");
		
		mojo.setRegex(false);
		mojo.setFile(filenameAndPath);
		mojo.setToken("$to*ken+");
		mojo.setValue(VALUE);
		mojo.execute();
		
		String results = FileUtils.readFileToString(new File(filenameAndPath));
		assertThat(results, equalTo(VALUE));
	}
	
	@Test
	public void shouldRegexReplaceContentsInFile() throws Exception {
		mojo.setFile(filenameAndPath);
		mojo.setToken("(.+)");
		mojo.setValue("$1" + VALUE);
		mojo.execute();
		
		String results = FileUtils.readFileToString(new File(filenameAndPath));
		assertThat(results, equalTo(TOKEN + VALUE));
	}
	
	@Test
	public void shouldReplaceContentsAndWriteToOutputDirWithBaseDirAndPreservingAsDefault() throws Exception {
		mojo.setBasedir(".");
		mojo.setFile(filenameAndPath);
		mojo.setToken(TOKEN);
		mojo.setValue(VALUE);
		mojo.setOutputDir(OUTPUT_DIR);
		mojo.execute();
		
		String results = FileUtils.readFileToString(new File("./" + OUTPUT_DIR + filenameAndPath));
		assertThat(results, equalTo(VALUE));
	}
	
	@Test
	public void shouldNotReplaceIfIgnoringMissingFilesAndFileNotExists() throws Exception {
		assertFalse(new File("bogus").exists());
		mojo.setFile("bogus");
		mojo.setIgnoreMissingFile(true);
		
		mojo.execute();
		
		assertFalse(new File("bogus").exists());
	}
	
	@Test (expected = MojoExecutionException.class)
	public void shouldRethrowIOExceptionsAsMojoExceptions() throws Exception {
		mojo.setFile("bogus");
		mojo.execute();
	}
	
	@Test
	public void shouldReplaceContentsWithTokenValuesInTokenAndValueFiles() throws Exception {
		String tokenFilename = createTempFile(TOKEN);
		String valueFilename = createTempFile(VALUE);
		
		mojo.setFile(filenameAndPath);
		mojo.setTokenFile(tokenFilename);
		mojo.setValueFile(valueFilename);
		mojo.execute();
		
		String results = FileUtils.readFileToString(new File(filenameAndPath));
		assertThat(results, equalTo(VALUE));
	}
	
	@Test
	public void shouldReplaceContentsWithTokenValuesInMap() throws Exception {
		String tokenValueMapFilename = createTempFile(asList("#comment", TOKEN + "=" + VALUE));
		
		mojo.setTokenValueMap(tokenValueMapFilename);
		mojo.setFile(filenameAndPath);
		mojo.execute();
		
		String results = FileUtils.readFileToString(new File(filenameAndPath));
		assertThat(results, equalTo(VALUE));
	}
	
	@Test
	public void shouldReplaceContentsInFilesToInclude() throws Exception {
		String include1 = createTempFile(TOKEN);
		String include2 = createTempFile(TOKEN);
		
		mojo.setFilesToInclude(include1 + ", " + include2);
		mojo.setToken(TOKEN);
		mojo.setValue(VALUE);
		mojo.execute();
		
		String include1Results = FileUtils.readFileToString(new File(include1));
		assertThat(include1Results, equalTo(VALUE));
		String include2Results = FileUtils.readFileToString(new File(include2));
		assertThat(include2Results, equalTo(VALUE));
	}
	
	@Test
	public void shouldReplaceContentsInIncludeButNotExcludesAndNotPreserveWhenDisabled() throws Exception {
		String include1 = createTempFile("test/prefix1", TOKEN);
		String include2 = createTempFile("test/prefix2", TOKEN);
		String exclude = createTempFile(TOKEN);
		List<String> includes = asList("target/**/prefix*");
		List<String> excludes = asList(exclude);
		
		mojo.setPreserveDir(false);
		mojo.setIncludes(includes);
		mojo.setExcludes(excludes);
		mojo.setToken(TOKEN);
		mojo.setValue(VALUE);
		mojo.execute();
		
		String include1Results = FileUtils.readFileToString(new File(include1));
		assertThat(include1Results, equalTo(VALUE));
		String include2Results = FileUtils.readFileToString(new File(include2));
		assertThat(include2Results, equalTo(VALUE));
		String excludeResults = FileUtils.readFileToString(new File(exclude));
		assertThat(excludeResults, equalTo(TOKEN));
	}
	
	@Test
	public void shouldPreserveFilePathWhenUsingIncludesAndOutputDir() throws Exception {
		String include1 = createTempFile("test/prefix1", TOKEN);
		String include2 = createTempFile("test/prefix2", TOKEN);
		String exclude = createTempFile(TOKEN);
		List<String> includes = asList("target/**/prefix*");
		List<String> excludes = asList(exclude);
		
		mojo.setIncludes(includes);
		mojo.setExcludes(excludes);
		mojo.setToken(TOKEN);
		mojo.setValue(VALUE);
		mojo.execute();
		
		String include1Results = FileUtils.readFileToString(new File(include1));
		assertThat(include1Results, equalTo(VALUE));
		String include2Results = FileUtils.readFileToString(new File(include2));
		assertThat(include2Results, equalTo(VALUE));
		String excludeResults = FileUtils.readFileToString(new File(exclude));
		assertThat(excludeResults, equalTo(TOKEN));
	}
	
	@Test
	public void shouldReplaceContentsAndWriteToOutputFile() throws Exception {
		String outputFilename = createTempFile("");
		
		mojo.setFile(filenameAndPath);
		mojo.setToken(TOKEN);
		mojo.setValue(VALUE);
		mojo.setOutputFile(outputFilename);
		mojo.execute();
		
		String results = FileUtils.readFileToString(new File(outputFilename));
		assertThat(results, equalTo(VALUE));
	}
	
	@Test
	public void shouldReplaceContentsInReplacementsInSameFileWhenNoOutputFile() throws Exception {
		Replacement replacement = new Replacement();
		replacement.setToken(TOKEN);
		replacement.setValue(VALUE);
		List<Replacement> replacements = asList(replacement);
		
		mojo.setReplacements(replacements);
		mojo.setFile(filenameAndPath);
		mojo.execute();
		
		String results = FileUtils.readFileToString(new File(filenameAndPath));
		assertThat(results, equalTo(VALUE));
	}
	
	@Test
	public void shouldWriteToOutputDirBasedOnOutputBaseDir() throws Exception {
		mojo.setOutputBasedir("target/outputBasedir");
		mojo.setFile(filenameAndPath);
		mojo.setToken(TOKEN);
		mojo.setValue(VALUE);
		mojo.setOutputDir(OUTPUT_DIR);
		mojo.execute();
		
		String results = FileUtils.readFileToString(new File("target/outputBasedir/" + OUTPUT_DIR + filenameAndPath));
		assertThat(results, equalTo(VALUE));
	}
	
	private String createTempFile(String contents) throws IOException {
		String filename = new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName();
		return createTempFile(filename, contents);
	}
	
	private String createTempFile(String filename, String contents) throws IOException {
		com.google.code.maven_replacer_plugin.file.FileUtils utils = new com.google.code.maven_replacer_plugin.file.FileUtils();
		String fullname = "target/" + filename + new Random().nextInt();
		utils.ensureFolderStructureExists(fullname);
		File file = new File(fullname);
		FileUtils.writeStringToFile(file, contents);
		file.deleteOnExit();
		return fullname;
	}
	
	private String createTempFile(List<String> contents) throws IOException {
		String filename = new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName();
		File file = new File("target/" + filename);
		FileUtils.writeLines(file, contents);
		file.deleteOnExit();
		return "target/" + file.getName();
	}
}
