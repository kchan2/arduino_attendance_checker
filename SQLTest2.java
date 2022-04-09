/*
This program
 */

import java.sql.*;
import java.util.Scanner;

public class SQLTest2 {

    // SQL config
    // I don't know how you set up your SQL stuff,
    // so you can change accordingly
    static final String DB_URL = "jdbc:mysql://localhost/TUTORIALSPOINT";
    static final String USER = "guest";
    static final String PASS = "guest123";
    static final String QUERY = "SELECT tag id, student id, first, last FROM Table";

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.print("Enter a tag ID: ");
        String read = s.next();
        // Open a connection
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(QUERY);
        ) {
            while (rs.next()) {
                // Display values
                // I don't know how you do your column labels,
                // so you can change accordingly
                System.out.print("Tag ID: " + rs.getString("tag id"));
                System.out.print(", Student ID: " + rs.getInt("student id"));
                System.out.print(", First: " + rs.getString("first"));
                System.out.println(", Last: " + rs.getString("last"));
                if (rs.getString("tag id").equals(read)) {
                    String studentID = rs.getString("student id");
                    String last = rs.getString("last");
                    String first = rs.getString("first");
                    Student student = new Student(read,studentID,last,first);
                    System.out.println("Student found.");
                    System.out.println(student);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
