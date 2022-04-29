package com.company;

import java.sql.*;



public class Main {
    // Port 1433
    static String absent = "Absent";
    static String present = "1";
    static String QUERY = "SELECT RFIDNumber, FirstName, LastName, StudentID, Status FROM RFIDCardsReferenceTable";


    public static void main(String[] args) {
        String url = "jdbc:sqlserver://LAPTOP-S2KTOM1Q\\SQLEXPRESS;databaseName=RFIDCards;integratedSecurity=true";

        try{
            // Get a connection
            Connection connection = DriverManager.getConnection(url);
            System.out.println("Connected");
            Statement stmt = connection.createStatement();

            // Update if an RFID Card is scanned
            // you scan the card 205E6870
            String test = "'205E6870'";
            String sql = "Update RFIDCardsReferenceTable Set Status ='Present' where RFIDNumber =" + test;
            stmt.executeUpdate(sql);

            // Grabbing info for the json
            ResultSet rs = stmt.executeQuery(QUERY);
            while (rs.next()){
                String first = rs.getString("FirstName");
                String last = rs.getString("LastName");
                String status = rs.getString("Status");
                status = status.trim();
                if (status.equals(absent)){
                    System.out.println("Absent: " + first + last);
                } else if (status.equals(present)){
                    System.out.println("Present: " + first + last);
                } else {
                    System.out.println("Late: " + first + last);
                }
            }

            // Setting all values back to Absent
            String revertBackToAbsent = "Update RFIDCardsReferenceTable Set Status ='Absent'";
            stmt.executeUpdate(revertBackToAbsent);

        }
        catch (Exception exc){
            exc.printStackTrace();
        }
    }
}
