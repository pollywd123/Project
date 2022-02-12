package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


//import static gitlet.Utils.join;


/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Momo Siu, Jessica Moulia, Minghui Wang
 */
public class Commit implements Serializable {
    /**
     * @author TODO
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */

    private String commitID;
    private String shortID;
    private String parentCommitID; //SAVE THE HASH OF THE PREVIOUS COMMIT INSTEAD. doNOT save a pointer
    private String message;
    private String timestamp;
    //private ArrayList<Commit> children; //save children's hash instead of commits (to save space).
    public HashMap<String, Blob> blobs; //filename, blob
    //pointer from hash to blob

    /* TODO: fill in the rest of this class. */
    public Commit(String msg) {
        message = msg;
        Date now = new Date();
        SimpleDateFormat d = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        timestamp = d.format(now);
        parentCommitID = Repository.HEADCommit.getCommitID();
        commitID = Utils.sha1(Utils.serialize(this)); //do we need to serilize this into bytes first??
        shortID = commitID.substring(0, 6);
    }

    public Commit(String msg, String time) {
        message = msg;
        timestamp = time;
        commitID = Utils.sha1(Utils.serialize(this));
        shortID = commitID.substring(0, 6);
    }



//helper for getCommit
    public static boolean commitExist(String sha){
        File f = Utils.join(Repository.COMMIT_DIR, sha);
        return f.exists();
    }

    public static Commit getCommit(String sha){
        if (!commitExist(sha)){
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        File f = Utils.join(Repository.COMMIT_DIR, sha);
        return Utils.readObject(f, Commit.class);
    }

    public static Commit getHEADCommit(){
        File f = Utils.join(Repository.COMMIT_DIR, "HEADCommit");
        return Utils.readObject(f, Commit.class);
    }

    public static void saveHEADCommit(){
        File f = Utils.join(Repository.COMMIT_DIR, "HEADCommit");
        Utils.writeObject(f, Repository.HEADCommit);
    }


    //Filename, Sha of the Blob
    public static HashMap<String, Blob> getBlobs(String commitSHA){
        //TODO: need to fill in & connect the commit to its respective blobs
        File f = Utils.join(Repository.COMMIT_DIR, commitSHA);
        Commit c = Utils.readObject(f, Commit.class);
        return c.blobs;
    }

    public String getCommitID () { return this.commitID; }
    public String getParent() { return this.parentCommitID; }
    public String getMessage() { return this.message; }
    public String getTimeStamp() { return this.timestamp; }

    // PERSISTENCE
    public void saveCommit() {
        File f = Utils.join(Repository.COMMIT_DIR, this.shortID);
        Utils.writeObject(f, this);
    }

}
