package bc.mro.mrosupply_com_api_caller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class GenerateJsonHtmlTest {
    @Test
    public void pageContainsButtonAndNavLink() throws Exception {
        String html = new String(Files.readAllBytes(Paths.get("src/main/webapp/generate-json.html")), StandardCharsets.UTF_8);
        assertThat(html, containsString("Generate new .json files"));
        assertThat(html, containsString("href=\"generate-json.html\""));
        assertThat(html, containsString("<pre><code id=\"result\"></code></pre>"));
    }
}
