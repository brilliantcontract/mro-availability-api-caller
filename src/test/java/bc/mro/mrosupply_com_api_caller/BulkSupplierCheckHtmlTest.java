package bc.mro.mrosupply_com_api_caller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class BulkSupplierCheckHtmlTest {
    @Test
    public void pageContainsControls() throws Exception {
        String html = new String(Files.readAllBytes(Paths.get("src/main/webapp/bulk-supplier-check.html")), StandardCharsets.UTF_8);
        assertThat(html, containsString("id=\"supplierSelect\""));
        assertThat(html, containsString("id=\"bulkInput\""));
        assertThat(html, containsString("Bulk check"));
        assertThat(html, containsString("Copy available"));
    }
}
