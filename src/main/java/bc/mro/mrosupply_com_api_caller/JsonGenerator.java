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
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonGenerator {

    private static final Logger LOG = Logger.getLogger(JsonGenerator.class.getName());

    private final ApiCallerFrontEnd apiCaller;
    private final String csvFile;
    public JsonGenerator(ApiCallerFrontEnd apiCaller) {
        this(apiCaller, "products-to-find-available.csv");
    }

    public JsonGenerator(ApiCallerFrontEnd apiCaller, String csvFile) {
        this.apiCaller = apiCaller;
        this.csvFile = csvFile;
    }

    private static class SupplierData {
        final List<String> products = new ArrayList<>();
        String catalog1;
        String catalog2;
        String catalog3;
    }

    public JsonObject generate(String cookies) throws IOException {
        Map<String, SupplierData> csvData = readCsv();
        LOG.log(Level.INFO, "Loaded {0} suppliers from CSV", csvData.size());

        List<JsonObject> allSuppliers = new ArrayList<>();
        for (Map.Entry<String, SupplierData> e : csvData.entrySet()) {
            String supplier = e.getKey();
            SupplierData data = e.getValue();
            List<String> selected = selectProducts(data.products, cookies);
            LOG.log(Level.INFO, "Supplier {0} selected products {1}", new Object[]{supplier, selected});

            JsonObjectBuilder b = Json.createObjectBuilder();
            b.add("supplier", supplier);
            for (int i = 0; i < 3; i++) {
                String id = (i < selected.size()) ? selected.get(i) : "0";
                b.add("id" + (i + 1), Integer.parseInt(id));
            }
            b.add("catalog_number1", data.catalog1 == null ? "" : data.catalog1);
            b.add("catalog_number2", data.catalog2 == null ? "" : data.catalog2);
            b.add("catalog_number3", data.catalog3 == null ? "" : data.catalog3);
            allSuppliers.add(b.build());
        }

        JsonArrayBuilder allArr = Json.createArrayBuilder();
        for (JsonObject o : allSuppliers) allArr.add(o);

        return Json.createObjectBuilder()
                .add("all", allArr)
                .add("regal", Json.createArrayBuilder())
                .build();
    }

    // Method left for backwards compatibility if future persistence is needed
    // Currently no file writing is performed.

    private Map<String, SupplierData> readCsv() throws IOException {
        Map<String, SupplierData> map = new LinkedHashMap<>();
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
                SupplierData data = map.computeIfAbsent(supplier, k -> new SupplierData());
                data.products.add(product);
                if (parts.length >= 5 && data.catalog1 == null) {
                    data.catalog1 = parts[2].trim();
                    data.catalog2 = parts[3].trim();
                    data.catalog3 = parts[4].trim();
                }
            }
        }
        return map;
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

}
