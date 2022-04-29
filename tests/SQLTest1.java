import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class SQLTest1 {
    // SQL config
    // I don't know how you set up your SQL stuff,
    // so you can change accordingly
    static final String DB_URL = "jdbc:mysql://localhost/TUTORIALSPOINT";
    static final String USER = "guest";
    static final String PASS = "guest123";
    static final String QUERY = "SELECT tag id, student id, first, last FROM Table";

    // Scan and save the whole table
    static ArrayList<Student> table = new ArrayList<>();

    public static void main(String[] args) {
        // Open a connection
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(QUERY);
        ) {
            while (rs.next()) {
                // Display values
                // I don't know how you do your column labels,
                // so you can change accordingly
                String tagID = rs.getString("tag id");
                String studentID = rs.getString("student id");
                String last = rs.getString("lastname");
                String first = rs.getString("firstname");
                Student student = new Student(tagID, studentID, last, first);
                table.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (Student stu : table) {
            System.out.println(stu);
        }
        Scanner s = new Scanner(System.in);
        System.out.print("Enter a tag ID: ");
        String read = s.next();
        for (Student stu : table) {
            if (stu.tagID.equals(read)) {
                System.out.println("Student found.");
                System.out.println(stu);
            }
        }
    }

}
