package bakersoftware.maven_replacer_plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class TokenReplacer {
	private final String lineSeparator;
	private final StreamFactory streamFactory;

	public TokenReplacer(StreamFactory streamFactory, String lineSeparator) {
		this.lineSeparator = lineSeparator;
		this.streamFactory = streamFactory;
	}
	
	public void replaceTokens(String token, String value) throws IOException {
		StringBuffer buffer = new StringBuffer();
		
		InputStream inputStream = streamFactory.getNewInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		
		String line = reader.readLine();
		if (line == null) {
			throw new IOException("Could not read from stream");
		}
		while (line != null) {
			buffer.append(line.replaceAll(token, value));
			if (lineSeparator != null) {
				buffer.append(lineSeparator);
			}
			line = reader.readLine();
		}
		reader.close();
		
		Writer writer = new OutputStreamWriter(streamFactory.getNewOutputStream());
		writer.write(buffer.toString());
		writer.close();
	}
}
