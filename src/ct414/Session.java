package ct414;

/**
 * Created by Aidan Boyd on 05/02/18.
 */

import java.io.Serializable;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Session extends TimerTask implements Serializable{

    /**
     * Creates a timer object that runs in a new thread and counts up to the 900 second max mark
     *
     * Once the 900 second mark is reached the session will become invalid and the user will be logged out
     */
    private static final long serialVersionUID = 1L;
    private int studentID;
    private int timeRunning;
    private Timer timer;
    private Student student;
    private  boolean threadActive;
    private static long sessionLength;

    public Session(int id, Student s) {
        this.threadActive = true;
        Date now = new Date();

        // Set session length to 15 minutes
        sessionLength = 900;
        this.studentID = id;
        this.timeRunning = 0;
        this.student = s;
        this.timer = new Timer();
        this.startTimer();
    }

    // Start second timer
    private void startTimer() {
        this.timer.scheduleAtFixedRate(this, new Date(System.currentTimeMillis()), 1000);
    }

    @Override
    public void run() {
        this.timeRunning++;
        if (sessionLength <= 0) {
            this.threadActive = false;
            this.timer.cancel();
            System.out.println("Session has expired for student with ID: " + this.studentID + " closed.");
            System.out.println(this);
        }
        else if (this.timeRunning == sessionLength) {
            this.threadActive = false;
            this.timer.cancel();
            System.out.println("Session for student with ID: " + this.studentID + " closed.");
            System.out.println(this);
        }
    }

    public boolean isActiveSession() {
        return this.threadActive;
    }

}
