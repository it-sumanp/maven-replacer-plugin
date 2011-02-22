package com.google.code.maven_replacer_plugin;

import java.io.File;

import com.google.code.maven_replacer_plugin.file.FileUtils;

public class OutputFilenameBuilder {
	
	private final FileUtils fileUtils;

	public OutputFilenameBuilder() {
		fileUtils = new FileUtils();
	}

	public String buildFrom(String inputFilename, ReplacerMojo mojo) {
		if (mojo.getOutputDir() != null) {
			String result = mojo.isPreserveDir() ? inputFilename : getJustFilename(inputFilename);
			if (mojo.getOutputBasedir() != null) {
				return fileUtils.createFullPath(mojo.getOutputBasedir(), mojo.getOutputDir(), result);
			}
			return fileUtils.createFullPath(mojo.getBasedir(), mojo.getOutputDir(), result);
		}
		
		if (mojo.getOutputFile() != null) {
			if (mojo.getOutputFile().startsWith("/")) {
				return fileUtils.createFullPath(mojo.getOutputFile());
			} else {
				return fileUtils.createFullPath(mojo.getBasedir(), mojo.getOutputFile());
			}
		}
		return fileUtils.createFullPath(mojo.getBasedir(), inputFilename);
	}

	private String getJustFilename(String inputFilename) {
		return new File(inputFilename).getName();
	}

}
