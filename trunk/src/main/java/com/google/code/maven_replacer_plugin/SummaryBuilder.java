package com.google.code.maven_replacer_plugin;

import org.apache.maven.plugin.logging.Log;

public class SummaryBuilder {

	private static final String FILE_DEBUG_FORMAT = "Replacement run on %s and writing to %s";
	private static final String SUMMARY_FORMAT = "Replacement run on %d file%s.";
	
	private int filesReplaced;

	public void add(String inputFile, String outputFile, Log log) {
		log.debug(String.format(FILE_DEBUG_FORMAT, inputFile, outputFile));
		filesReplaced++;
	}

	public void print(Log log) {
		log.info(String.format(SUMMARY_FORMAT, filesReplaced, filesReplaced > 1 ? "s" : ""));
	}

}
