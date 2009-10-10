package bakersoftware.maven_replacer_plugin.file;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileUtilsTest {
	private FileUtils fileUtils;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Before
	public void setUp() {
		fileUtils = new FileUtils();
	}

	@Test
	public void shouldReturnTrueIfFileExists() throws Exception {
		folder.newFile("some file");
		assertTrue(fileUtils.fileExists("some file"));
	}

	@Test
	public void shouldReturnFalseIfFileDoesNotExist() throws Exception {
		assertFalse(fileUtils.fileExists("some other file"));
	}

	@Test
	public void shouldCreateDirectoryStructure() throws Exception {
		fileUtils.ensureFolderStructureExists("/tmp/somefile");
		File file = new File("/tmp/");
		assertTrue(file.exists());
		file.delete();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentIfOutputFileIsDirectory() throws Exception {
		fileUtils.ensureFolderStructureExists("/tmp/");
		File file = new File("/tmp/");
		assertTrue(file.exists());
		file.delete();
	}
}
