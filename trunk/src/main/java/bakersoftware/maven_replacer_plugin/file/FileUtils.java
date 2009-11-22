package bakersoftware.maven_replacer_plugin.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileUtils {
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public boolean fileNotExists(String filename) {
		return !new File(filename).exists();
	}

	public void ensureFolderStructureExists(String file) {
		File outputFile = new File(file);
		if (outputFile.getParent() == null) {
			return;
		}

		if (!outputFile.isDirectory()) {
			File parentPath = new File(outputFile.getParent());
			if (!parentPath.exists()) {
				parentPath.mkdirs();
			}
		} else {
			throw new IllegalArgumentException("OutputFile cannot be a directory: " + file);
		}
	}

	public String readFile(String file) throws IOException {
		StringBuilder contents = new StringBuilder();
		BufferedReader input = new BufferedReader(new FileReader(file));
		try {
			String line = null;
			while ((line = input.readLine()) != null) {
				contents.append(line).append(LINE_SEPARATOR);
			}
		} finally {
			input.close();
		}

		return contents.toString().trim();
	}
}
