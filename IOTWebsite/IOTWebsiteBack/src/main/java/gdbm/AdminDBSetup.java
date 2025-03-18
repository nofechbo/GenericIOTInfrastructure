package gdbm;

import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminDBSetup {

    public static GDBM initAdminDB() throws SQLException {
        String URL = "jdbc:mysql://localhost:3306/";
        String username = "root";
        String password = "303021398";

        GDBM gdbm = new GDBM("AdminDB", URL, username, password);

        /*gdbm.deleteTable("Contacts"); //delete if exists:
        gdbm.deleteTable("Products"); //delete if exists:
        gdbm.deleteTable("Companies"); //delete if exists:*/

        CreateCompaniesTable(gdbm);
        CreateContactsTable(gdbm);
        CreateProductsTable(gdbm);

        return gdbm;
    }

    private static void CreateCompaniesTable(GDBM gdbm) throws SQLException {
        List<Map.Entry<DataTypes, String>> companiesColumns = new ArrayList<>();
        List<String> companiesCharLengths = new ArrayList<>();

        companiesColumns.add(new AbstractMap.SimpleEntry<>(DataTypes.VARCHAR, "CompanyID")); //primary
        companiesCharLengths.add("16");
        companiesColumns.add(new AbstractMap.SimpleEntry<>(DataTypes.VARCHAR, "CompanyName"));
        companiesCharLengths.add("32");
        companiesColumns.add(new AbstractMap.SimpleEntry<>(DataTypes.VARCHAR, "CompanyAddress"));
        companiesCharLengths.add("64");
        companiesColumns.add(new AbstractMap.SimpleEntry<>(DataTypes.VARCHAR, "SubscriptionPlan"));
        companiesCharLengths.add("16");

        gdbm.createTable(
                "Companies",
                companiesColumns,
                companiesCharLengths,
                "CompanyID"
        );
    }

    private static void CreateContactsTable(GDBM gdbm) throws SQLException {
        List<Map.Entry<DataTypes, String>> contactsColumns = new ArrayList<>();
        List<String> contactsCharLengths = new ArrayList<>();

        contactsColumns.add(new AbstractMap.SimpleEntry<>(DataTypes.VARCHAR, "CompanyID")); //foreign
        contactsCharLengths.add("16");
        contactsColumns.add(new AbstractMap.SimpleEntry<>(DataTypes.VARCHAR, "ContactID")); //primary
        contactsCharLengths.add("16");
        contactsColumns.add(new AbstractMap.SimpleEntry<>(DataTypes.VARCHAR, "ContactName"));
        contactsCharLengths.add("32");
        contactsColumns.add(new AbstractMap.SimpleEntry<>(DataTypes.VARCHAR, "ContactEmail"));
        contactsCharLengths.add("64");
        contactsColumns.add(new AbstractMap.SimpleEntry<>(DataTypes.VARCHAR, "ContactPhoneNumber"));
        contactsCharLengths.add("16");
        contactsColumns.add(new AbstractMap.SimpleEntry<>(DataTypes.VARCHAR, "Password"));
        contactsCharLengths.add("64");

        gdbm.createTable(
                "Contacts",
                contactsColumns,
                contactsCharLengths,
                "ContactID",
                "CompanyID",
                "Companies",
                "CompanyID"
        );
    }

    private static void CreateProductsTable(GDBM gdbm) throws SQLException {
        List<Map.Entry<DataTypes, String>> productsColumns = new ArrayList<>();
        List<String> productsCharLengths = new ArrayList<>();

        productsColumns.add(new AbstractMap.SimpleEntry<>(DataTypes.VARCHAR, "CompanyID")); //foreign
        productsCharLengths.add("16");
        productsColumns.add(new AbstractMap.SimpleEntry<>(DataTypes.VARCHAR, "ProductID")); //primary
        productsCharLengths.add("16");
        productsColumns.add(new AbstractMap.SimpleEntry<>(DataTypes.VARCHAR, "ProductName"));
        productsCharLengths.add("32");
        productsColumns.add(new AbstractMap.SimpleEntry<>(DataTypes.VARCHAR, "ProductDescription"));
        productsCharLengths.add("64");

        gdbm.createTable(
                "Products",
                productsColumns,
                productsCharLengths,
                "ProductID",
                "CompanyID",
                "Companies",
                "CompanyID"
        );
    }
}

