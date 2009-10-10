package bakersoftware.maven_replacer_plugin.file;

import org.apache.maven.plugin.logging.Log;

public interface FileParameterProvider {

	String getFile();

	String getOutputFile();

	Log getLog();

}
