
package ct414;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ExamEngine implements ExamServer {
    private ArrayList<Student> students = new ArrayList<>();
    private ArrayList<Assessment> assessments = new ArrayList<>();
    private ArrayList<Session> sessions = new ArrayList<>();
    private HashMap<String,String> graded_assignments = new HashMap<>();

    // Constructor is required
    public ExamEngine() {
        super();


        Student student1 = new Student(14439308, "howstings", "4BP1");
        Student student2 = new Student(14521303, "howkeepin", "4BP1");
        Student student3 = new Student(14512345, "student3", "4BP1");

        this.addStudent(student1);
        this.addStudent(student2);
        this.addStudent(student3);

        ExamPaper test1 = new ExamPaper("28/02/2018", "Assessment Daire", 14439308);
        ExamPaper test2 = new ExamPaper("02/09/2018", "Assessment Aidan", 14521303);
        ExamPaper test3 = new ExamPaper("14/02/2018", "Assessment Bot", 14512345);

        String[] ans1 = {"1", "2", "3"};
        String[] ans2 = {"76", "86", "64"};
        String[] ans3 = {"Barack Obama", "Donald Trump", "Hilary Clinton"};
        String[] ans4 = {"Krakow", "Warsaw", "Poznan"};

        ExamQuestion q1_1 = new ExamQuestion(1, "How many sides on a triangle", ans1, 2);
        ExamQuestion q2_1 = new ExamQuestion(2, "123 - 47 = ", ans2, 0);
        ExamQuestion q3_1 = new ExamQuestion(3, "President of the USA is?", ans3, 1);
        ExamQuestion q1_2 = new ExamQuestion(1, "Capital city of Poland is?", ans4, 1);
        ExamQuestion q2_2 = new ExamQuestion(2, "123 - 47 = ", ans2, 0);
        ExamQuestion q3_2 = new ExamQuestion(3, "President of the USA is?", ans3, 1);
        ExamQuestion q1_3 = new ExamQuestion(1, "How many sides on a triangle", ans1, 2);
        ExamQuestion q2_3 = new ExamQuestion(2, "President of the USA is?", ans3, 1);
        ExamQuestion q3_3 = new ExamQuestion(3, "Capital city of Poland is?", ans4, 1);


        test1.addQuestion(q1_1);
        test1.addQuestion(q2_1);
        test1.addQuestion(q3_1);

        test2.addQuestion(q1_2);
        test2.addQuestion(q2_2);
        test2.addQuestion(q3_2);

        test3.addQuestion(q1_3);
        test3.addQuestion(q2_3);
        test3.addQuestion(q3_3);

        this.addAssessment(test1);
        this.addAssessment(test2);
        this.addAssessment(test3);

        for (Student s : this.getStudents()) {
            for (Assessment exam : this.getAssessments()) {
                if (s.getId() == exam.getAssociatedID()) {
                    s.addAssessment(exam);
                }
            }
        }
        System.out.println();
    }

    // Implement the methods defined in the ExamServer interface...
    // Return an access token that allows access to the server for some time period
    public int login(int studentid, String password) throws UnauthorizedAccess, RemoteException {
        for (int i = 0; i < students.size(); i++) {
            if (studentid == students.get(i).getId() && password.equals(students.get(i).getPassword())) {
                Session s = new Session(studentid, students.get(i));
                sessions.add(s);
                return studentid;
            } else if (studentid == students.get(i).getId() && !(password.equals(students.get(i).getPassword()))) {
                String reason = "Incorrect Password";
                throw new UnauthorizedAccess(reason);
            }
        }
        return 0;
    }

    // Return a summary list of Assessments currently available for this studentid
    public List<String> getAvailableSummary(int token, int studentid) throws
            UnauthorizedAccess, NoMatchingAssessment, RemoteException {


        if (checkSessionActive()) {
            for (Student s : this.getStudents()) {
                if (s.getId() == studentid) {
                    ArrayList<Assessment> aList = s.getAssessments();
                    List<String> names = new ArrayList<>();

                    for (Assessment a : aList) {
                        names.add(a.getInformation());
                    }
                    return names;
                }
            }

        }

        return null;
    }

    // Return an Assessment object associated with a particular course code
    public Assessment getAssessment(int token, int studentid, String courseCode) throws
            UnauthorizedAccess, NoMatchingAssessment, RemoteException {

        if(checkSessionActive()) {
            for (Assessment assessment : this.getAssessments()) {
                if (assessment.getInformation().equals(courseCode) && assessment.getAssociatedID() == studentid) {
                    return assessment;
                }
            }
        }

        return null;
    }

    // Submit a completed assessment
    public void submitAssessment(int token, int studentid, Assessment completed) throws
            UnauthorizedAccess, NoMatchingAssessment, RemoteException {
        // If the session is still active
        if (checkSessionActive()) {
            // Make sure the assessment is still open
            Date now = new Date();
            Date closingDate = completed.getClosingDate();
            boolean before = now.before(closingDate);

            if (before) {
                // Make sure that assessment is associated with the passed in student
                if (completed.getAssociatedID() == studentid) {
                    // Set assessment to completed
                    System.out.println("Assessment " + completed.getInformation() + " completed.");
                    ((ExamPaper) completed).setCompleted(true);

                    // Create a map of all graded assignments that the user can query to obtain result
                    graded_assignments.put(completed.getInformation()+"_"+studentid,gradeAssignment(studentid,completed));
                }
            }

        }
    }

    private String gradeAssignment(int studentid, Assessment assessment) throws
            UnauthorizedAccess, NoMatchingAssessment, RemoteException{

        // If the session is still active
        if (studentid == assessment.getAssociatedID()) {
            int correct = 0;
            StringBuilder result = new StringBuilder();
            for (Question q : assessment.getQuestions()) {
                ExamQuestion question = (ExamQuestion) q;
                result.append("\nQuestion number: " + q.getQuestionNumber() + "\n");
                result.append(question.getQuestionDetail() + "\n");
                result.append("You answered: " + question.getSelectedAnswer() + "\n");
                result.append("Correct answer: " + question.getCorrectAnswer() + "\n");

                if (question.getSelectedAnswer().equals(question.getCorrectAnswer())) {
                    result.append("You were correct!\n");
                    correct++;
                } else {
                    result.append("You were incorrect!\n");
                }
            }
            float score = ((float) correct) / ((float) assessment.getQuestions().size())*100;
            result.append("You obtained " + score + "% in the assignment " + assessment.getInformation());
            return result.toString();
        }

        return null;
    }

    @Override
    public String gradeSubmission(String key) throws
            UnauthorizedAccess, NoMatchingAssessment, RemoteException{

        if (graded_assignments.containsKey(key)) {
            return graded_assignments.get(key);
        }
        return "Assignment not graded yet. Submit assignment first.";
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

    private boolean checkSessionActive() throws UnauthorizedAccess{
        for(int s = 0; s<sessions.size(); s++){
            if(sessions.get(s).isActiveSession()) {
                return true;
            } else {
                System.err.println("Your session is no longer active, please login");
            }
        }
        throw new UnauthorizedAccess("You are not logged in. Log in to continue.");
    }

    public static void main(String[] args) {
//        if (System.getSecurityManager() == null) {
//            System.setSecurityManager(new SecurityManager());
//        }
        try {
            String name = "ExamServer";
            ExamServer engine = new ExamEngine();
            ExamServer stub = (ExamServer) UnicastRemoteObject.exportObject(engine, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            System.out.println("ExamEngine bound");
        } catch (Exception ex) {
            System.err.println("ExamEngine exception:");
            ex.printStackTrace();
        }
    }
}
