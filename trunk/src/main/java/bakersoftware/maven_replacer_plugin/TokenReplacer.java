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

	public void replaceTokens(String token, String value, boolean isTokenRegex)
			throws IOException {
		StringBuffer buffer = new StringBuffer();

		InputStream inputStream = streamFactory.getNewInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));

		String line = reader.readLine();
		if (line == null) {
			throw new IOException("Could not read from stream");
		}
		while (line != null) {
			if (isTokenRegex) {
				buffer.append(line.replaceAll(token, value));
			} else {
				buffer.append(replaceNonRegex(line, token, value));
			}
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

	private String replaceNonRegex(String input, String token, String value) {
		if (input.length() == 0) {
			return input;
		}

		StringBuffer buffer = new StringBuffer();
		int start = 0;
		int end = 0;
		while ((end = input.indexOf(token, start)) >= 0) {
			buffer.append(input.substring(start, end));
			buffer.append(value);
			start = end + token.length();
		}
		return buffer.append(input.substring(start)).toString();
	}

}
