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
        Files.write(csv, ("supplier,productId,catalog_number\n" +
                "ABB Motors and Mechanical Inc. - Baldor,111,CNA\n" +
                "ABB Motors and Mechanical Inc. - Baldor,222,CNB\n" +
                "ABB Motors and Mechanical Inc. - Baldor,333,CNC\n").getBytes(StandardCharsets.UTF_8));

        ApiCallerFrontEnd mockCaller = mock(ApiCallerFrontEnd.class);
        when(mockCaller.call("111", "a\t1"))
            .thenReturn(new ApiResponse(200, "{\"total_qty_available\":0}"));
        when(mockCaller.call("222", "a\t1"))
            .thenReturn(new ApiResponse(200, "{\"total_qty_available\":5}"));
        when(mockCaller.call("333", "a\t1"))
            .thenReturn(new ApiResponse(200, "{\"total_qty_available\":0}"));

        String cookies = "a\t1";

        JsonGenerator gen = new JsonGenerator(mockCaller, csv.toString());
        JsonArray result = gen.generate(cookies);

        assertEquals("222", result.getJsonObject(0).get("id1").toString());
        assertEquals("CNB", result.getJsonObject(0).getString("catalog_number1"));
        assertEquals("0", result.getJsonObject(0).get("id2").toString());
        assertEquals("", result.getJsonObject(0).getString("catalog_number2"));
    }
}
