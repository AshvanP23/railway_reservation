package dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBUtil {
    public static Connection getConnection() {
    	Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
           conn= DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/Railway", "RailDB", "infinitelocs");
           System.out.println("Connection Established");
           return conn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void main(String args[])
    {
    	 getConnection();
    }
}
