//Authors :
//        Daire Canavan 14439308
//        Aidan Boyd    14521303

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
//            if (System.getSecurityManager() == null) {
//                System.setProperty("java.security.policy", "java.policy");
//                System.setSecurityManager(new SecurityManager());
//            }
            //Setting up registry and server
            String name = "ExamServer";
            Registry registry = LocateRegistry.getRegistry("localhost");
            ExamServer exam = (ExamServer) registry.lookup(name);
            Scanner in = new Scanner(System.in); //input from user command line
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
            token = exam.login(userid, password); //calling login method from exam server,

            //Runs until user confirms session is completed (or exits due to error)
            while(!completed) {
                //Displays the assingment list
                displayAssignments(in, exam, token, userid);
                System.out.println("\nWould you like to return to the assignment list? (y/n)"); //after submission / viewing grades
                String reSubmission = in.nextLine();
                //Checks if user is finished session
                if(reSubmission.equals("n")) {
                    completed = true; // breaks loop
                }
            }
            System.out.println("\nUser has been logged out");
            System.exit(0); //end session - close client program
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static void displayAssignments(Scanner in, ExamServer exam, int token, int username) throws
            UnauthorizedAccess, NoMatchingAssessment, RemoteException {
        //Display available assignments for user
        System.out.println("\nAssignments available for User ID: " + username);
        List<String> l = exam.getAvailableSummary(token, username); //prints available assignments tied to the user
        if(l == null) System.out.println("No assessments available for student: " + username
                + "\nPlease come back later");
        else { //printing out assignment info
            HashMap<Integer,String> assignments = new HashMap<>(); //for holding assignments and corresponding number key for selection
            System.out.println("#\tAssignment Name\t\tDeadline\tSubmitted\tAvailable for grading"); //top line in assignment summary

            boolean after = false; //checks if deadline has passed
            boolean complete = false; //set true if assignment completed
            String graded = "";
            for (int x = 0; x < l.size(); x++) { //for each assignment available to user

                String name = l.get(x);
                assignments.put((x+1), name);
                Assessment a = exam.getAssessment(token,username,name); //gets assessment object
                ExamPaper ex = (ExamPaper) a; //cast to exampaper object to access methods
                Date now = new Date(); //current date
                Date close = ex.getClosingDate(); //returns deadline for assignment
                after = now.after(close); //sets after variable
                String closeDate = "";
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                //returns grade info, if completed the assignment is graded but grade is only available after closing date
                graded = exam.gradeSubmission(a.getInformation() + "_" + username); //takes assignment title+userid
                if (!graded.equals("Not in Map")) { //i.e. assignment has been completed, i.e. in 'graded assignments' map in server
                    complete = true; //it is in graded assignments map therefore it is completed
                }

                try {
                    closeDate = df.format(close);//change close date to string
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //prints out information collected within this method about the assignent
                System.out.println((x+1) + "\t" + name + "\t" + closeDate + "\t" + complete + "\t\t" + after);
            }
            //Selecting and starting an assignment
            boolean inList = false;
            Assessment a = null;
            while (!inList) {
                System.out.println("\nPlease enter the number of the assessment you wish to access:");
                String input = in.nextLine(); //select assignment number
                int number = Integer.parseInt(input);
                if (assignments.containsKey(number)) {
                    String courseCode = assignments.get(number); //return assignment string
                    a = exam.getAssessment(token, username, courseCode); //return assessment object tied to this user with given title(courseCode)
                    System.out.println("\n" + a.getInformation()); //print out assignment title
                    inList = true; //break loop
                }
            }

            // Decide which options are available to the user for selected assignment
            if (after && complete) { //i.e. the assignmet is past deadline and has been submitted - therefore grade is available
                // Grade the assignment
                System.out.println(graded); //print graded information collected earlier from grade submission method
            } else if (!after && !complete) { //before deadline and not submitted
                // Start assignment
                try {

                    System.out.println("\nWould you like to begin the assignment? (y/n)");
                    String begin = in.nextLine();
                    if(begin.equals("y")) {
                        completeAssignment(a,in); //answer questions on assignment
                    } else {
                        return; //return to main
                    }

                    finishAssignment(in, a, token, username, exam); //submits assignment to server
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (!after && complete) { //can resubmit if before deadline

                // Redo the assignment
                try {

                    System.out.println("\nWould you like to resubmit the assignment? (y/n)");
                    String begin = in.nextLine();
                    if(begin.equals("y")) {
                        completeAssignment(a,in);
                    } else {
                        return;
                    }

                    finishAssignment(in, a, token, username, exam); //submit
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
                exam.submitAssessment(token,username_int,a); //calls submit assessment from server.
            }
            else { //user doesn't want to submit completed assignment
                System.out.println("\nWould you like to resubmit your assignment? (y/n)");
                String redo = in.nextLine();
                if(redo.equals("y")) {
                    completeAssignment(a,in);
                }
                else {
                    System.exit(0); //session ends if user neither wants to redo or complete assignment
                }
            }
        }
    }

    private static void completeAssignment(Assessment a, Scanner in) throws
            InvalidQuestionNumber, InvalidOptionNumber {
        //Displays question with options and allows user to select an answer
        for(int i=0; i<a.getQuestions().size(); i++){ //loop for each question
            System.out.println();
            System.out.println("\nQuestion Number "+ (i+1) + ":");
            System.out.println(a.getQuestion(i+1).getQuestionDetail()); //prints question
            String[] answers = a.getQuestion(i+1).getAnswerOptions();
            for (int j=0; j<answers.length;j++) { //loop prints each possible answer
                System.out.println("Option "+(j+1)+": "+answers[j]);
            }

            System.out.println("\nPlease select your answer for question: "+ (i+1) +"\n" +
                    "e.g. If you want to select Option 1 of a question you would enter 1");
            String answer = in.nextLine();
            //takes answer number from command line, call assessment method to select answer
            a.selectAnswer((i+1),(Integer.parseInt(answer)-1)); //1st argument i+1 (when i=0, question no. =1)
                                                                //2nd (selected answer -1 due to answers being
                                                                // stored in array index starting @ 0
        }
    }
}