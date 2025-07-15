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
        assertThat(js, containsString("? 'badge-info' : 'badge-secondary'"));
        assertThat(js, containsString("result.data && typeof result.data.total_qty_available !== 'undefined'"));
    }

    @Test
    public void aopFailureHandledAsFail() throws Exception {
        String js = new String(Files.readAllBytes(Paths.get("src/main/webapp/js/report.js")), StandardCharsets.UTF_8);
        assertThat(js, containsString("data.result.success === false"));
        assertThat(js, containsString("return {status: \"Fail\""));
    }

    @Test
    public void catalogNumbersUsedForAop() throws Exception {
        String js = new String(Files.readAllBytes(Paths.get("src/main/webapp/js/report.js")), StandardCharsets.UTF_8);
        assertThat(js, containsString("catalog_number1"));
        assertThat(js, containsString("checkAopProduct(supplierSlug, catalog_number1)"));
    }

    @Test
    public void availabilityHandledWhenCalculatingQty() throws Exception {
        String js = new String(Files.readAllBytes(Paths.get("src/main/webapp/js/report.js")), StandardCharsets.UTF_8);
        assertThat(js, containsString("data.result.availability"));
        assertThat(js, containsString("data.total_qty_available = total"));
    }

    @Test
    public void overloadedStatusUsesWarningBadge() throws Exception {
        String js = new String(Files.readAllBytes(Paths.get("src/main/webapp/js/report.js")), StandardCharsets.UTF_8);
        assertThat(js, containsString("Overloaded"));
        assertThat(js, containsString("badge-warning"));
    }

    @Test
    public void notActiveStatusHandled() throws Exception {
        String js = new String(Files.readAllBytes(Paths.get("src/main/webapp/js/report.js")), StandardCharsets.UTF_8);
        assertThat(js, containsString("data.error && data.error.includes('not active')"));
        assertThat(js, containsString("return {status: \"Not active\""));
    }
}
