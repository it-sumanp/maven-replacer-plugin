package com.google.code.maven_replacer_plugin.file;

import java.io.File;
import java.io.IOException;

public class FileUtils {
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
		return org.apache.commons.io.FileUtils.readFileToString(new File(file));
	}
}
