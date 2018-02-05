package ct414;

import java.util.ArrayList;

public class Student {
    private int id;
    private String password;
    private String courseCode;
    private ArrayList<Assessment> assessments;
    public Student(int id,String password,String courseCode){
        this.id = id;
        this.password = password;
        this.courseCode = courseCode;

    }

    public String getCourseCode() {
        return courseCode;
    }

    public int getId() {
        return id;
    }
    public String getPassword() {
        return password;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void addAssessment(Assessment a){
    this.assessments.add(a);
    }

    public ArrayList<Assessment> getAssessments() {
        return assessments;
    }
}
