package bakersoftware.maven_replacer_plugin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.logging.Log;

import bakersoftware.maven_replacer_plugin.file.FileUtils;

public class TokenValueMapFactory {

	private final FileUtils fileUtils;

	public TokenValueMapFactory(FileUtils fileUtils) {
		this.fileUtils = fileUtils;
	}

	public List<ReplacerContext> contextsForFile(String tokenValueMapFile, Log log, String file,
			String outputFile) throws IOException {
		String contents = fileUtils.readFile(tokenValueMapFile);

		Properties properties = new Properties();
		InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
		try {
			properties.load(inputStream);
		} finally {
			inputStream.close();
		}
		List<ReplacerContext> contexts = new ArrayList<ReplacerContext>();
		for (Object key : properties.keySet()) {
			ReplacerContext context = new ReplacerContext(log, file);
			context.setToken(String.valueOf(key));
			context.setValue(properties.getProperty(String.valueOf(key)));
			context.setOutputFile(outputFile);
			contexts.add(context);
		}
		return contexts;
	}

}
