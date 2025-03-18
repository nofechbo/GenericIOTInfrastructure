package servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gdbm.AdminDBSingleton;
import gdbm.GDBM;

@WebServlet("/contact")
public class RegContactServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final GDBM adminDB = AdminDBSingleton.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Redirect to the React registration page
        resp.sendRedirect("http://localhost:3000/");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        BufferedReader reader = req.getReader();
        Type mapType = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> jsonMap = gson.fromJson(reader, mapType);

        try {
            adminDB.beginTransaction();
            adminDB.insertRecord("Contacts", getContactTableArgs(jsonMap));
            adminDB.commitTransaction();
            sendSuccessResponse(resp, "Contact registered successfully.");

        } catch (SQLException e) {
            handleRollback(e, jsonMap);
            sendErrorResponse(resp, "Database error: " + e.getMessage());
        }
    }

    private List<String> getContactTableArgs(Map<String, String> jsonMap) {
        List<String> queryArgs = new ArrayList<>();
        queryArgs.add("\"" + jsonMap.get("company_id") + "\"");
        queryArgs.add("\"" + jsonMap.get("contact_id") + "\"");
        queryArgs.add("\"" + jsonMap.get("contact_name") + "\"");
        queryArgs.add("\"" + jsonMap.get("contact_email") + "\"");
        queryArgs.add("\"" + jsonMap.get("contact_phone_number") + "\"");
        queryArgs.add("\"" + hashPassword(jsonMap.get("password")) + "\"");
        return queryArgs;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
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

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}


