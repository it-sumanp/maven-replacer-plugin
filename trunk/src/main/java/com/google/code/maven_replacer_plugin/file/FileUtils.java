package com.google.code.maven_replacer_plugin.file;

import java.io.File;
import java.io.FileInputStream;
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
		StringBuilder contents = new StringBuilder();
		FileInputStream fis = new FileInputStream(file);
		
		byte[] buffer = new byte[1024];
		int len;
		while ((len = fis.read(buffer)) != -1) {
			contents.append(new String(buffer, 0, len));
		}
		return contents.toString();
	}
}
