package bakersoftware.maven_replacer_plugin;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import bakersoftware.maven_replacer_plugin.file.FileUtils;

public class Replacer {
	private final FileUtils fileUtils;
	private final TokenReplacer tokenReplacer;

	public Replacer(FileUtils fileUtils, TokenReplacer tokenReplacer) {
		this.fileUtils = fileUtils;
		this.tokenReplacer = tokenReplacer;
	}

	public void replace(List<ReplacerContext> contexts, boolean regex, String file,
			String outputFile) throws IOException {
		String content = fileUtils.readFile(file);
		for (ReplacerContext context : contexts) {
			if (context.getToken() == null) {
				throw new IllegalArgumentException("Token or token file required");
			}

			content = tokenReplacer.replaceContents(content, context.getToken(),
					context.getValue(), regex);
		}

		Writer writer = new OutputStreamWriter(new FileOutputStream(outputFile));
		writer.write(content);
		writer.close();
	}
}
