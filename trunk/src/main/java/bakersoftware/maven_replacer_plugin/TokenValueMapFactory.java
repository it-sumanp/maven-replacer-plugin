package bakersoftware.maven_replacer_plugin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import bakersoftware.maven_replacer_plugin.file.FileUtils;

public class TokenValueMapFactory {

	private final FileUtils fileUtils;

	public TokenValueMapFactory(FileUtils fileUtils) {
		this.fileUtils = fileUtils;
	}

	public List<ReplacerContext> contextsForFile(String tokenValueMapFile) throws IOException {
		String contents = fileUtils.readFile(tokenValueMapFile);

		Properties properties = readPropertyFile(contents);
		List<ReplacerContext> contexts = new ArrayList<ReplacerContext>();
		for (Object key : properties.keySet()) {
			String token = String.valueOf(key);
			String value = properties.getProperty(token);
			contexts.add(new ReplacerContext(fileUtils, token, value));
		}
		return contexts;
	}

	private Properties readPropertyFile(String contents) throws IOException {
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
