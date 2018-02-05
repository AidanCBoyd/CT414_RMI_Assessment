
package ct414;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExamEngine implements ExamServer {
    ArrayList<Student> students;

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

    public static void main(String[] args) {
        Student a = new Student(14439308,"howstings","4BP1");
        Student b = new Student(14521303,"howkeepin",,"4BP1");
        Assessment test1 = new ExamPaper("28/02/2018","Java RMI test",14439308);
        Assessment test2 = new ExamPaper("02/11/2018","Java RMI test",14521303);
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
        } catch (Exception e) {
            System.err.println("ExamEngine exception:");
            e.printStackTrace();
        }
    }
}
