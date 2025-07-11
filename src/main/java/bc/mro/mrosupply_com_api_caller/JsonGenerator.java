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

    private static class ProductRec {
        final String productId;
        final String catalogNumber;

        ProductRec(String productId, String catalogNumber) {
            this.productId = productId;
            this.catalogNumber = catalogNumber;
        }
    }

    private static class SupplierData {
        final List<ProductRec> products = new ArrayList<>();
    }

    public javax.json.JsonArray generate(String cookies) throws IOException {
        Map<String, SupplierData> csvData = readCsv();
        LOG.log(Level.INFO, "Loaded {0} suppliers from CSV", csvData.size());

        JsonArrayBuilder allArr = Json.createArrayBuilder();
        for (Map.Entry<String, SupplierData> e : csvData.entrySet()) {
            String supplier = e.getKey();
            SupplierData data = e.getValue();
            LOG.log(Level.INFO, "Processing supplier {0} with {1} products", new Object[]{supplier, data.products.size()});
            List<ProductRec> selected = selectProducts(data.products, cookies);
            LOG.log(Level.INFO, "Supplier {0} selected products {1}", new Object[]{supplier, selected});

            JsonObjectBuilder b = Json.createObjectBuilder();
            b.add("supplier", supplier);
            for (int i = 0; i < 3; i++) {
                String id = (i < selected.size()) ? selected.get(i).productId : "0";
                String catalog = (i < selected.size()) ? selected.get(i).catalogNumber : "";
                b.add("id" + (i + 1), Integer.parseInt(id));
                b.add("catalog_number" + (i + 1), catalog);
            }
            allArr.add(b.build());
        }

        return allArr.build();
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
                if (parts.length < 3) continue;
                String supplier = parts[0].trim();
                String product = parts[1].trim();
                String catalog = parts[2].trim();
                SupplierData data = map.computeIfAbsent(supplier, k -> new SupplierData());
                data.products.add(new ProductRec(product, catalog));
            }
        }
        return map;
    }

    private List<ProductRec> selectProducts(List<ProductRec> products, String cookies) {
        List<ProductRec> result = new ArrayList<>();
        for (ProductRec rec : products) {
            if (result.size() == 3) break;
            LOG.log(Level.INFO, "Checking product {0}", rec.productId);
            ApiResponse response = apiCaller.call(rec.productId, cookies);
            String body = response.getBody();
            if (body == null || body.trim().isEmpty()) {
                LOG.log(Level.WARNING, "Empty response for product {0}", rec.productId);
                continue;
            }
            if (!body.trim().startsWith("{") && !body.trim().startsWith("[")) {
                String preview = body.trim();
                if (preview.length() > 100) {
                    preview = preview.substring(0, 100) + "...";
                }
                LOG.log(Level.WARNING, "Non JSON response for product {0}: {1}", new Object[]{rec.productId, preview});
                continue;
            }

            try {
                JsonObject obj = Json.createReader(new java.io.StringReader(body)).readObject();
                int qty = obj.getInt("total_qty_available", 0);
                LOG.log(Level.INFO, "Product {0} qty {1}", new Object[]{rec.productId, qty});
                if (qty > 0) {
                    result.add(rec);
                }
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Failed to parse response for product " + rec.productId, e);
            }
        }
        return result;
    }

}
