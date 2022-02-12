package gitlet;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static gitlet.Repository.*;


//import static gitlet.Utils.join;

public class StagingArea implements Serializable {

    // String 1: name, String 2: sha
    // change add and remove
    //

    // add wug.txt (ver 1), does not commit.
    // modify wug.txt (to ver2),
    // if we do commit, commits ver1.
    // keep a list of file names
    // add a file to gitlet dir,
    //if change the file locally, git does not recognize
    // if keep a list, pull contents from gitlet directory --> last known contents (by gitlet).

    public List<String> adds; //filenames
    public List<String> removes; //filenames
    public HashMap<String, Blob> currBlobs; //filename, blob object TODO: null during init.

    public StagingArea() {
        this.adds = new ArrayList<>();
        this.removes = new ArrayList<>();
        currBlobs = new HashMap<>(); //TODO: previously staged blobs
    }

    public void addFile(String filename) {
        File f = Utils.join(Repository.CWD, filename);
        HashMap<String, Blob> committedBlobs = HEADCommit.blobs;
        if (!f.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        Blob b = new Blob(filename); //most recent version of the file
        //need to have access to the blobs in the currCommit: know whether files are same version
        if (this.removes.contains(filename)) {
            this.removes.remove(filename);
            return;
        }
        if (committedBlobs != null && committedBlobs.containsKey(filename) && committedBlobs.get(filename).getSha().equals(b.getSha())) {
            if (this.adds != null && this.adds.contains(filename)) {
                this.adds.remove(filename);
                this.currBlobs.remove(filename);
            }
            return;
        } else if ((adds != null) && adds.contains(filename)) {
            Utils.writeObject(f, b);
        } else {
            adds.add(filename);
        } this.currBlobs.put(filename, b);
    }



    public void wipe() {
        adds.clear();
        removes.clear();
    }


    // PERSISTENCE
    public void saveStage() {
        File f = Utils.join(Repository.STAGE_DIR, "stage");
        Utils.writeObject(f, this);
    }

    public static StagingArea getStage() {
        File f = Utils.join(Repository.STAGE_DIR, "stage");
        return Utils.readObject(f, StagingArea.class);
    }
}
