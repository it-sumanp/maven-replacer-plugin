package bakersoftware.maven_replacer_plugin;

import java.io.IOException;
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
		this.replacerFactory = new ReplacerFactory(getLog(), fileUtils, tokenReplacer);
		this.tokenValueMapFactory = new TokenValueMapFactory(fileUtils);
	}

	public ReplacerMojo(FileUtils fileUtils, TokenReplacer tokenReplacer,
			ReplacerFactory replacerFactory, TokenValueMapFactory tokenValueMapFactory) {
		this.fileUtils = fileUtils;
		this.tokenReplacer = tokenReplacer;
		this.replacerFactory = replacerFactory;
		this.tokenValueMapFactory = tokenValueMapFactory;
	}

	public void execute() throws MojoExecutionException {
		try {
			Replacer replacer = replacerFactory.create();

			if (tokenValueMap == null) {
				ReplacerContext context = new ReplacerContext(getLog(), file);
				context.setToken(token);
				context.setTokenFile(tokenFile);
				context.setValue(value);
				context.setValueFile(valueFile);
				context.setOutputFile(outputFile);
				replacer.replace(context, ignoreMissingFile, regex);
			} else {
				List<ReplacerContext> contexts = tokenValueMapFactory.contextsForFile(
						tokenValueMap, getLog(), file, outputFile);

				// when there is an output file, contexts following need to
				// reference the outputfile so that all contexts are replaced in
				// the same file
				if (outputFile != null && contexts.size() > 1) {
					for (int i = 1; i < contexts.size(); i++) {
						contexts.get(i).setFile(outputFile);
					}
				}
				for (ReplacerContext context : contexts) {
					replacer.replace(context, ignoreMissingFile, regex);
				}
			}
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

	public void setTokenValueMap(String tokenValueMap) {
		this.tokenValueMap = tokenValueMap;
	}
}
