package com.google.code.maven_replacer_plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
 */
public class ReplacerMojo extends AbstractMojo {
	private final FileUtils fileUtils;
	private final TokenReplacer tokenReplacer;
	private final ReplacerFactory replacerFactory;
	private final TokenValueMapFactory tokenValueMapFactory;
	private final FileSelector fileSelector;
	private final PatternFlagsFactory patternFlagsFactory;

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
	private List<String> includes;

	/**
	 * List of excluded files pattern in ant format. Cannot use with outputFile.
	 *
	 * @parameter expression=""
	 */
	private List<String> excludes;

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
	 * Token
	 *
	 * @parameter expression=""
	 */
	private String token;

	/**
	 * Token file
	 *
	 * @parameter expression=""
	 */
	private String tokenFile;

	/**
	 * Ignore missing files
	 *
	 * @parameter expression=""
	 */
	private boolean ignoreMissingFile;

	/**
	 * Value to replace token with
	 *
	 * @parameter expression=""
	 */
	private String value;

	/**
	 * Value file to read value to replace token with
	 *
	 * @parameter expression=""
	 */
	private String valueFile;

	/**
	 * Token uses regex
	 *
	 * @parameter expression=""
	 */
	private boolean regex = true;

	/**
	 * Output to another file
	 *
	 * @parameter expression=""
	 */
	private String outputFile;

	/**
	 * Output to another dir
	 *
	 * @parameter expression=""
	 */
	private String outputDir = "";

	/**
	 * Map of tokens and respective values to replace with
	 *
	 * @parameter expression=""
	 */
	private String tokenValueMap;

	/**
	 * Optional base directory for each file to replace
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
	 * List of replacements with token/value pairs
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

	public ReplacerMojo() {
		super();
		this.fileUtils = new FileUtils();
		this.tokenReplacer = new TokenReplacer();
		this.replacerFactory = new ReplacerFactory(fileUtils, tokenReplacer);
		this.tokenValueMapFactory = new TokenValueMapFactory(fileUtils);
		this.fileSelector = new FileSelector();
		this.patternFlagsFactory = new PatternFlagsFactory();
	}

	public ReplacerMojo(FileUtils fileUtils, TokenReplacer tokenReplacer,
			ReplacerFactory replacerFactory, TokenValueMapFactory tokenValueMapFactory,
			FileSelector fileSelector, PatternFlagsFactory patternFlagsFactory) {
		super();
		this.fileUtils = fileUtils;
		this.tokenReplacer = tokenReplacer;
		this.replacerFactory = replacerFactory;
		this.tokenValueMapFactory = tokenValueMapFactory;
		this.fileSelector = fileSelector;
		this.patternFlagsFactory = patternFlagsFactory;
	}

	public void execute() throws MojoExecutionException {
		try {
			if (ignoreMissingFile && fileUtils.fileNotExists(file)) {
				getLog().info("Ignoring missing file");
				return;
			}

			Replacer replacer = replacerFactory.create();
			List<Replacement> contexts = getContexts();

			addIncludesFilesAndExcludedFiles();

			if (includes == null || includes.isEmpty()) {
				replaceContents(replacer, contexts, file);
				return;
			}

			for (String file : fileSelector.listIncludes(basedir, includes, excludes)) {
				replaceContents(replacer, contexts, file);
			}
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private String getFilename(String file) {
		return basedir + File.separator + file;
	}

	private void addIncludesFilesAndExcludedFiles() {
		if (filesToInclude != null) {
			String[] splitFiles = filesToInclude.split(",");
			if (includes == null) {
				includes = new ArrayList<String>();
			}
			addToList(Arrays.asList(splitFiles), includes);
		}

		if (filesToExclude != null) {
			String[] splitFiles = filesToExclude.split(",");
			if (excludes == null) {
				excludes = new ArrayList<String>();
			}
			addToList(Arrays.asList(splitFiles), excludes);
		}
	}

	private void addToList(List<String> toAdds, List<String> destination) {
		for (String toAdd : toAdds) {
			destination.add(toAdd.trim());
		}
	}

	private void replaceContents(Replacer replacer, List<Replacement> contexts, String inputFile) throws IOException {
		String outputFileName = getOutputFile(getFilename(inputFile));
		if (outputDir.trim().length() > 0) {
			outputFileName = getFilename(outputDir + File.separator + new File(outputFileName).getName());
		}
		getLog().info("Replacing content in " + getFilename(inputFile));
		getLog().info("Outputting to: " + outputFileName);
		replacer.replace(contexts, regex, getFilename(inputFile), outputFileName, patternFlagsFactory.buildFlags(regexFlags));
	}

	private List<Replacement> getContexts() throws IOException {
		if (replacements != null) {
			return replacements;
		}

		if (tokenValueMap == null) {
			Replacement context = new Replacement(fileUtils, token, value);
			context.setTokenFile(tokenFile);
			context.setValueFile(valueFile);
			return Arrays.asList(context);
		}
		return tokenValueMapFactory.contextsForFile(tokenValueMap, isCommentsEnabled());
	}

	private String getOutputFile(String defaultFilename) {
		if (outputFile == null) {
			return defaultFilename;
		}

		String outputFileName = getFilename(outputFile);
		return outputFileName;
	}

	public void setRegex(boolean regex) {
		this.regex = regex;
	}

	public void setFile(String file) {
		this.file = file;
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
}
