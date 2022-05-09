package com.company;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class GUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new ThreadForGUI());
    }

    private static class ThreadForGUI implements Runnable {

        public void run() {
            GUI gui = new GUI();
        }
    }

    public GUI() {
        Frame frame = new Frame("Arduino Attendance Checker");
    }

}

class Frame extends JFrame implements ActionListener {

    JLabel classInfo;
    JTable display;
    DefaultTableModel model;
    JButton export;
    App app;
    Thread t;

    public Frame(String title) {
        super(title);
        app = new App(this);
        t = new Thread(app);
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addComponents(getContentPane());
        addListeners();
        setVisible(true);
        t.start();
    }

    private void addComponents(Container contentPane) {
        // northern region
        String info = "Class: " + app.getClass + "   Instructor: Jae Woong Lee";
        classInfo = new JLabel(info, SwingConstants.CENTER);
        classInfo.setFont(new Font(classInfo.getFont().getFontName(), Font.PLAIN, 15));
        classInfo.setPreferredSize(new Dimension(700, 30));
        // central region
        String[] column = {"Name", "Arrival Time"};
        model = new DefaultTableModel(column, 0);
        display = new JTable(model);
        display.setRowHeight(20);
        JScrollPane sp = new JScrollPane(display);
        // southern region
        export = new JButton("Export Attendance Data");
        export.setPreferredSize(new Dimension(700, 30));
        // establish the regions
        contentPane.setLayout(new BorderLayout());
        contentPane.add(classInfo, BorderLayout.NORTH);
        contentPane.add(sp, BorderLayout.CENTER);
        contentPane.add(export, BorderLayout.SOUTH);
    }

    public void addRow(Student student, LocalTime t) {
        String s = student.firstname + " " + student.lastname;
        if (student.lastname.equals("")) {
            s = student.tagID;
        }
        String time = t.truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_TIME);
        String[] row = {s, time};
        model.addRow(row);
    }

    private void addListeners() {
        export.addActionListener(this);
        Frame f = this;
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                int yn = JOptionPane.showConfirmDialog(f,
                        "Are you sure you want to close this window? \n" +
                                "Please make sure you have exported the attendance data before you exit. ",
                        "Close Window?", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (yn == JOptionPane.YES_OPTION) {
                    t.interrupt();
                    System.exit(0);
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        // Only one action - export
        String command = event.getActionCommand();
        if (command.equalsIgnoreCase("Export Attendance Data")) {
            try {
                app.exportToJson();
                JOptionPane.showMessageDialog(this,
                        "Data has been exported and sent to you email.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}