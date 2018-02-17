
/**
 * @author Daire Canavan 14439308
 * @author Aidan Boyd 14521303
 */

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

        // Setting up dummy objects for testing
        Student student1 = new Student(14439308, "howstings", "4BP1");
        Student student2 = new Student(14521303, "howkeepin", "4BP1");
        Student student3 = new Student(14512345, "student3", "4BP1");

        this.addStudent(student1);
        this.addStudent(student2);
        this.addStudent(student3);

        ExamPaper test1 = new ExamPaper("28/02/2018", "Assessment Daire", 14439308);
        ExamPaper test2 = new ExamPaper("17/02/2018", "Assessment Aidan", 14521303);
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
        //For all students in the database
        for (int i = 0; i < students.size(); i++) {
            // If the appropriate student is found
            if (studentid == students.get(i).getId() && password.equals(students.get(i).getPassword())) {

                //Create a session giving the student 15 minutes on the system
                Session s = new Session(studentid, students.get(i));
                sessions.add(s);
                //Set token as the student number
                return studentid;
            } else if (studentid == students.get(i).getId() && !(password.equals(students.get(i).getPassword()))) {

                // If the login details were invalid
                String reason = "Incorrect Password";
                throw new UnauthorizedAccess(reason);
            }
        }
        return 0;
    }

    // Return a summary list of Assessments currently available for this studentid
    public List<String> getAvailableSummary(int token, int studentid) throws
            UnauthorizedAccess, NoMatchingAssessment, RemoteException {

        // Makes sure it is in an active session
        if (checkSessionActive()) {
            for (Student s : this.getStudents()) {
                if (s.getId() == studentid) {
                    // Returns all assessments associated with the currently logged in student
                    ArrayList<Assessment> aList = s.getAssessments();
                    List<String> names = new ArrayList<>();


                    for (Assessment a : aList) {
                        // Adds the assessment name to the list
                        names.add(a.getInformation());
                    }
                    // Returns list of assessment names
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
            // Searches for a specific assessment and if it is found it is returned otherwise null is returned
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
            // Make sure the assessment is still open i.e. it is before the submission deadline
            Date now = new Date();
            Date closingDate = completed.getClosingDate();
            boolean before = now.before(closingDate);

            if (before) {
                // Make sure that assessment is associated with the passed in student
                if (completed.getAssociatedID() == studentid) {
                    // Set assessment to completed
                    System.out.println("Assignment " + completed.getInformation() + " completed.");

                    // Create a map of all graded assignments that the user can query to obtain result
                    graded_assignments.put(completed.getInformation()+"_"+studentid, gradeAssignment(studentid,completed));
                }
            }

        }
    }

    // Grades the submitted assignment
    private String gradeAssignment(int studentid, Assessment assessment) throws
            UnauthorizedAccess, NoMatchingAssessment, RemoteException{

        // If the session is still active
        if (studentid == assessment.getAssociatedID()) {
            int correct = 0; // Number of correct answers
            StringBuilder result = new StringBuilder(); // Results string

            // For all questions in the assessment
            for (Question q : assessment.getQuestions()) {

                // Create a string with the test information
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

            // Calculate grade
            float score = ((float) correct) / ((float) assessment.getQuestions().size())*100;
            result.append("You obtained " + score + "% in the assignment " + assessment.getInformation());
            return result.toString();
        }

        return null;
    }


    // Allows for the client to query the database for a result of an assessment
    // If a result is available it means the submission deadline has passed and the grade can be returned
    @Override
    public String gradeSubmission(String key) throws
            UnauthorizedAccess, NoMatchingAssessment, RemoteException{

        if (checkSessionActive()) {
            // If there is a grade available return it
            if (graded_assignments.containsKey(key)) {
                return graded_assignments.get(key);
            }
        }
        // Otherwise return a message saying it is not available to the client
        return "Not in Map";
    }


    // Adds a new student to database
    public void addStudent(Student s) {
        students.add(s);
    }

    // Get all students in the database
    public ArrayList<Student> getStudents() {
        return students;
    }

    // Add a new assessment
    public void addAssessment(Assessment a) {
        assessments.add(a);
    }

    // Get all assessments
    public ArrayList<Assessment> getAssessments() {
        return assessments;
    }

    // Checks that the current session is still valid and the 15 minute period has not elapsed
    private boolean checkSessionActive() throws UnauthorizedAccess{
        for(int s = 0; s<sessions.size(); s++){
            if(sessions.get(s).isActiveSession()) {
                return true;
            } else {
                System.err.println("Your session is no longer active, please login again.");
                System.exit(0);
            }
        }
        throw new UnauthorizedAccess("You are not logged in. Log in to continue.");
    }

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        // Set up server backend and bind it
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
