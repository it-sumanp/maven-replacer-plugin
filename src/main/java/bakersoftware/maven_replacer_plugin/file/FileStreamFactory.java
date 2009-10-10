package bakersoftware.maven_replacer_plugin.file;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileStreamFactory implements StreamFactory {
	private final FileUtils fileUtils;
	private final FileParameterProvider fileParameterProvider;

	public FileStreamFactory(FileParameterProvider fileParameterProvider, FileUtils fileUtils) {
		this.fileParameterProvider = fileParameterProvider;
		this.fileUtils = fileUtils;
	}

	public InputStream getNewInputStream() {
		try {
			return new FileInputStream(fileParameterProvider.getFile());
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public OutputStream getNewOutputStream() {
		try {
			String outputFile = fileParameterProvider.getOutputFile();
			if (outputFile != null) {
				fileParameterProvider.getLog().info("Outputting to: " + outputFile);
				if (!fileUtils.fileExists(outputFile)) {
					fileUtils.ensureFolderStructureExists(outputFile);
				}
				return new FileOutputStream(outputFile);
			} else {
				return new FileOutputStream(fileParameterProvider.getFile());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
