package bakersoftware.maven_replacer_plugin;

public class TokenReplacer {
	public String replaceContents(String contents, String token, String value, boolean isTokenRegex) {
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
