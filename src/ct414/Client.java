package ct414;

import java.awt.print.Book;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

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
            //Getting input from user
            Scanner in = new Scanner(System.in);
            //Checking for completion of session and successful logins
            boolean completed = false;
            boolean loginSuccesful = false;
            int user = 0;
            String username = "";
            int token = 0;

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
                //Begins an assignment
                Assessment a = startAssignment(in, exam, token, username);
                user = a.getAssociatedID();
                //Grades an assignment
               // gradeAssessment(exam, token, username, a);
                System.out.println("\nWould you like to make another submission or grade an assignment? (y/n)");
                String reSubmission = in.nextLine();
                //Checks if user is finished session
                if(reSubmission.equals("n")) {
                    completed = true;
                }
            }
            System.out.println("\nUser '"+user+"' has been logged out"
                    +"\nThanks for using the exam system");
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static void displayAssignments(Scanner in, ExamServer exam, int token, int username) throws
            UnauthorizedAccess, NoMatchingAssessment, RemoteException {
        //Display available assignments for user
        System.out.println("\nWould you like a summary of available assignments which you can submit or grade? (y/n)");
        String showAssignments = in.nextLine();
        if(showAssignments.equals("y")) {
            List<String> l = exam.getAvailableSummary(token, username);
            if(l == null) System.out.println("No assessments available for student: " + username
                    + "\nPlease come back later");
            else {
                for (String name : l) {
                    System.out.println(name + ", ");
                }
            }
            System.out.println("\nWould you like to grade an assignment? (y/n)");
            String grade = in.nextLine();
            if (grade.equals("y")){
                System.out.println("\nPlease enter the name of the assessment you wish to grade:");
                String courseCode = in.nextLine();
                Assessment a = exam.getAssessment(token, username, courseCode);
                System.out.println("\n"+a.getInformation());
                Date now = new Date();
                Date closingDate = a.getClosingDate();
                if (now.after(closingDate)) {
                    System.out.println(exam.gradeSubmission(a.getInformation() + "_" + username));
                } else {
                    System.out.println("Deadline has not passed yet. Please wait for the deadline to pass before checking grade.");
                }
            }
        }
        else {
            // log user out
            System.out.println("\nThanks for using the exam system. \nYou have been logged out.");
            System.exit(0);
        }
    }

    private static Assessment startAssignment(Scanner in, ExamServer exam, int token, String username) throws
            UnauthorizedAccess, NoMatchingAssessment, RemoteException, InvalidOptionNumber, InvalidQuestionNumber {

        int username_int = Integer.parseInt(username);
        //Selecting and starting an assignment
        System.out.println("\nPlease enter the name of the assessment you wish to complete:");
        String courseCode = in.nextLine();
        Assessment a = exam.getAssessment(token, username_int, courseCode);
        System.out.println("\n"+a.getInformation());

        System.out.println("\nWould you like to begin? (y/n)");
        String begin = in.nextLine();
        if(begin.equals("y")) {
            completeAssessment(a,in);
        }

        boolean submitted = false;
        //Loops continuously allowing user to submit multiple assessments during session.
        while(!submitted) {
            System.out.println("\nWould you like to submit your assessment? (y/n)");
            String submit = in.nextLine();
            if(submit.equals("y")) {
                submitted = true;
                exam.submitAssessment(token,username_int,a);
            }
            else {
                System.out.println("\nWould you like to redo your assessment? (y/n)");
                String redo = in.nextLine();
                if(redo.equals("y")) {
                    completeAssessment(a,in);
                }
                //Code exits if user doesnt want to submit or re-do their assingment
                else {
                    System.exit(0);
                }
            }
        }
        return a;
    }

    private static void completeAssessment(Assessment a, Scanner in) throws
            InvalidQuestionNumber, InvalidOptionNumber {
        //Displays question with options and allows user to select an answer
        for(int i=0; i<a.getQuestions().size(); i++){
            System.out.println("____________________________________________");
            System.out.println("\nQuestion Number "+ (i+1) + ":");
            System.out.println(a.getQuestion(i+1).getQuestionDetail());
            String[] answers = a.getQuestion(i+1).getAnswerOptions();
            for (int j=0; j<answers.length;j++) {
                System.out.println("Option "+(j+1)+": "+answers[j]);
            }

            System.out.println("\nPlease select correct option for question "+ (i+1) +"\n" +
                    "For Example: If you want to select Option one of a question you would enter 1");
            String answer = in.nextLine();
            System.out.println((i+1) + "/" + (Integer.parseInt(answer)-1));
            a.selectAnswer((i+1),(Integer.parseInt(answer)-1));
        }
    }

    //Grades the assessment
//    private static void gradeAssessment(ExamServer exam, int token, String username, Assessment a) throws
//            UnauthorizedAccess, NoMatchingAssessment, RemoteException, InvalidQuestionNumber {
//        //Checking if deadline for submission has expired
//       // String confirmation = exam.submitAssessment(token,Integer.parseInt(username), a);
////        if(confirmation.equals("Deadline Expired")) {
////            System.out.println("Deadline Expired\nYou will now be signed out");
////            System.exit(0);
////        }
//        //Counts the number of correct answers
//        double counter=0.0;
//        boolean[] results = exam.queryResults(token, username);
//        //Tells the users whether they got questions correct and shows them the answer (option number)
//        System.out.println("____________________________________________");
//        for(int i=0; i<results.length; i++) {
//            if(results[i]) {
//                System.out.println("\nYou answered "+a.getQuestion(i+1).getQuestionNumber()+" Correctly"+
//                        "\nThe correct answer is Option "+a.getAnswers()[i]);
//                counter++;
//            }
//            else {
//                System.out.println("\nYou answered "+a.getQuestion(i+1).getQuestionNumber()+" Incorrectly"+
//                        "\nThe correct answer is Option "+a.getAnswers()[i]);
//            }
//        }
//        //Gives user their overall score as a percentage
//        System.out.println("\nOverall you scored "+(counter/results.length)*100+"%");
//        System.out.println("____________________________________________");
//    }


}