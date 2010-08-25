package com.google.code.maven_replacer_plugin.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

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

	public void writeToFile(String outputFile, String content) throws IOException {
		ensureFolderStructureExists(outputFile);
		
		Writer writer = new OutputStreamWriter(new FileOutputStream(outputFile));
		writer.write(content);
		writer.close();
	}
}
