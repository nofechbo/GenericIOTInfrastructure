package servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gdbm.AdminDBSingleton;
import gdbm.GDBM;

@WebServlet("/product")
public class RegProductServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final GDBM adminDB = AdminDBSingleton.getInstance();
    private final URL url = new URL("http://localhost:8090/iots");

    public RegProductServlet() throws MalformedURLException {}

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Redirect to the React registration page
        resp.sendRedirect("http://localhost:3000/");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Allow requests from your React frontend
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        // Handle preflight (OPTIONS request)
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        BufferedReader reader = req.getReader();
        Type mapType = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> jsonMap = gson.fromJson(reader, mapType);

        try {
            adminDB.beginTransaction();
            addProductInfo(jsonMap);
            sendRegProduct(jsonMap);
            adminDB.commitTransaction();
            sendSuccessResponse(resp, "Product registered successfully.");

        } catch (SQLException e) {
            handleRollback(e, jsonMap);
            sendErrorResponse(resp, "Database error: " + e.getMessage());
        } catch (IOException e) {
            handleRollback(e, jsonMap);
            sendErrorResponse(resp, "Internal server error: " + e.getMessage());
        }
    }

    private void addProductInfo(Map<String, String> jsonMap) throws SQLException {
        adminDB.insertRecord("Products", getProductTableArgs(jsonMap));
    }

    private List<String> getProductTableArgs(Map<String, String> jsonMap) {
        List<String> queryArgs = new ArrayList<>();

        queryArgs.add("\"" + jsonMap.get("company_id") + "\"");
        queryArgs.add("\"" + jsonMap.get("product_id") + "\"");
        queryArgs.add("\"" + jsonMap.get("product_name") + "\"");
        queryArgs.add("\"" + jsonMap.get("product_description") + "\"");

        return queryArgs;
    }

    private void sendRegProduct(Map<String, String> jsonMap) throws IOException {
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String jsonInput = getJsonOfRegProduct(jsonMap);

        try(OutputStream outStream = connection.getOutputStream()) {
            byte[] input = jsonInput.getBytes("utf-8");
            outStream.write(input, 0, input.length);

            System.out.println("request sent to server");
        }

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            System.out.println("response: " + response.toString());
        }
    }

    private String getJsonOfRegProduct(Map<String, String> jsonMap) {
        String productName = "\"" + jsonMap.get("product_name") + "\"";

        return "{ \"command\": \"RegProduct\", \n" +
                "    \"args\": {\n" +
                "    \"productName\": " + productName + "\n" +
                "    }\n" +
                "}";
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        BufferedReader reader = req.getReader();
        Type mapType = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> jsonMap = gson.fromJson(reader, mapType);

        String productId = jsonMap.get("product_id");

        if (productId == null || productId.isEmpty()) {
            sendErrorResponse(resp, "Product ID is required.");
            return;
        }

        try {
            adminDB.beginTransaction();
            adminDB.deleteRecords("Products", "ProductID", productId);
            adminDB.commitTransaction();
            sendSuccessResponse(resp, "Product removed successfully.");
        } catch (SQLException e) {
            handleRollback(e, jsonMap);
            sendErrorResponse(resp, "Database error: " + e.getMessage());
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void sendSuccessResponse(HttpServletResponse resp, String message) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write("{\"message\": \"" + message + "\"}");
    }
    private void sendErrorResponse(HttpServletResponse resp, String errorMessage) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        resp.getWriter().write("{\"error\": \"" + errorMessage + "\"}");
    }

    private void handleRollback(Exception e, Map<String, String> jsonMap) {
        e.printStackTrace();

        try {
            adminDB.rollbackTransaction();
        } catch (SQLException rollbackException) {
            rollbackException.printStackTrace();

            // Fallback: Manually delete inserted records if rollback fails
            try {
                adminDB.deleteRecords("Products", "ProductID", jsonMap.get("product_id"));
                System.err.println("Rollback failed! Manually deleted product with ID: " + jsonMap.get("product_id"));
            } catch (SQLException deleteException) {
                deleteException.printStackTrace();
                System.err.println("CRITICAL: Rollback and manual deletion both failed! Database may be corrupted.");
            }
        }
    }

}
