package bakersoftware.maven_replacer_plugin;

import java.io.IOException;

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
		this.fileUtils = new FileUtils();
		this.tokenReplacer = new TokenReplacer();
		this.replacerFactory = new ReplacerFactory(getLog(), fileUtils, tokenReplacer);
	}

	public ReplacerMojo(FileUtils fileUtils, TokenReplacer tokenReplacer,
			ReplacerFactory replacerFactory) {
		this.fileUtils = fileUtils;
		this.tokenReplacer = tokenReplacer;
		this.replacerFactory = replacerFactory;
	}

	public void execute() throws MojoExecutionException {
		try {
			ReplacerContext context = new ReplacerContext(getLog(), file, ignoreMissingFile, regex);
			context.setToken(token);
			context.setTokenFile(tokenFile);
			context.setValue(value);
			context.setValueFile(valueFile);
			context.setOutputFile(outputFile);

			Replacer replacer = replacerFactory.create();
			replacer.replace(context);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
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
}
