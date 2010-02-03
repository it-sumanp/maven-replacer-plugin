package com.google.code.maven_replacer_plugin;

import java.io.IOException;

import com.google.code.maven_replacer_plugin.file.FileUtils;


public class Replacement {
	private final FileUtils fileUtils;

	private String token;
	private String value;
	
	public Replacement() {
		this.fileUtils = new FileUtils();
	}

	public Replacement(FileUtils fileUtils, String token, String value) {
		this.fileUtils = fileUtils;
		this.token = token;
		this.value = value;
	}

	public void setTokenFile(String tokenFile) throws IOException {
		if (tokenFile != null) {
			token = fileUtils.readFile(tokenFile).trim();
		}
	}

	public void setValueFile(String valueFile) throws IOException {
		if (valueFile != null) {
			value = fileUtils.readFile(valueFile).trim();
		}
	}

	public String getToken() {
		return token;
	}

	public String getValue() {
		return value;
	}
	
	public void setToken(String token) {
		this.token = token;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
