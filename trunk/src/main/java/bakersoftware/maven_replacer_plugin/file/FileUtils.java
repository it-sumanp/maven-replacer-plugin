package bakersoftware.maven_replacer_plugin.file;

import java.io.File;

public class FileUtils {
	public boolean fileExists(String filename) {
		return new File(filename).exists();
	}

	public void ensureFolderStructureExists(String file) {
		File outputFile = new File(file);
		if (!outputFile.isDirectory()) {
			File parentPath = new File(outputFile.getParent());
			if (!parentPath.exists()) {
				parentPath.mkdirs();
			}
		} else {
			throw new IllegalArgumentException("Parameter outputFile cannot be a directory: "
					+ file);
		}
	}
}
