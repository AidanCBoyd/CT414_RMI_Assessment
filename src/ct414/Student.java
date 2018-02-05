package ct414;

import java.util.ArrayList;

public class Student {
    private int id;
    private String password;
    private ArrayList<Assessment> assessments;
    public Student(int id,String password){
        this.id = id;
        this.password = password;

    }

    public int getId() {
        return id;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void addAssessment(Assessment a){

    }
}
