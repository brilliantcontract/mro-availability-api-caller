package bc.mro.mrosupply_com_api_caller;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Cookie;

import java.util.Map;

import bc.mro.mrosupply_com_api_caller.CookieUtils;

public class ApiCallerBackEnd {

    public ApiCallerBackEnd() {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
    }

    public ApiResponse call(final String productId, String rawCookies) {
        // 1. Build a reusable Jersey client
        Client client = ClientBuilder
                .newBuilder()
                .build();

        Map<String, String> cookies = CookieUtils.parseCookies(rawCookies);

        // 2. (Optional) supply cookies when the endpoint requires an
        //    authenticated session. Replace the dummy values.
        Cookie csrftoken = new Cookie("csrftoken", cookies.getOrDefault("csrftoken", ""));

        // 3. Prepare the request with all headers you care about
        Invocation.Builder req = client
                .target(generateApiUrl(productId))
                .request(MediaType.WILDCARD_TYPE) // Accept: */*
                .header("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:136.0) Gecko/20100101 Firefox/136.0")
                .header("Accept-Language", "en-US,en;q=0.5")
                .header("X-CSRFToken", csrftoken.getValue())
                .header("X-Requested-With", "XMLHttpRequest")
                .header("Sec-Fetch-Dest", "empty")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Referer", "https://www.mrosupply.com/admin/products/product/6500502/change/");

        // Attach cookies;
        // -- DRF’s header name is case-insensitive, but use the canonical spelling:
        req.header("X-CSRFToken", cookies.get("csrftoken"));
        cookies.remove("csrftoken");

        // -- Build ONE Cookie header instead of many (cleaner, and works with any server):
        String cookieHeader = CookieUtils.buildCookieHeader(cookies);
        req.header("Cookie", cookieHeader);

        // 4. Fire the GET and handle the response
        try (Response r = req.get()) {
            return new ApiResponse(r.getStatus(), r.readEntity(String.class));
        }
    }

    private static final String BASE_URL = "https://www.mrosupply.com/admin/products/proxyproduct/price_preview/";

    private String generateApiUrl(String productId) {
        String url = BASE_URL + productId;
        return url;
    }

}
