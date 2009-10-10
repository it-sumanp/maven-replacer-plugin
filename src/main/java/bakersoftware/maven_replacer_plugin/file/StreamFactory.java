package bakersoftware.maven_replacer_plugin.file;

import java.io.InputStream;
import java.io.OutputStream;

public interface StreamFactory {
	InputStream getNewInputStream();

	OutputStream getNewOutputStream();
}
