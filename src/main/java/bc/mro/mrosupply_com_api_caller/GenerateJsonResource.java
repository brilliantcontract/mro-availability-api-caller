package bc.mro.mrosupply_com_api_caller;

import java.io.IOException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("generate-json")
public class GenerateJsonResource {

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response generate() throws IOException {
        JsonGenerator generator = new JsonGenerator(new ApiCallerFrontEnd());
        generator.generate();
        return Response.ok("JSON files generated").build();
    }
}
