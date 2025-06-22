package bc.mro.mrosupply_com_api_caller;

import java.util.Map;

import org.junit.Test;
import static org.junit.Assert.*;

public class CookieUtilsTest {

    @Test
    public void parseCookiesShouldHandleQuotesAndWhitespace() {
        String text = "name1\t\"value1\"\n" +
                       "name2\tvalue2\n";
        Map<String, String> cookies = CookieUtils.parseCookies(text);
        assertEquals(2, cookies.size());
        assertEquals("value1", cookies.get("name1"));
        assertEquals("value2", cookies.get("name2"));
    }

    @Test
    public void buildCookieHeaderShouldJoinCookies() {
        String text = "a\t1\n" +
                       "b\t2";
        Map<String, String> cookies = CookieUtils.parseCookies(text);
        String header = CookieUtils.buildCookieHeader(cookies);
        // order should be preserved
        assertEquals("a=1; b=2", header);
    }
}
