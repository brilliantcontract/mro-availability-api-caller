package bc.mro.mrosupply_com_api_caller;

import java.io.IOException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("generate-json")
public class GenerateJsonResource {

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response generate(@FormParam("cookies") String cookies) throws IOException {
        JsonGenerator generator = new JsonGenerator(new ApiCallerFrontEnd());
        generator.generate(cookies == null ? "" : cookies);
        return Response.ok("JSON files generated").build();
    }
}
