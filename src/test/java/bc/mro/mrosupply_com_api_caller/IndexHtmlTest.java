package bc.mro.mrosupply_com_api_caller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class IndexHtmlTest {
    @Test
    public void productsTextareaEmptyAndAutoLoadsJson() throws Exception {
        String html = new String(Files.readAllBytes(Paths.get("src/main/webapp/index.html")), StandardCharsets.UTF_8);
        assertThat(html, containsString("<textarea id=\"productsToCheck\" class=\"form-control\" placeholder=\"Products to check\" rows=\"20\"></textarea><br />"));
        assertThat(html, containsString("loadJsonIntoTextarea('suppliers-to-check-all.json');"));
        assertThat(html, not(containsString("\"supplier\": \"A & P BEARINGS\"")));
        assertThat(html, not(containsString("load-regal-brands")));
    }

    @Test
    public void legendTableContainsAllBadges() throws Exception {
        String html = new String(Files.readAllBytes(Paths.get("src/main/webapp/index.html")), StandardCharsets.UTF_8);
        assertThat(html, containsString("id=\"legend-table\""));
        assertThat(html, containsString("Fail"));
        assertThat(html, containsString("Success"));
        assertThat(html, containsString("Overloaded"));
        assertThat(html, containsString("Selenium down"));
        assertThat(html, containsString("No script found"));
        assertThat(html, containsString("Hidden"));
        assertThat(html, containsString("Not authorized"));
    }
}
