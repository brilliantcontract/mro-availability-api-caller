package bc.mro.mrosupply_com_api_caller;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

@Ignore
public class BasicFrontEndTest {

    private String cookies = "";

    public BasicFrontEndTest() {
        cookies = "__kla_id	\"eyJjaWQiOiJOak0xTnpJelpXUXRZVFExTUMwME9UZGtMVGxsTTJRdE1XTmhZekkwTWpFNVlqVXgiLCIkcmVmZXJyZXIiOnsidHMiOjE3MjkwOTg3MDcsInZhbHVlIjoiIiwiZmlyc3RfcGFnZSI6Imh0dHBzOi8vd3d3Lm1yb3N1cHBseS5jb20vZWxlY3RyaWMtbW90b3JzLzk2NjUwODYzXzE5MTg3MjAwX21hcmF0aG9uLWVsZWN0cmljLyJ9LCIkbGFzdF9yZWZlcnJlciI6eyJ0cyI6MTc0NjYzNzAxNCwidmFsdWUiOiJodHRwczovL3d3dy5tcm9zdXBwbHkuY29tL2FicmFzaXZlcy8iLCJmaXJzdF9wYWdlIjoiaHR0cHM6Ly93d3cubXJvc3VwcGx5LmNvbS9hYnJhc2l2ZXMvYnVycnMtZGVidXJyaW5nLXRvb2xzLyJ9fQ==\"\n"
                + "_clck	\"f0qseu|2|fvp|0|1750\"\n"
                + "_fbp	\"fb.1.1742581425151.327709340755871575\"\n"
                + "_ga	\"GA1.1.958634030.1729098705\"\n"
                + "_ga_MNNV0SMJQH	\"GS2.1.s1746636849$o36$g1$t1746637013$j53$l0$h0\"\n"
                + "_gcl_au	\"1.1.1597442235.1746636850\"\n"
                + "_isuid	\"be6e14e2-e284-41f6-b2e6-a056076ff9b2\"\n"
                + "_nb_sp_id.6daf	\"f09e47a8-269b-41c0-bacb-02e29be5e65e.1726748621.122.1747551604.1747511994.380d99b1-8402-4ba7-a750-2ec6329b6ec5\"\n"
                + "_nb_sp_ses.6daf	\"*\"\n"
                + "_rdt_uuid	\"1729098706359.de8e96cc-b828-4a7c-aa9c-f289c9abe71a\"\n"
                + "_uetvid	\"b9c56b708be111efba5fdb0d6cf9302a\"\n"
                + "comm100_visitorguid_60000959	\"5e082c54-37d4-4edb-9f51-4950ead89ee9\"\n"
                + "csrftoken	\"fQRcTv88moZkTZQswXPhBiNaTSD5S7A8\"\n"
                + "datadome	\"gE_~roaneAOHAWUe8OG7GlFG0N_JqncqKELTRmNDhw_j_phFoPGF55eYufNJRq7C_MR1NmJweheuGeTJ_ppekWCfBZt2~dX6nVO_7sKM3p4koNCU_70_0PZVYEIt5h8v\"\n"
                + "klaviyo-yeti	\"1745350697557\"\n"
                + "recent_products	'\"[422057\\\\054 95823686\\\\054 2380163\\\\054 2257881\\\\054 2139361\\\\054 2124985\\\\054 1312205\\\\054 6500502]:1uGXvU:1-LRVYdNM_bP9qm8-NXMj7fxuHzHBC6COGGQx1Stxwc\"'\n"
                + "sessionid	\"hpcsul5inxjjn33k3vqps8tlwrvm4bf3\"\n"
                + "ssSessionIdNamespace	\"022fe9ee-e6ce-46cd-a37a-7be29fa03b63\"\n"
                + "ssShopperId	\"dima@mrosupply.com\"\n"
                + "ssUserId	\"be6e14e2-e284-41f6-b2e6-a056076ff9b2\"\n"
                + "ssViewedProducts	\"6500502,96678182,96679482,96678799,96677705,96687796,5879515,266424,96651961,5878926,5878925,6496940,272706,96650500,96649725,96648840,96650863,96650815,96650842,96650723\"";
    }

    @Test
    public void test() {
        ApiCallerFrontEnd apiCaller = new ApiCallerFrontEnd();
        ApiResponse apiResponse = apiCaller.call("6500502", cookies);

        System.out.println("Status: " + apiResponse.getStatus());
        System.out.println("Body: " + apiResponse.getBody());
    }

}
