package ct414;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Client {

    public static void main(String args[]) {
        try {
            //Set up security manager
            if (System.getSecurityManager() == null) {
                System.setProperty("java.security.policy", "java.policy");
                System.setSecurityManager(new SecurityManager());
            }
            //Setting up registry and server
            String name = "ExamServer";
            Registry registry = LocateRegistry.getRegistry("localhost");
            ExamServer exam = (ExamServer) registry.lookup(name);
            //Getting input from user
            Scanner in = new Scanner(System.in);
            //Checking for completion of session and successful logins
            boolean completed = false;
            int user = 0;
            String username;
            int token;

            //Logging in
            System.out.println("Enter Username");
            username = in.nextLine();
            System.out.println("Enter Password");
            String password = in.nextLine();
            int userid=Integer.parseInt(username);
            token = exam.login(userid, password);

            //Runs until user confirms session is completed (or exits due to error)
            while(!completed) {
                //Displays the assingment list
                displayAssignments(in, exam, token, userid);

                //Grades an assignment
               // gradeAssessment(exam, token, username, a);
                System.out.println("\nWould you like to return to the assignment list? (y/n)");
                String reSubmission = in.nextLine();
                //Checks if user is finished session
                if(reSubmission.equals("n")) {
                    completed = true;
                }
            }
            System.out.println("\nUser has been logged out");
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static void displayAssignments(Scanner in, ExamServer exam, int token, int username) throws
            UnauthorizedAccess, NoMatchingAssessment, RemoteException {
        //Display available assignments for user
        System.out.println("\nAssignments available for User ID: " + username);
        List<String> l = exam.getAvailableSummary(token, username);
        if(l == null) System.out.println("No assessments available for student: " + username
                + "\nPlease come back later");
        else {
            HashMap<Integer,String> assignments = new HashMap<>();
            System.out.println("#\tAssignment Name\t\tDeadline\tSubmitted\tAvailable for grading");

            boolean after = false;
            boolean complete = false;
            String graded = "";
            for (int x = 0; x < l.size(); x++) {

                String name = l.get(x);
                assignments.put((x+1), name);
                Assessment a = exam.getAssessment(token,username,name);
                ExamPaper ex = (ExamPaper) a;
                Date now = new Date();
                Date close = ex.getClosingDate();
                after = now.after(close);
                String closeDate = "";
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

                graded = exam.gradeSubmission(a.getInformation() + "_" + username);
                if (!graded.equals("Not in Map")) {
                    complete = true;
                }

                try {
                    closeDate = df.format(close);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println((x+1) + "\t" + name + "\t" + closeDate + "\t" + complete + "\t\t" + after);
            }
            //Selecting and starting an assignment
            boolean inList = false;
            Assessment a = null;
            while (!inList) {
                System.out.println("\nPlease enter the number of the assessment you wish to access:");
                String input = in.nextLine();
                int number = Integer.parseInt(input);
                if (assignments.containsKey(number)) {
                    String courseCode = assignments.get(number);
                    a = exam.getAssessment(token, username, courseCode);
                    System.out.println("\n" + a.getInformation());
                    inList = true;
                }
            }

            // Decide which options are available to the user
            if (after && complete) {
                // Grade the assignment
                System.out.println(graded);
            } else if (!after && !complete) {
                // Start assignment
                try {

                    System.out.println("\nWould you like to begin the assignment? (y/n)");
                    String begin = in.nextLine();
                    if(begin.equals("y")) {
                        completeAssignment(a,in);
                    } else {
                        return;
                    }

                    finishAssignment(in, a, token, username, exam);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (!after && complete) {
                // Redo the assignment
                try {

                    System.out.println("\nWould you like to resubmit the assignment? (y/n)");
                    String begin = in.nextLine();
                    if(begin.equals("y")) {
                        completeAssignment(a,in);
                    } else {
                        return;
                    }

                    finishAssignment(in, a, token, username, exam);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (after && !complete) {
                // Tell user they didnt submit the assignment
                System.out.println("You did not submit this assignment so no grade is available.");
            }
        }
    }

    private static void finishAssignment(Scanner in, Assessment a, int token, int username_int, ExamServer exam) throws
            UnauthorizedAccess, NoMatchingAssessment, RemoteException, InvalidOptionNumber, InvalidQuestionNumber {
        boolean submitted = false;
        //Loops continuously allowing user to submit multiple assessments during session.
        while(!submitted) {
            System.out.println("\nWould you like to submit your assignment? (y/n)");
            String submit = in.nextLine();
            if(submit.equals("y")) {
                submitted = true;
                exam.submitAssessment(token,username_int,a);
            }
            else {
                System.out.println("\nWould you like to resubmit your assignment? (y/n)");
                String redo = in.nextLine();
                if(redo.equals("y")) {
                    completeAssignment(a,in);
                }
                //Code exits if user doesnt want to submit or re-do their assingment
                else {
                    System.exit(0);
                }
            }
        }
    }

    private static void completeAssignment(Assessment a, Scanner in) throws
            InvalidQuestionNumber, InvalidOptionNumber {
        //Displays question with options and allows user to select an answer
        for(int i=0; i<a.getQuestions().size(); i++){
            System.out.println();
            System.out.println("\nQuestion Number "+ (i+1) + ":");
            System.out.println(a.getQuestion(i+1).getQuestionDetail());
            String[] answers = a.getQuestion(i+1).getAnswerOptions();
            for (int j=0; j<answers.length;j++) {
                System.out.println("Option "+(j+1)+": "+answers[j]);
            }

            System.out.println("\nPlease select your answer for question: "+ (i+1) +"\n" +
                    "e.g. If you want to select Option 1 of a question you would enter 1");
            String answer = in.nextLine();
            a.selectAnswer((i+1),(Integer.parseInt(answer)-1));
        }
    }
}