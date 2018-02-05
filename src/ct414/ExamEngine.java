
package ct414;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExamEngine implements ExamServer {
    ArrayList<Student> students = new ArrayList<>();
    ArrayList<Assessment> assessments = new ArrayList<>();

    // Constructor is required
    public ExamEngine() {
        super();
    }

    // Implement the methods defined in the ExamServer interface...
    // Return an access token that allows access to the server for some time period
    public int login(int studentid, String password) throws 
                UnauthorizedAccess, RemoteException {

	// TBD: You need to implement this method!
	// For the moment method just returns an empty or null value to allow it to compile
        for(int i =0; i<students.size();i++){
            if (studentid==students.get(i).getId()&&password.equals(students.get(i).getPassword())){
                return studentid;
            }
            else if (studentid==students.get(i).getId()&&!(password.equals(students.get(i).getPassword()))){
                String reason = "Incorrect Password";
                throw new UnauthorizedAccess(reason);
            }
        }
        return  0;
    }

    // Return a summary list of Assessments currently available for this studentid
    public List<String> getAvailableSummary(int token, int studentid) throws
                UnauthorizedAccess, NoMatchingAssessment, RemoteException {


        // TBD: You need to implement this method!
        // For the moment method just returns an empty or null value to allow it to compile

        return null;
    }

    // Return an Assessment object associated with a particular course code
    public Assessment getAssessment(int token, int studentid, String courseCode) throws
                UnauthorizedAccess, NoMatchingAssessment, RemoteException {

        // TBD: You need to implement this method!
        // For the moment method just returns an empty or null value to allow it to compile

        return null;
    }

    // Submit a completed assessment
    public void submitAssessment(int token, int studentid, Assessment completed) throws 
                UnauthorizedAccess, NoMatchingAssessment, RemoteException {

        // TBD: You need to implement this method!
    }

    public void addStudent(Student s) {
        students.add(s);
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public void addAssessment(Assessment a) {
        assessments.add(a);
    }

    public ArrayList<Assessment> getAssessments() {
        return assessments;
    }

    public static void main(String[] args) {
        ExamEngine e = new ExamEngine();
        Student student1 = new Student(14439308,"howstings","4BP1");
        Student student2 = new Student(14521303,"howkeepin","4BP1");
        Student student3 = new Student(14512345,"student3","4BP1");

        e.addStudent(student1);
        e.addStudent(student2);
        e.addStudent(student3);

        ExamPaper test1 = new ExamPaper("28/02/2018","Java RMI test",14439308);
        ExamPaper test2 = new ExamPaper("02/11/2018","Java RMI test",14521303);
        ExamPaper test3 = new ExamPaper("14/02/2018","Java RMI test",14512345);

        String[] ans1 = {"1","2","3"};
        String[] ans2 = {"76","86","64"};
        String[] ans3 = {"Barack Obama","Donald Trump","Hilary Clinton"};
        String[] ans4 = {"Krakow","Warsaw","Poznan"};

        ExamQuestion q1 = new ExamQuestion(1,"How many sides on a triangle", ans1,2);
        ExamQuestion q2 = new ExamQuestion(2,"123 - 47 = ", ans2,0);
        ExamQuestion q3 = new ExamQuestion(3,"President of the USA is?", ans3,1);
        ExamQuestion q4 = new ExamQuestion(4,"Capital city of Poland is?", ans4,1);

        test1.addQuestion(q1);
        test1.addQuestion(q2);
        test1.addQuestion(q3);

        test2.addQuestion(q2);
        test2.addQuestion(q3);
        test2.addQuestion(q4);

        test3.addQuestion(q1);
        test3.addQuestion(q3);
        test3.addQuestion(q4);

        for (Student s : e.getStudents()) {
            for (Assessment exam : e.getAssessments()) {
                if (s.getId() == exam.getAssociatedID()) {
                    s.addAssessment(exam);
                }
            }
        }




        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "ExamServer";
            ExamServer engine = new ExamEngine();
            ExamServer stub =
                (ExamServer) UnicastRemoteObject.exportObject(engine, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            System.out.println("ExamEngine bound");
        } catch (Exception ex) {
            System.err.println("ExamEngine exception:");
            ex.printStackTrace();
        }
    }
}
