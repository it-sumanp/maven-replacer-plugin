package bakersoftware.maven_replacer_plugin;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import bakersoftware.maven_replacer_plugin.file.FileUtils;

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

	/**
	 * File to check and replace tokens
	 * 
	 * @parameter expression=""
	 */
	private String file;

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
	 * Map of tokens and respective values to replace with
	 * 
	 * @parameter expression=""
	 */
	private String tokenValueMap;

	public ReplacerMojo() {
		super();
		this.fileUtils = new FileUtils();
		this.tokenReplacer = new TokenReplacer();
		this.replacerFactory = new ReplacerFactory(fileUtils, tokenReplacer);
		this.tokenValueMapFactory = new TokenValueMapFactory(fileUtils);
	}

	public ReplacerMojo(FileUtils fileUtils, TokenReplacer tokenReplacer,
			ReplacerFactory replacerFactory, TokenValueMapFactory tokenValueMapFactory) {
		super();
		this.fileUtils = fileUtils;
		this.tokenReplacer = tokenReplacer;
		this.replacerFactory = replacerFactory;
		this.tokenValueMapFactory = tokenValueMapFactory;
	}

	public void execute() throws MojoExecutionException {
		try {
			getLog().info("Replacing content in " + file);
			if (ignoreMissingFile && fileUtils.fileNotExists(file)) {
				getLog().info("Ignoring missing file");
				return;
			}

			replacerFactory.create().replace(getContexts(), regex, file, getOutputFile());
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private List<ReplacerContext> getContexts() throws IOException {
		if (tokenValueMap == null) {
			ReplacerContext context = new ReplacerContext(fileUtils, token, value);
			context.setTokenFile(tokenFile);
			context.setValueFile(valueFile);
			return Arrays.asList(context);
		}
		return tokenValueMapFactory.contextsForFile(tokenValueMap);
	}

	private String getOutputFile() {
		if (outputFile == null) {
			return file;
		}

		getLog().info("Outputting to: " + outputFile);
		if (fileUtils.fileNotExists(file)) {
			fileUtils.ensureFolderStructureExists(outputFile);
		}
		return outputFile;
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
}
