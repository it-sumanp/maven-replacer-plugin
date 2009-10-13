package bakersoftware.maven_replacer_plugin.file;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FileStreamFactoryTest {
	@Mock
	private FileParameterProvider fileParameterProvider;
	@Mock
	private FileUtils fileUtils;
	@Mock
	private FileStreamFactory factory;
	@Mock
	private Log log;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Before
	public void setUp() throws Exception {
		when(fileParameterProvider.getLog()).thenReturn(log);

		folder.newFile("some file");
		when(fileParameterProvider.getFile()).thenReturn("some file");
		folder.newFile("some outputFile");
		when(fileParameterProvider.getOutputFile()).thenReturn("some outputFile");

		factory = new FileStreamFactory(fileParameterProvider, fileUtils);
	}

	@Test
	public void shouldReturnInputStream() throws Exception {
		assertTrue(factory.getNewInputStream() instanceof FileInputStream);
		verify(fileParameterProvider).getFile();
	}

	@Test
	public void shouldReturnOutputStreamForInputFile() throws Exception {
		when(fileParameterProvider.getOutputFile()).thenReturn(null);
		assertTrue(factory.getNewOutputStream() instanceof FileOutputStream);
		verify(fileParameterProvider).getOutputFile();
		verify(fileParameterProvider).getFile();
	}

	@Test
	public void shouldUseOutputFileIfGiven() throws Exception {
		assertTrue(factory.getNewOutputStream() instanceof FileOutputStream);
		verify(fileParameterProvider).getOutputFile();
		verify(fileParameterProvider, never()).getFile();
	}

	@Test
	public void shouldCreateOutputFileIfGivenButDoesntExist() throws Exception {
		when(fileParameterProvider.getOutputFile()).thenReturn("some new file");

		assertTrue(factory.getNewOutputStream() instanceof FileOutputStream);
		verify(fileParameterProvider).getOutputFile();
		verify(fileParameterProvider, never()).getFile();
		verify(fileUtils).ensureFolderStructureExists("some new file");
	}
}
