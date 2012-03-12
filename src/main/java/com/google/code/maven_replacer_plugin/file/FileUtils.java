package com.google.code.maven_replacer_plugin.file;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.io.File;
import java.io.IOException;

public class FileUtils {
	private String encoding;

	public boolean fileNotExists(String filename) {
		return isBlank(filename) || !new File(filename).exists();
	}

	public void ensureFolderStructureExists(String file) {
		File outputFile = new File(file);
		if (outputFile.getParent() == null) {
			return;
		}

		if (!outputFile.isDirectory()) {
			File parentPath = new File(outputFile.getParent());
			if (!parentPath.exists() && !parentPath.mkdirs()) {
				throw new IllegalStateException("Error creating directory.");
			}
		} else {
			throw new IllegalArgumentException("outputFile cannot be a directory: " + file);
		}
	}

	public String readFile(String file) throws IOException {
		if (encoding != null) {
			return org.apache.commons.io.FileUtils.readFileToString(new File(file), encoding);
		}
		return org.apache.commons.io.FileUtils.readFileToString(new File(file));
	}

	public void writeToFile(String outputFile, String content) throws IOException {
		ensureFolderStructureExists(outputFile);
		if (encoding != null) { 
			org.apache.commons.io.FileUtils.writeStringToFile(new File(outputFile), content, encoding);
		} else {
			org.apache.commons.io.FileUtils.writeStringToFile(new File(outputFile), content);
		}
	}
	
	public String createFullPath(String... dirsAndFilename) {
		StringBuilder fullPath = new StringBuilder();
		for (int i=0; i < dirsAndFilename.length - 1; i++) {
			if (dirsAndFilename[i] != null) {
				fullPath.append(dirsAndFilename[i]);
				fullPath.append(File.separator);
			}
		}
		fullPath.append(dirsAndFilename[dirsAndFilename.length - 1]);
		
		return fullPath.toString();
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}
