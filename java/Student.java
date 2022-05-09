package com.company;

public class Student {

    String tagID;
    String studentID;
    String lastname;
    String firstname;

    public Student(String tagID, String studentID,
                   String lastname, String firstname) {
        this.tagID = tagID.trim();
        this.studentID = studentID.trim();
        this.lastname = lastname.trim();
        this.firstname = firstname.trim();
    }

    @Override
    public String toString() {
        return "Tag ID: " + tagID + ", Student ID: " + studentID +
                ", First: " + firstname + ", Last: " + lastname;
    }

}
