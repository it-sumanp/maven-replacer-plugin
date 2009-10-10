package bakersoftware.maven_replacer_plugin;

import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import bakersoftware.maven_replacer_plugin.file.FileParameterProvider;
import bakersoftware.maven_replacer_plugin.file.FileStreamFactory;
import bakersoftware.maven_replacer_plugin.file.FileUtils;

/**
 * Goal replaces token with value inside file
 * 
 * @goal replace
 * 
 * @phase compile
 */
public class ReplacerMojo extends AbstractMojo implements FileParameterProvider {
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private final FileUtils fileUtils;
	private final TokenReplacer tokenReplacer;

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

	public ReplacerMojo() {
		super();
		fileUtils = new FileUtils();
		this.tokenReplacer = new TokenReplacer(new FileStreamFactory(this, fileUtils),
				LINE_SEPARATOR);
	}

	public ReplacerMojo(TokenReplacer tokenReplacer, FileUtils fileUtils) {
		this.tokenReplacer = tokenReplacer;
		this.fileUtils = fileUtils;
	}

	public void execute() throws MojoExecutionException {
		try {
			if (token == null && tokenFile == null) {
				throw new MojoExecutionException("Token or token file required");
			}

			if (ignoreMissingFile && !fileUtils.fileExists(file)) {
				getLog().info("Ignoring missing file");
				return;
			}

			String token = this.token;
			if (token == null) {
				token = fileUtils.readFile(tokenFile);
			}

			String value = this.value;
			if (value == null && valueFile != null) {
				value = fileUtils.readFile(valueFile);
			}
			getLog().info("Replacing content in " + file);

			tokenReplacer.replaceTokens(token, value, isRegex());
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}

	public boolean isRegex() {
		return regex;
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

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public String getFile() {
		return file;
	}
}
