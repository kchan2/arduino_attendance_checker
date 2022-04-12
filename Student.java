public class Student {

    String tagID;
    String studentID;
    String lastname;
    String firstname;

    public Student(String tagID, String studentID,
                   String lastname, String firstname) {
        this.tagID = tagID;
        this.studentID = studentID;
        this.lastname = lastname;
        this.firstname = firstname;
    }

    @Override
    public String toString() {
        return "Tag ID: " + tagID + ", Student ID: " + studentID +
                ", First: " + firstname + ", Last: " + lastname;
    }

}
