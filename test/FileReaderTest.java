import org.junit.*;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Jonathan McDevitt on 2017-03-24.
 */
public class FileReaderTest {
    @Test
    public void readTableFile() throws Exception {
        List<List<String>> table = FileReader.readTableFile("data1");
        assertNotNull(table);
        assertTrue(table.size() > 0);
    }
}