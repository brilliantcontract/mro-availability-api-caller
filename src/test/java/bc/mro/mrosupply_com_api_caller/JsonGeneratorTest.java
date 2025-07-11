package bc.mro.mrosupply_com_api_caller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.json.JsonArray;

import org.junit.Test;

public class JsonGeneratorTest {
    @Test
    public void generatorUsesFirstAvailableProducts() throws Exception {
        Path dir = Files.createTempDirectory("gen");
        Path csv = dir.resolve("products.csv");
        Files.write(csv, ("supplier,productId,catalog_number1,catalog_number2,catalog_number3\n" +
                "ABB Motors and Mechanical Inc. - Baldor,111,CNA,CNB,CNC\n" +
                "ABB Motors and Mechanical Inc. - Baldor,222,CNA,CNB,CNC\n" +
                "ABB Motors and Mechanical Inc. - Baldor,333,CNA,CNB,CNC\n").getBytes(StandardCharsets.UTF_8));

        ApiCallerFrontEnd mockCaller = mock(ApiCallerFrontEnd.class);
        when(mockCaller.call("111", "a\t1"))
            .thenReturn(new ApiResponse(200, "{\"total_qty_available\":0}"));
        when(mockCaller.call("222", "a\t1"))
            .thenReturn(new ApiResponse(200, "{\"total_qty_available\":5}"));
        when(mockCaller.call("333", "a\t1"))
            .thenReturn(new ApiResponse(200, "{\"total_qty_available\":0}"));

        String cookies = "a\t1";

        JsonGenerator gen = new JsonGenerator(mockCaller, csv.toString());
        javax.json.JsonObject result = gen.generate(cookies);

        JsonArray arr = result.getJsonArray("all");
        assertEquals("222", arr.getJsonObject(0).get("id1").toString());
        assertEquals("CNA", arr.getJsonObject(0).getString("catalog_number1"));
        assertEquals("CNB", arr.getJsonObject(0).getString("catalog_number2"));
        assertEquals("CNC", arr.getJsonObject(0).getString("catalog_number3"));
    }
}
