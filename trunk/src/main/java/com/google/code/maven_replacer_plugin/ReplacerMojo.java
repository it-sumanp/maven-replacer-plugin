package com.google.code.maven_replacer_plugin;

import static org.apache.commons.lang.StringUtils.isEmpty;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import com.google.code.maven_replacer_plugin.file.FileUtils;
import com.google.code.maven_replacer_plugin.include.FileSelector;


/**
 * Goal replaces token with value inside file
 *
 * @goal replace
 *
 * @phase compile
 * 
 * @threadSafe
 * 
 */
public class ReplacerMojo extends AbstractMojo {
	private static final String INVALID_IGNORE_MISSING_FILE_MESSAGE = "<ignoreMissingFile> only useable with <file>";
	private static final String REGEX_PATTERN_WITH_DELIMITERS_MESSAGE = "Error: %s. " +
		"Check that your delimiters do not contain regex characters. (e.g. '$'). " +
		"Either remove the regex characters from your delimiters or set <regex>false</regex>" +
		" in your configuration.";
	
	private final FileUtils fileUtils;
	private final ReplacerFactory replacerFactory;
	private final TokenValueMapFactory tokenValueMapFactory;
	private final FileSelector fileSelector;
	private final PatternFlagsFactory patternFlagsFactory;
	private final OutputFilenameBuilder outputFilenameBuilder;
	private final SummaryBuilder summaryBuilder;
	private final ReplacementProcessor processor;

	/**
	 * File to check and replace tokens
	 *
	 * @parameter expression=""
	 */
	private String file;

	/**
	 * List of included files pattern in ant format. Cannot use with outputFile.
	 *
	 * @parameter expression=""
	 */
	private List<String> includes = new ArrayList<String>();

	/**
	 * List of excluded files pattern in ant format. Cannot use with outputFile.
	 *
	 * @parameter expression=""
	 */
	private List<String> excludes = new ArrayList<String>();

	/**
	 * Comma separated list of includes. This is split up and used the same way a array of includes would be.
	 *
	 * @parameter expression=""
	 */
	private String filesToInclude;

	/**
	 * Comma separated list of excludes. This is split up and used the same way a array of excludes would be.
	 *
	 * @parameter expression=""
	 */
	private String filesToExclude;

	/**
	 * Token to replace.
	 *
	 * @parameter expression=""
	 */
	private String token;

	/**
	 * Token file containing a token to be replaced in the target file/s.
	 *
	 * @parameter expression=""
	 */
	private String tokenFile;

	/**
	 * Ignore missing target file. Use only with file (not includes etc).
	 *
	 * @parameter expression=""
	 */
	private boolean ignoreMissingFile;

	/**
	 * Value to replace token with.
	 *
	 * @parameter expression=""
	 */
	private String value;

	/**
	 * Value file to read value to replace token with.
	 *
	 * @parameter expression=""
	 */
	private String valueFile;

	/**
	 * Find the token with regex. Set to false when the token to be 
	 * replaced contains regex characters.
	 *
	 * @parameter expression=""
	 */
	private boolean regex = true;

	/**
	 * Output to another file.
	 *
	 * @parameter expression=""
	 */
	private String outputFile;

	/**
	 * Output to another dir.
	 *
	 * @parameter expression=""
	 */
	private String outputDir;

	/**
	 * Map of tokens and respective values to replace with.
	 *
	 * @parameter expression=""
	 */
	private String tokenValueMap;

	/**
	 * Optional base directory for each file to replace.
	 *
	 * @parameter expression="${basedir}"
	 */
	private String basedir = ".";

	/**
	 * List of regex flags.
	 * Must contain one or more of:
	 * * CANON_EQ
	 * * CASE_INSENSITIVE
	 * * COMMENTS
	 * * DOTALL
	 * * LITERAL
	 * * MULTILINE
	 * * UNICODE_CASE
	 * * UNIX_LINES
	 *
	 * @parameter expression=""
	 */
	private List<String> regexFlags;

	/**
	 * List of replacements with token/value pairs.
	 *
	 * @parameter expression=""
	 */
	private List<Replacement> replacements;

	/**
	 * Comments enabled in the tokenValueMapFile. Default is true.
	 * Comment lines start with '#'
	 *
	 * @parameter expression=""
	 */
	private boolean commentsEnabled = true;
	
	/**
	 * Base directory (appended) to use for outputDir.
	 * Having this existing but blank will cause the outputDir
	 * to be based on the execution directory. 
	 *
	 * @parameter expression=""
	 */
	private String outputBasedir;
	
	/**
	 * Parent directory is preserved when replacing files found from includes and 
	 * being written to an outputDir. Default is true.
	 *
	 * @parameter expression=""
	 */
	private boolean preserveDir = true;

	/**
	 * Print a summary of how many files were replaced in this execution. 
	 * Default is false.
	 *
	 * @parameter expression=""
	 */
	private boolean quiet = false;

	/**
	 * Unescape tokens and values to Java format.
	 * e.g. token\n is unescaped to token(carriage return).
	 * Default is false.
	 *
	 * @parameter expression=""
	 */
	private boolean unescape;
	
	/**
	 * List of delimiters.
	 * e.g. "${*}" would match tokens as ${token}
	 * and "@" would match tokens as @token@
	 *
	 * @parameter expression=""
	 */
	private List<String> delimiters = new ArrayList<String>();
	
	/**
	 * Variable tokenValueMap. Same as the tokenValueMap but 
	 * can be an include configuration rather than an outside property file.
	 * Comments are not supported.
	 * Format is comma separated. e.g. token=value,token2=value2
	 *
	 * @parameter expression=""
	 */
	private String variableTokenValueMap;
	
	/**
	 * Ignore any errors produced by this plugin such as 
	 * files not being found and continue with the build.
	 * Default is false.
	 *
	 * @parameter expression=""
	 */
	private boolean ignoreErrors;
	
	/**
	 * X-Path expression for locating node's whose content you wish to replace.
	 * This is useful if you have the same token appearing in many nodes but 
	 * wish to only replace the contents of one or more of them.
	 *
	 * @parameter expression=""
	 */
	private String xpath;
	
	public ReplacerMojo() {
		super();
		this.fileUtils = new FileUtils();
		this.replacerFactory = new ReplacerFactory();
		this.tokenValueMapFactory = new TokenValueMapFactory(fileUtils);
		this.fileSelector = new FileSelector();
		this.patternFlagsFactory = new PatternFlagsFactory();
		this.outputFilenameBuilder = new OutputFilenameBuilder();
		this.summaryBuilder = new SummaryBuilder();
		this.processor = new ReplacementProcessor(fileUtils, replacerFactory);
	}

	public ReplacerMojo(FileUtils fileUtils, ReplacementProcessor processor, ReplacerFactory replacerFactory, 
			TokenValueMapFactory tokenValueMapFactory, FileSelector fileSelector, 
			PatternFlagsFactory patternFlagsFactory, OutputFilenameBuilder outputFilenameBuilder,
			SummaryBuilder summaryBuilder) {
		super();
		this.fileUtils = fileUtils;
		this.processor = processor;
		this.replacerFactory = replacerFactory;
		this.tokenValueMapFactory = tokenValueMapFactory;
		this.fileSelector = fileSelector;
		this.patternFlagsFactory = patternFlagsFactory;
		this.outputFilenameBuilder = outputFilenameBuilder;
		this.summaryBuilder = summaryBuilder;
	}

	public void execute() throws MojoExecutionException {
		try {
			if (checkFileExists()) {
				getLog().info("Ignoring missing file");
				return;
			}

			List<Replacement> replacements = getDelimiterReplacements(buildReplacements());
			addIncludesFilesAndExcludedFiles();

			if (includes.isEmpty()) {
				replaceContents(processor, replacements, file);
				return;
			}

			for (String file : fileSelector.listIncludes(basedir, includes, excludes)) {
				replaceContents(processor, replacements, file);
			}
		} catch (Exception e) {
			getLog().error(e.getMessage());
			if (!isIgnoreErrors()) {
				throw new MojoExecutionException(e.getMessage(), e);
			}
		} finally {
			if (!quiet) {
				summaryBuilder.print(getLog());
			}
		}
	}

	private boolean checkFileExists() throws MojoExecutionException {
		if (ignoreMissingFile && file == null) {
			getLog().error(INVALID_IGNORE_MISSING_FILE_MESSAGE);
			throw new MojoExecutionException(INVALID_IGNORE_MISSING_FILE_MESSAGE);
		}
		return ignoreMissingFile && fileUtils.fileNotExists(getBaseDirPrefixedFilename(file));
	}

	private String getBaseDirPrefixedFilename(String file) {
		if (isEmpty(basedir)) {
			return file;
		}
		return basedir + File.separator + file;
	}

	private void addIncludesFilesAndExcludedFiles() {
		if (filesToInclude != null) {
			String[] splitFiles = filesToInclude.split(",");
			addToList(Arrays.asList(splitFiles), includes);
		}

		if (filesToExclude != null) {
			String[] splitFiles = filesToExclude.split(",");
			addToList(Arrays.asList(splitFiles), excludes);
		}
	}

	private void addToList(List<String> toAdds, List<String> destination) {
		for (String toAdd : toAdds) {
			destination.add(toAdd.trim());
		}
	}

	private void replaceContents(ReplacementProcessor processor, List<Replacement> replacements, String inputFile) throws IOException {
		String outputFileName = outputFilenameBuilder.buildFrom(inputFile, this);
		try {
			processor.replace(replacements, regex, getBaseDirPrefixedFilename(inputFile), outputFileName, patternFlagsFactory.buildFlags(regexFlags));
		} catch (PatternSyntaxException e) {
			if (!delimiters.isEmpty()) {
				getLog().error(String.format(REGEX_PATTERN_WITH_DELIMITERS_MESSAGE, e.getMessage()));
				throw e;
			}
		}
		summaryBuilder.add(getBaseDirPrefixedFilename(inputFile), outputFileName, getLog());
	}

	private List<Replacement> buildReplacements() throws IOException {
		if (replacements != null) {
			return replacements;
		}
		
		if (variableTokenValueMap != null) {
			return tokenValueMapFactory.replacementsForVariable(variableTokenValueMap, isCommentsEnabled(), unescape);
		}
		
		if (tokenValueMap == null) {
			Replacement replacement = new Replacement(fileUtils, token, value, unescape, xpath);
			replacement.setTokenFile(tokenFile);
			replacement.setValueFile(valueFile);
			return Arrays.asList(replacement);
		}
		
		return tokenValueMapFactory.replacementsForFile(getBaseDirPrefixedFilename(tokenValueMap), isCommentsEnabled(), unescape);
	}

	private List<Replacement> getDelimiterReplacements(List<Replacement> replacements) {
		if (delimiters.isEmpty()) {
			return replacements;
		}
		
		List<Replacement> newReplacements = new ArrayList<Replacement>();
		for (Replacement replacement : replacements) {
			for (DelimiterBuilder delimiter : buildDelimiters()) {
				Replacement withDelimiter = Replacement.from(replacement).withDelimiter(delimiter);
				newReplacements.add(withDelimiter);
			}
		}
		return newReplacements;
	}
	
	private List<DelimiterBuilder> buildDelimiters() {
		List<DelimiterBuilder> built = new ArrayList<DelimiterBuilder>();
		for (String delimiter : delimiters) {
			built.add(new DelimiterBuilder(delimiter));
		}
		return built;
	}

	public void setRegex(boolean regex) {
		this.regex = regex;
	}

	public void setFile(String file) {
		this.file = file;
	}
	
	public String getFile() {
		return file;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setTokenFile(String tokenFile) {
		this.tokenFile = tokenFile;
	}

	public void setValueFile(String valueFile) {
		this.valueFile = valueFile;
	}

	public void setIgnoreMissingFile(boolean ignoreMissingFile) {
		this.ignoreMissingFile = ignoreMissingFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public void setTokenValueMap(String tokenValueMap) {
		this.tokenValueMap = tokenValueMap;
	}

	public void setFilesToInclude(String filesToInclude) {
		this.filesToInclude = filesToInclude;
	}

	public void setFilesToExclude(String filesToExclude) {
		this.filesToExclude = filesToExclude;
	}

	public void setBasedir(String baseDir) {
		this.basedir = baseDir;
	}

	public void setReplacements(List<Replacement> replacements) {
		this.replacements = replacements;
	}

	public void setRegexFlags(List<String> regexFlags) {
		this.regexFlags = regexFlags;
	}

	public void setIncludes(List<String> includes) {
		this.includes = includes;
	}

	public List<String> getIncludes() {
		return includes;
	}

	public void setExcludes(List<String> excludes) {
		this.excludes = excludes;
	}

	public List<String> getExcludes() {
		return excludes;
	}

	public String getFilesToInclude() {
		return filesToInclude;
	}

	public String getFilesToExclude() {
		return filesToExclude;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	public boolean isCommentsEnabled() {
		return commentsEnabled;
	}

	public void setCommentsEnabled(boolean commentsEnabled) {
		this.commentsEnabled = commentsEnabled;
	}

	public void setOutputBasedir(String outputBasedir) {
		this.outputBasedir = outputBasedir;
	}
	
	public boolean isPreserveDir() {
		return preserveDir;
	}
	
	public void setPreserveDir(boolean preserveDir) {
		this.preserveDir = preserveDir;
	}

	public String getBasedir() {
		return basedir;
	}

	public String getOutputDir() {
		return outputDir;
	}

	public String getOutputBasedir() {
		return outputBasedir;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setQuiet(boolean quiet) {
		this.quiet = quiet;
	}

	public void setDelimiters(List<String> delimiters) {
		this.delimiters = delimiters;
	}

	public List<String> getDelimiters() {
		return delimiters;
	}

	public void setUnescape(boolean unescape) {
		this.unescape = unescape;
	}
	
	public boolean isUnescape() {
		return unescape;
	}

	public void setVariableTokenValueMap(String variableTokenValueMap) {
		this.variableTokenValueMap = variableTokenValueMap;
	}
	
	public String getVariableTokenValueMap() {
		return variableTokenValueMap;
	}

	public void setIgnoreErrors(boolean ignoreErrors) {
		this.ignoreErrors = ignoreErrors;
	}

	public boolean isIgnoreErrors() {
		return ignoreErrors;
	}

	public void setXpath(String xpath) {
		this.xpath = xpath;
	}
}
