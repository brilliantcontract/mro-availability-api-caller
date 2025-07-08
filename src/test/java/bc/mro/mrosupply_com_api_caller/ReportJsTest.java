package bc.mro.mrosupply_com_api_caller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import org.junit.Test;

public class ReportJsTest {
    @Test
    public void qtyBadgeUsesSecondaryWhenZero() throws Exception {
        String js = new String(Files.readAllBytes(Paths.get("src/main/webapp/js/report.js")), StandardCharsets.UTF_8);
        assertThat(js, containsString("? 'badge-success' : 'badge-secondary'"));
    }
}
