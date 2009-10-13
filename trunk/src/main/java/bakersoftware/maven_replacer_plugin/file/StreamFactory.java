package bakersoftware.maven_replacer_plugin.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface StreamFactory {
	InputStream getNewInputStream() throws IOException;

	OutputStream getNewOutputStream() throws IOException;
}
