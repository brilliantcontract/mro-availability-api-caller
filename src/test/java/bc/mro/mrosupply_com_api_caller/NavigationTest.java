package bc.mro.mrosupply_com_api_caller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

public class NavigationTest {
    @Test
    public void allPagesHaveGenerateLink() throws Exception {
        String[] pages = {"src/main/webapp/index.html", "src/main/webapp/help.html", "src/main/webapp/test.html", "src/main/webapp/generate-json.html"};
        for (String page : pages) {
            String html = new String(Files.readAllBytes(Paths.get(page)), StandardCharsets.UTF_8);
            assertThat(html, containsString("href=\"generate-json.html\""));
        }
    }

    @Test
    public void navigationLinksDoNotUseBlankTarget() throws Exception {
        String[] pages = {"src/main/webapp/index.html", "src/main/webapp/help.html", "src/main/webapp/test.html", "src/main/webapp/generate-json.html"};
        for (String page : pages) {
            String html = new String(Files.readAllBytes(Paths.get(page)), StandardCharsets.UTF_8);
            int navStart = html.indexOf("<nav");
            int navEnd = html.indexOf("</nav>", navStart);
            String navHtml = html.substring(navStart, navEnd);
            assertThat(navHtml, not(containsString("target=\"_blank\"")));
        }
    }
}
