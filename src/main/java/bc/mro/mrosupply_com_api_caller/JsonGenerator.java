package bc.mro.mrosupply_com_api_caller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

public class JsonGenerator {

    private final ApiCallerFrontEnd apiCaller;
    private final String csvFile;
    private final String allJson;
    private final String regalJson;

    public JsonGenerator(ApiCallerFrontEnd apiCaller) {
        this(apiCaller, "products-to-find-available.csv",
                "src/main/webapp/suppliers-to-check-all.json",
                "src/main/webapp/suppliers-to-check-regal.json");
    }

    public JsonGenerator(ApiCallerFrontEnd apiCaller, String csvFile,
            String allJson, String regalJson) {
        this.apiCaller = apiCaller;
        this.csvFile = csvFile;
        this.allJson = allJson;
        this.regalJson = regalJson;
    }

    public JsonObject generate(String cookies) throws IOException {
        Map<String, List<String>> csvData = readCsv();
        List<JsonObject> allSuppliers = loadExisting(Paths.get(allJson));
        List<JsonObject> regalSuppliers = loadExisting(Paths.get(regalJson));

        List<JsonObject> updatedAll = process(allSuppliers, csvData, cookies);
        List<JsonObject> updatedRegal = process(regalSuppliers, csvData, cookies);

        JsonArrayBuilder allArr = Json.createArrayBuilder();
        for (JsonObject o : updatedAll) allArr.add(o);
        JsonArrayBuilder regalArr = Json.createArrayBuilder();
        for (JsonObject o : updatedRegal) regalArr.add(o);

        return Json.createObjectBuilder()
                .add("all", allArr)
                .add("regal", regalArr)
                .build();
    }

    public void generateAndSave(String cookies) throws IOException {
        JsonObject result = generate(cookies);
        backupAndWrite(Paths.get(allJson), result.getJsonArray("all").getValuesAs(JsonObject.class));
        backupAndWrite(Paths.get(regalJson), result.getJsonArray("regal").getValuesAs(JsonObject.class));
    }

    private Map<String, List<String>> readCsv() throws IOException {
        Map<String, List<String>> map = new LinkedHashMap<>();
        InputStream is = getClass().getClassLoader().getResourceAsStream(csvFile);
        if (is == null) {
            Path p = Paths.get(csvFile);
            if (Files.exists(p)) {
                is = Files.newInputStream(p);
            }
        }
        if (is == null) return map;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String header = br.readLine();
            if (header == null) return map;
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 2) continue;
                String supplier = parts[0].trim();
                String product = parts[1].trim();
                map.computeIfAbsent(supplier, k -> new ArrayList<>()).add(product);
            }
        }
        return map;
    }

    private List<JsonObject> loadExisting(Path file) throws IOException {
        if (!Files.exists(file)) {
            return new ArrayList<>();
        }
        try (InputStream is = Files.newInputStream(file)) {
            return Json.createReader(is).readArray().getValuesAs(JsonObject.class);
        }
    }

    private List<JsonObject> process(List<JsonObject> existing, Map<String, List<String>> csvData, String cookies) {
        List<JsonObject> out = new ArrayList<>();
        for (JsonObject obj : existing) {
            String supplier = obj.getString("supplier");
            List<String> products = csvData.get(supplier);
            List<String> selected;
            if (products != null) {
                selected = selectProducts(products, cookies);
            } else {
                selected = new ArrayList<>();
            }
            while (selected.size() < 3 && products != null && products.size() >= 3) {
                int idx = products.size() - (3 - selected.size());
                if (idx < 0) idx = 0;
                selected.add(products.get(idx));
            }
            JsonObjectBuilder b = Json.createObjectBuilder();
            b.add("supplier", supplier);
            for (int i = 0; i < selected.size() && i < 3; i++) {
                b.add("id" + (i + 1), Integer.parseInt(selected.get(i)));
            }
            if (selected.size() < 3) {
                for (int i = selected.size(); i < 3; i++) {
                    b.add("id" + (i + 1), obj.getInt("id" + (i + 1), 0));
                }
            }
            b.add("catalog_number1", "OR834");
            b.add("catalog_number2", "BF9484");
            b.add("catalog_number3", "NGP895");
            out.add(b.build());
        }
        return out;
    }

    private List<String> selectProducts(List<String> products, String cookies) {
        List<String> result = new ArrayList<>();
        for (String p : products) {
            if (result.size() == 3) break;
            ApiResponse response = apiCaller.call(p, cookies);
            try {
                JsonObject obj = Json.createReader(new java.io.StringReader(response.getBody())).readObject();
                int qty = obj.getInt("total_qty_available", 0);
                if (qty > 0) {
                    result.add(p);
                }
            } catch (Exception e) {
                // ignore malformed
            }
        }
        return result;
    }

    private void backupAndWrite(Path file, List<JsonObject> data) throws IOException {
        Path backup = file.resolveSibling("backup-" + file.getFileName().toString());
        Files.move(file, backup, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        JsonArrayBuilder arr = Json.createArrayBuilder();
        for (JsonObject o : data) arr.add(o);
        try (JsonWriter w = Json.createWriter(Files.newOutputStream(file))) {
            w.writeArray(arr.build());
        }
    }
}
