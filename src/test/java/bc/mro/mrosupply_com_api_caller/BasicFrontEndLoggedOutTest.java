package bc.mro.mrosupply_com_api_caller;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

@Ignore
public class BasicFrontEndLoggedOutTest {

    private String cookies = "";

    @Test
    public void test() {
        ApiCallerFrontEndLoggedOut apiCaller = new ApiCallerFrontEndLoggedOut();
        ApiResponse apiResponse = apiCaller.call("6500502", cookies);

        System.out.println("Status: " + apiResponse.getStatus());
        System.out.println("Body: " + apiResponse.getBody());
    }

}
