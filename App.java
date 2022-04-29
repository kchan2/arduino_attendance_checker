import com.fazecast.jSerialComm.SerialPort;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class App extends Thread {

    static Frame frame;

    // SQL config
    // Port 1433
    static String url = "jdbc:sqlserver://LAPTOP-S2KTOM1Q\\SQLEXPRESS;databaseName=RFIDCards;integratedSecurity=true";
    static String QUERY_GET_COLUMNS = "SELECT RFIDNumber, FirstName, LastName, StudentID, Status FROM RFIDCardsReferenceTable";

    HashMap<String, LocalTime[]> classes = new HashMap<>();
    String getClass;
    static LocalTime classStart;
    static LocalTime classEnd;

    public App(Frame frame) {
        initializeClassInfo();
        App.frame = frame;
        getClass = (String) JOptionPane.showInputDialog(
                null, "Choose current class:",
                "Choose Class", JOptionPane.PLAIN_MESSAGE,
                null, classes.keySet().toArray(),
                classes.keySet().toArray()[0]);
        classStart = classes.get(getClass)[0];
        classEnd = classes.get(getClass)[1];
    }

    private void initializeClassInfo() {
        classes.put("CSC 473", new LocalTime[]{LocalTime.of(11, 30), LocalTime.of(12, 25)});
        classes.put("CSC 241", new LocalTime[]{LocalTime.of(13, 50), LocalTime.of(14, 45)});
    }

    public void run() {
        SerialPort comPort = SerialPort.getCommPorts()[0];
        comPort.openPort();
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);
        try {
            while (LocalTime.now().isBefore(classEnd)) {   // May have to change the condition when testing
                byte[] readBuffer = new byte[8];
                comPort.readBytes(readBuffer, readBuffer.length);
                String read = new String(readBuffer);
                LocalTime arrivalTime = LocalTime.now();
                if (read.matches("^[a-z0-9_]+$")) {   // ID
                    try (   // Get a connection
                            Connection connection = DriverManager.getConnection(url);
                            Statement stmt = connection.createStatement();
                            ResultSet rs = stmt.executeQuery(QUERY_GET_COLUMNS);
                    ) {
                        while (rs.next()) {
                            // get column info
                            String tagID = read;
                            String studentID = "";
                            String last = "";
                            String first = "";
                            Student student = null;
                            if (rs.getString("ID").equals(tagID)) {
                                studentID = rs.getString("student ID");
                                last = rs.getString("lastname");
                                first = rs.getString("firstname");
                                student = new Student(read, studentID, last, first);
                                if (arrivalTime.isAfter(classStart)) {
                                    // late
                                    stmt.executeQuery(generateUpdateQuery("Late", read));
                                } else {
                                    // on time
                                    stmt.executeQuery(generateUpdateQuery("Present", read));
                                }
                            }
                            frame.addRow(student, arrivalTime);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        comPort.closePort();
    }

    private String generateUpdateQuery(String status, String id) {
        String QUERY_UPDATE = "Update RFIDCardsReferenceTable Set Status =" + status + " where RFIDNumber =" + id;
        return QUERY_UPDATE;
    }

    public void exportToJson() throws SQLException, IOException {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        JsonArrayBuilder presentBuilder = Json.createArrayBuilder();
        JsonArrayBuilder lateBuilder = Json.createArrayBuilder();
        JsonArrayBuilder absentBuilder = Json.createArrayBuilder();
        Connection connection = DriverManager.getConnection(url);
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(QUERY_GET_COLUMNS);
        while (rs.next()) {
            String studentID = rs.getString("student ID");
            String first = rs.getString("FirstName");
            String last = rs.getString("LastName");
            String status = rs.getString("Status");
            status = status.trim();
            JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
            jsonObjectBuilder.add("LastName", last);
            jsonObjectBuilder.add("FirstName", first);
            jsonObjectBuilder.add("StudentID", studentID);
            JsonObject studentObject = jsonObjectBuilder.build();
            if (status.equals("Absent")) {
                absentBuilder.add(studentObject);
            } else if (status.equals("Present")) {
                presentBuilder.add(studentObject);
            } else {
                lateBuilder.add(studentObject);
            }
        }
        // Setting all values back to Absent
        String revertBackToAbsent = "Update RFIDCardsReferenceTable Set Status ='Absent'";
        stmt.executeUpdate(revertBackToAbsent);
        builder.add("Present", presentBuilder.build());
        builder.add("Late", lateBuilder.build());
        builder.add("Absent", absentBuilder.build());
        JsonObject json = builder.build();
        // send it at the end
        String fn = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String formatted = getClass.replaceAll(" ", "") + "_" + fn.replaceAll(":", ".");
        File f = new File(formatted + ".json");
        FileWriter fw = new FileWriter(f);
        fw.write(json.toString());
        fw.close();
        Email e = new Email();
        e.send(getClass, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE), f);
        f.delete();
    }
}
