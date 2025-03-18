package servlets;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import gdbm.AdminDBSingleton;
import gdbm.GDBM;

@WebServlet("/company-data")
public class FetchCompanyServlet extends HttpServlet {
    private final GDBM adminDB = AdminDBSingleton.getInstance();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        resp.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setContentType("application/json");

        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String companyId = req.getParameter("company_id");
        if (companyId == null || companyId.isEmpty()) {
            sendErrorResponse(resp, "Company ID is required.");
            return;
        }

        try {
            List<String> columns = List.of("CompanyID", "CompanyName", "CompanyAddress", "SubscriptionPlan");
            String whereClause = "CompanyID = '" + companyId + "'";
            List<List<String>> companyData = adminDB.selectRecords("Companies", columns, whereClause);

            if (!companyData.isEmpty()) {
                sendSuccessResponse(resp, companyData);
            } else {
                sendErrorResponse(resp, "Company not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sendErrorResponse(resp, "Database error: " + e.getMessage());
        }
    }

    private void sendSuccessResponse(HttpServletResponse resp, Object data) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(gson.toJson(data));
    }

    private void sendErrorResponse(HttpServletResponse resp, String errorMessage) throws IOException {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        resp.getWriter().write(gson.toJson(Map.of("error", errorMessage)));
    }
}

