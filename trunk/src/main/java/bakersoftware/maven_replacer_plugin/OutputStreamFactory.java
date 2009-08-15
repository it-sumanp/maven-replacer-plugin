package bakersoftware.maven_replacer_plugin;

import java.io.OutputStream;

public interface OutputStreamFactory {
	OutputStream newStream();
}
