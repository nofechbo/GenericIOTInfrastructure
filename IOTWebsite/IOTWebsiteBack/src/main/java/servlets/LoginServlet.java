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
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import gdbm.GDBM;
import gdbm.AdminDBSingleton;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final GDBM adminDB = AdminDBSingleton.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCorsHeaders(resp);

        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        BufferedReader reader = req.getReader();
        Type mapType = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> jsonMap = gson.fromJson(reader, mapType);

        String companyId = jsonMap.get("company_id");
        String contactName = jsonMap.get("contact_name");
        String password = jsonMap.get("password");

        try {
            List<String> columns = List.of("Password", "ContactName");
            String whereClause = "CompanyID='" + companyId + "' AND ContactName='" + contactName + "'";

            List<List<String>> result = adminDB.selectRecords("Contacts", columns, whereClause);

            if (result.isEmpty()) {
                sendErrorResponse(resp, "Incorrect Company ID.");
                return;
            }

            String storedPasswordHash = result.get(0).get(0);
            String storedContactName = result.get(0).get(1);

            if (!storedContactName.equals(contactName)) {
                sendErrorResponse(resp, "Incorrect Contact Name.");
                return;
            }

            if (!storedPasswordHash.equals(hashPassword(password))) {
                sendErrorResponse(resp, "Incorrect Password.");
                return;
            }

            sendSuccessResponse(resp, "Login successful");

        } catch (SQLException e) {
            sendErrorResponse(resp, "Database error: " + e.getMessage());
        }
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

    private void sendErrorResponse(HttpServletResponse resp, String errorMessage) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        resp.getWriter().write("{\"error\": \"" + errorMessage + "\"}");
    }

    private void sendSuccessResponse(HttpServletResponse resp, String message) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write("{\"message\": \"" + message + "\"}");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCorsHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void setCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
    }
}
