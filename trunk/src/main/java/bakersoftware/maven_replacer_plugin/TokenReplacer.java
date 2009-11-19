package bakersoftware.maven_replacer_plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import bakersoftware.maven_replacer_plugin.file.StreamFactory;

public class TokenReplacer {
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public void replaceTokens(String token, String value, boolean isTokenRegex,
			StreamFactory streamFactory) throws IOException {
		StringBuffer buffer = readContents(streamFactory.getNewInputStream());
		String result = replaceContents(buffer.toString(), token, value, isTokenRegex);

		Writer writer = new OutputStreamWriter(streamFactory.getNewOutputStream());
		writer.write(result);
		writer.close();
	}

	private StringBuffer readContents(InputStream inputStream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

		StringBuffer buffer = new StringBuffer();
		String line = reader.readLine();
		if (line == null) {
			throw new IOException("Could not read from stream");
		}
		while (line != null) {
			buffer.append(line).append(LINE_SEPARATOR);
			line = reader.readLine();
		}
		reader.close();
		return buffer;
	}

	private String replaceContents(String contents, String token, String value, boolean isTokenRegex) {
		String valueToReplaceWith = value == null ? "" : value;
		final String result;
		if (isTokenRegex) {
			result = contents.replaceAll(token, valueToReplaceWith);
		} else {
			result = replaceNonRegex(contents, token, valueToReplaceWith);
		}
		return result;
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
