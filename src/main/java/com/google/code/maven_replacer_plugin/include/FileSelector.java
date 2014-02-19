package com.google.code.maven_replacer_plugin.include;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.DirectoryScanner;

public class FileSelector {
	/**
		In case basedir is used elsewhere by users. If this value is set OR the basedir is 
		empty, then dont set the directoryscanner's basedir which allows absolute paths to work.
		Dont like doing this, but it may be the only workaround for existing users and the
		turn around time for finding issues and releases is large with maven central.
	*/
	private static final String FLAG_FOR_ABS = "USE_ABSOLUTE_PATH";

	public List<String> listIncludes(String basedir, List<String> includes, List<String> excludes) {
		if (includes == null || includes.isEmpty()) {
			return Collections.emptyList();
		}

		DirectoryScanner directoryScanner = new DirectoryScanner();
		directoryScanner.addDefaultExcludes();
		if (StringUtils.isNotBlank(basedir) && !FLAG_FOR_ABS.equals(basedir)) {
			directoryScanner.setBasedir(new File(basedir));
		}
		directoryScanner.setIncludes(stringListToArray(includes));
		directoryScanner.setExcludes(stringListToArray(excludes));

		directoryScanner.scan();
		return Arrays.asList(directoryScanner.getIncludedFiles());
	}

	private String[] stringListToArray(List<String> stringList) {
		if (stringList == null) {
			return null;
		}
		return stringList.toArray(new String[] {});
	}
}
