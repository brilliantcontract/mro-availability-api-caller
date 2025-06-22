package bc.mro.mrosupply_com_api_caller;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

@Ignore
public class BasicBackEndTest {

    private String cookies = "";

    public BasicBackEndTest() {
        cookies = "__kla_id	\"eyJjaWQiOiJOak0xTnpJelpXUXRZVFExTUMwME9UZGtMVGxsTTJRdE1XTmhZekkwTWpFNVlqVXgiLCIkcmVmZXJyZXIiOnsidHMiOjE3MjkwOTg3MDcsInZhbHVlIjoiIiwiZmlyc3RfcGFnZSI6Imh0dHBzOi8vd3d3Lm1yb3N1cHBseS5jb20vZWxlY3RyaWMtbW90b3JzLzk2NjUwODYzXzE5MTg3MjAwX21hcmF0aG9uLWVsZWN0cmljLyJ9LCIkbGFzdF9yZWZlcnJlciI6eyJ0cyI6MTc0ODgwOTAyMCwidmFsdWUiOiIiLCJmaXJzdF9wYWdlIjoiaHR0cHM6Ly93d3cubXJvc3VwcGx5LmNvbS9hdXRvbWF0aW9uLzk2NjQ1ODQwXzYwMzMwMDA3MDBfbHNpcy8ifX0=\"\n"
                + "_clck	\"f0qseu|2|fwe|0|1750\"\n"
                + "_clsk	\"1pvzcqs|1748809020422|1|1|e.clarity.ms/collect\"\n"
                + "_fbp	\"fb.1.1742581425151.327709340755871575\"\n"
                + "_ga	\"GA1.1.958634030.1729098705\"\n"
                + "_ga_M8TWSNS3RE	\"GS2.1.s1748887393$o2$g1$t1748887416$j37$l0$h0\"\n"
                + "_ga_MNNV0SMJQH	\"GS2.1.s1748887392$o41$g1$t1748887416$j36$l0$h0\"\n"
                + "_gat	\"1\"\n"
                + "_gcl_au	\"1.1.1597442235.1746636850\"\n"
                + "_gid	\"GA1.2.1723299191.1748809016\"\n"
                + "_isuid	\"be6e14e2-e284-41f6-b2e6-a056076ff9b2\"\n"
                + "_nb_sp_id.6daf	\"f09e47a8-269b-41c0-bacb-02e29be5e65e.1726748621.130.1748887417.1748809018.de961274-a23a-4f96-9b95-058ba849a92b\"\n"
                + "_nb_sp_ses.6daf	\"*\"\n"
                + "_rdt_uuid	\"1729098706359.de8e96cc-b828-4a7c-aa9c-f289c9abe71a\"\n"
                + "_uetsid	\"5f4892e03f2511f0a8d725ff355227ae\"\n"
                + "_uetvid	\"b9c56b708be111efba5fdb0d6cf9302a\"\n"
                + "comm100_visitorguid_60000959	\"5e082c54-37d4-4edb-9f51-4950ead89ee9\"\n"
                + "csrftoken	\"fQRcTv88moZkTZQswXPhBiNaTSD5S7A8\"\n"
                + "datadome	\"0lK86OrxN~n~F9FWTHm9QsOy8TrJjJWIh_9D_NGr1CHzQy05RjbHWDwkf8gv6tNyzJKvD6syKG~~fJ5cFoYJmvCUJru361udskPdj9j_OjaqU14lMocK6dF23Y6_57xL\"\n"
                + "recent_products	'\"[2224187\\\\054 1151390\\\\054 1276976\\\\054 96736639\\\\054 96652836\\\\054 2842391\\\\054 96645840\\\\054 106111]:1uM9VW:FrTiyUxXKmZHmoaZKH6-f73vGIs6HAK5W6_ySMWJx_c\"'\n"
                + "sessionid	\"hpcsul5inxjjn33k3vqps8tlwrvm4bf3\"\n"
                + "ssSessionIdNamespace	\"022fe9ee-e6ce-46cd-a37a-7be29fa03b63\"\n"
                + "ssShopperId	\"dima@mrosupply.com\"\n"
                + "ssUserId	\"be6e14e2-e284-41f6-b2e6-a056076ff9b2\"\n"
                + "ssViewedProducts	\"106111,96678182,96679482,96678799,96677705,96687796,5879515,266424,96651961,5878926,5878925,6496940,272706,96650500,96649725,96648840,96650863,96650815,96650842,96650723\"";
    }

    @Test
    public void test() {
        ApiCallerBackEnd apiCaller = new ApiCallerBackEnd();
        ApiResponse apiResponse = apiCaller.call("6500502", cookies);

        System.out.println("Status: " + apiResponse.getStatus());
        System.out.println("Body: " + apiResponse.getBody());
    }

}
