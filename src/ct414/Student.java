package ct414;

/**
 * Class to hold student information such as username(ID), password and associated assessments
 *
 * @author Daire Canavan 14439308
 * @author Aidan Boyd 14521303
 */

import java.util.ArrayList;

public class Student {
    private int id;
    private String password;
    private String courseCode;
    private ArrayList<Assessment> assessments = new ArrayList<>();

    public Student(int id,String password,String courseCode){
        this.id = id;
        this.password = password;
        this.courseCode = courseCode;

    }

    public int getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public void addAssessment(Assessment a){
    this.assessments.add(a);
    }

    public ArrayList<Assessment> getAssessments() {
        return assessments;
    }
}
