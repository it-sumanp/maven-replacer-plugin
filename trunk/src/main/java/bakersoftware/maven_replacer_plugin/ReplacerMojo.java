package bakersoftware.maven_replacer_plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Goal replaces token with value inside file
 * 
 * @goal replace
 * 
 * @phase compile
 */
public class ReplacerMojo extends AbstractMojo implements StreamFactory {
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

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

	public void execute() throws MojoExecutionException {
		getLog().info("Replacing " + token + " with " + value + " in " + file);
		try {
			if (ignoreMissingFile && !fileExists(file)) {
				getLog().info("Ignoring missing file");
				return;
			}
			
			TokenReplacer tokenReplacer = getTokenReplacer();
			tokenReplacer.replaceTokens(token, value);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}

	private boolean fileExists(String filename) {
		return new File(filename).exists();
	}
	
	public TokenReplacer getTokenReplacer() {
		return new TokenReplacer(this, LINE_SEPARATOR);
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

	public void setIgnoreMissingFile(boolean ignoreMissingFile) {
		this.ignoreMissingFile = ignoreMissingFile;
	}

	public InputStream getNewInputStream() {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public OutputStream getNewOutputStream() {
		try {
			return new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
