package com.google.code.maven_replacer_plugin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.google.code.maven_replacer_plugin.file.FileUtils;


public class TokenValueMapFactory {

	private final FileUtils fileUtils;

	public TokenValueMapFactory(FileUtils fileUtils) {
		this.fileUtils = fileUtils;
	}

	public List<Replacement> contextsForFile(String tokenValueMapFile) throws IOException {
		String contents = fileUtils.readFile(tokenValueMapFile);

		Properties properties = readProperties(contents);
		List<Replacement> contexts = new ArrayList<Replacement>();
		for (Object key : properties.keySet()) {
			String token = String.valueOf(key);
			String value = properties.getProperty(token);
			contexts.add(new Replacement(fileUtils, token, value));
		}
		return contexts;
	}

	private Properties readProperties(String contents) throws IOException {
		Properties properties = new Properties();
		InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
		try {
			properties.load(inputStream);
		} finally {
			inputStream.close();
		}
		return properties;
	}

}
