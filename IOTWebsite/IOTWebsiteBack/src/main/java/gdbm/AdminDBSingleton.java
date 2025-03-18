package gdbm;

import java.sql.SQLException;

public class AdminDBSingleton {
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("In static block");
            throw new RuntimeException(e);
        }
    }

    public static GDBM getInstance(){
        return dbmsInstance.getAdminGdbm();
    }

    private static class dbmsInstance {
        private static GDBM adminGdbm;

        static {
            try {
                adminGdbm = AdminDBSetup.initAdminDB();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        private static GDBM getAdminGdbm(){
            return adminGdbm;
        }
    }
}
