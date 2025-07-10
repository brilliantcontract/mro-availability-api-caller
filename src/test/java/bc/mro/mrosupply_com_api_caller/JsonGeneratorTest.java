package bc.mro.mrosupply_com_api_caller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.json.Json;
import javax.json.JsonArray;

import org.junit.Test;

public class JsonGeneratorTest {
    @Test
    public void generatorUsesFirstAvailableProducts() throws Exception {
        Path dir = Files.createTempDirectory("gen");
        Path csv = dir.resolve("products.csv");
        Files.write(csv, ("supplier,productId\n" +
                "ABB Motors and Mechanical Inc. - Baldor,111\n" +
                "ABB Motors and Mechanical Inc. - Baldor,222\n" +
                "ABB Motors and Mechanical Inc. - Baldor,333\n").getBytes(StandardCharsets.UTF_8));
        Path cookies = dir.resolve("cookies.txt");
        Files.write(cookies, "a\t1".getBytes(StandardCharsets.UTF_8));
        Path all = dir.resolve("all.json");
        Files.copy(Paths.get("src/main/webapp/suppliers-to-check-all.json"), all);
        Path regal = dir.resolve("regal.json");
        Files.copy(Paths.get("src/main/webapp/suppliers-to-check-regal.json"), regal);

        ApiCallerFrontEnd mockCaller = mock(ApiCallerFrontEnd.class);
        when(mockCaller.call("111", "a\t1"))
            .thenReturn(new ApiResponse(200, "{\"total_qty_available\":0}"));
        when(mockCaller.call("222", "a\t1"))
            .thenReturn(new ApiResponse(200, "{\"total_qty_available\":5}"));
        when(mockCaller.call("333", "a\t1"))
            .thenReturn(new ApiResponse(200, "{\"total_qty_available\":0}"));

        JsonGenerator gen = new JsonGenerator(mockCaller, cookies.toString(), csv.toString(), all.toString(), regal.toString());
        gen.generate();

        String content = new String(Files.readAllBytes(all), StandardCharsets.UTF_8);
        assertThat(content, containsString("222"));
        JsonArray arr = Json.createReader(new java.io.StringReader(content)).readArray();
        assertEquals("222", arr.getJsonObject(0).get("id1").toString());
        assertTrue(Files.exists(dir.resolve("backup-" + all.getFileName().toString())));
    }
}
