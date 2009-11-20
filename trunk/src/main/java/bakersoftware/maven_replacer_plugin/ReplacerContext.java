package bakersoftware.maven_replacer_plugin;

import org.apache.maven.plugin.logging.Log;

import bakersoftware.maven_replacer_plugin.file.FileParameterProvider;

public class ReplacerContext implements FileParameterProvider {
	private final Log log;

	private String file;
	private String outputFile;
	private String token;
	private String tokenFile;
	private String value;
	private String valueFile;

	public ReplacerContext(Log log, String file) {
		this.log = log;
		this.file = file;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setTokenFile(String tokenFile) {
		this.tokenFile = tokenFile;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setValueFile(String valueFile) {
		this.valueFile = valueFile;
	}

	public String getFile() {
		return file;
	}

	public Log getLog() {
		return log;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public String getToken() {
		return token;
	}

	public String getTokenFile() {
		return tokenFile;
	}

	public String getValue() {
		return value;
	}

	public String getValueFile() {
		return valueFile;
	}

	public void setFile(String file) {
		this.file = file;
	}
}
