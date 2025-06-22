package bc.mro.mrosupply_com_api_caller;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("product-availability-and-price-on-front-end-logged-out")
public class ProductAvailabilityAndPriceOnFrontEndLoggedOutResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of ProductAvailabilityAndPriceResource
     */
    public ProductAvailabilityAndPriceOnFrontEndLoggedOutResource() {
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  POST /api/product-availability-and-price
    // ─────────────────────────────────────────────────────────────────────────
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)   // <- key=value body
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchAvailability(@FormParam("cookies") String rawCookies,
            @FormParam("productId") String productId) {

        ApiCallerFrontEndLoggedOut apiCallerFrontEndLoggedOut = new ApiCallerFrontEndLoggedOut();
        ApiResponse apiResponse = apiCallerFrontEndLoggedOut.call(productId, rawCookies);

        return Response.status(apiResponse.getStatus())
                .entity(apiResponse.getBody())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getJson() {
        return "How to use:\n\n"
                + "curl -X POST \\\n"
                + "     -H \"Content-Type: text/plain\" \\\n"
                + "     --data-binary @cookies.txt \\\n"
                + "     https://mroscrape.top:4002/mro-availability-api-caller/resources/product-availability-and-price-on-front-end-logged-out\n\n\n"
                + "curl -X POST \\\n"
                + "    -H \"Content-Type: application/x-www-form-urlencoded\" \\\n"
                + "    --data-binary @cookies.txt \\\n"
                + "    https://mroscrape.top:4002/mro-availability-api-caller/resources/product-availability-and-price-on-front-end-logged-out";
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }
}
