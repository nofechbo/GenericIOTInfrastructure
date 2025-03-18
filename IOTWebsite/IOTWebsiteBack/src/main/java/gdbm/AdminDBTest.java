package gdbm;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminDBTest {
    public static void main(String[] args) {
        try {
            GDBM gdbm = AdminDBSetup.initAdminDB();
            System.out.println("DB setup completed");
            insertCompanies(gdbm);
            insertContacts(gdbm);
            insertProducts(gdbm);
            System.out.println("Data inserted successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private static void insertCompanies(GDBM gdbm) throws SQLException {
        for (int i = 1; i <= 6; i++) {
            List<String> companyData = new ArrayList<>();
            companyData.add("'C00" + i + "'"); // CompanyID
            companyData.add("'Company " + i + "'"); // CompanyName
            companyData.add("'the " + (i * 10 + 3) + " street'"); //CompanyAddress
            companyData.add("'Plan" + (i % 3 + 1) + "'"); // SubscriptionPlan
            gdbm.insertRecord("Companies", companyData);
        }
        System.out.println("6 Companies inserted");
    }

    private static void insertContacts(GDBM gdbm) throws SQLException {
        for (int i = 1; i <= 9; i++) {
            List<String> agentData = new ArrayList<>();
            agentData.add("'C00" + ((i <= 3) ? 1 : (i % 6 + 1)) + "'"); // CompanyID (first 3 agents belong to Company 1)
            agentData.add("'A00" + i + "'"); //ContactID
            agentData.add("'Agent " + i + "'"); // ContactName
            agentData.add("'agent" + i + "@infinity.com'"); // ContactEmail
            agentData.add("'123-456-78" + i + "'"); // ContactPhoneNumber
            gdbm.insertRecord("Contacts", agentData);
        }
        System.out.println("9 Agents inserted");
    }

    private static void insertProducts(GDBM gdbm) throws SQLException {
        for (int i = 1; i <= 10; i++) {
            List<String> productData = new ArrayList<>();
            productData.add("'C00" + (i % 6 + 1) + "'"); // CompanyID (rotate between companies)
            productData.add("'P00" + i + "'"); // ProductID
            productData.add("'Product " + i + "'"); // ProductName
            productData.add("'amazing number " + i + " product!'");// ProductDescription
            gdbm.insertRecord("Products", productData);
        }
        System.out.println("10 Products inserted");
    }
}

