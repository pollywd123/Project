package gitlet;

// TODO: any imports you need here
import java.io.File;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import java.io.File;
import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Momo Siu, Jessica Moulia, Minghui Wang
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     * <p>
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    public static Branch headBranch;
    public static String branch = "master";
    public static String HEAD = "place holder for the most recent commit's SHA ID ";
    // changed this to HEADCommit as the spec states that these pointers should be pointing to commits, not shas
    public static Commit HEADCommit;

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    public static final File STAGE_DIR = Utils.join(GITLET_DIR, "Stage");
    public static StagingArea stage;

    // BLOBS
    public static final File BLOB_DIR = Utils.join(GITLET_DIR, "blob");

    // COMMITS
    public static final File COMMIT_DIR = Utils.join(GITLET_DIR, "commit");
    //hashMap <String, Commit> commits --> not desirable. if want to get a commit, would need to deserialize entire hashmap

    // BRANCHES
    public static final File BRANCH_DIR = Utils.join(GITLET_DIR, "branch");


    public Repository() {

    }

    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }

        // MAKING GIT INTO A DIRECTORY
        GITLET_DIR.mkdir();
        // MAKING ALL NEW DIRECTORIES
        BLOB_DIR.mkdir();
        COMMIT_DIR.mkdir();
        STAGE_DIR.mkdir();
        BRANCH_DIR.mkdir();

        Commit initial = new Commit("initial commit", "Wed Dec 31 16:00:00 1969 -0800");
        headBranch = new Branch("master", initial.getCommitID());
        HEADCommit = initial;
        HEADCommit.blobs = new HashMap<>();
        HEAD = initial.getCommitID();

        headBranch.saveBranch();
        stage = new StagingArea();
        Branch.saveHeadBranch();
        Commit.saveHEADCommit();
        HEADCommit.saveCommit();
        stage.saveStage();

    }

    //can add multiple files
    public static void add(String... filename) {
        headBranch = Branch.getHeadBranch();
        stage = StagingArea.getStage();
        HEADCommit = Commit.getHEADCommit();
        for (String f : filename) {
            stage.addFile(f);
        }
        stage.saveStage();
    }

    public static void commit(String msg) {
        if (msg.equals("")) {
            System.out.println("Please enter a commit message");
            System.exit(0);
        }
        headBranch = Branch.getHeadBranch();
        stage = StagingArea.getStage();
        HEADCommit = Commit.getHEADCommit();
        if (HEADCommit.getParent() != null){
            HEADCommit.blobs = Commit.getBlobs(HEADCommit.getParent().substring(0, 6));
        }
        if (!stage.adds.isEmpty()) {
            Commit newCommit = new Commit(msg);  //calling commit constructor in commit class
            HEADCommit = newCommit;
            HEAD = HEADCommit.getCommitID(); //TODO: staged blobs
            for (String s : stage.adds) {
                if (HEADCommit.blobs == null) {
                    HEADCommit.blobs = new HashMap<>();
                } else if (HEADCommit.blobs.containsKey(s)){
                    HEADCommit.blobs.replace(s, stage.currBlobs.get(s));
                }
                HEADCommit.blobs.put(s, stage.currBlobs.get(s));
                for (Blob b : HEADCommit.blobs.values()) {
                    b.saveBlob();
                }
            }
        } else if (stage.adds.isEmpty() && stage.removes.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        headBranch.saveBranch();
        Commit.saveHEADCommit();
        Branch.saveHeadBranch();
        stage.wipe();
        stage.saveStage();
        HEADCommit.saveCommit();
    }

    // 3 different types of checkout methods
    public void checkout(String dashes, String filename) {
        headBranch = Branch.getHeadBranch();
        stage = StagingArea.getStage();
        HEADCommit = Commit.getHEADCommit();
        if (!HEADCommit.blobs.containsKey(filename)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        } else {
            File headVersion = Utils.join(CWD, filename);
            byte[] headBlob = HEADCommit.blobs.get(filename).getContents();
            Utils.writeContents(headVersion, headBlob); //overwriting. NOT adding to stage
        }
        Branch.saveHeadBranch();
        stage.saveStage();
    }

    public void checkout(String dashes, String commitID, String filename) {
        headBranch = Branch.getHeadBranch();
        stage = StagingArea.getStage();
        HEADCommit = Commit.getHEADCommit();
        Commit c = Commit.getCommit(commitID.substring(0, 6));
        if (!c.blobs.containsKey(filename)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        } else if (c == null) {   //find out if c exists
            System.out.println("No commit with that id exists.");
            System.exit(0);
        } else {
            File headVersion = Utils.join(CWD, filename);
            byte[] cBlob = c.blobs.get(filename).getContents();
            Utils.writeContents(headVersion, cBlob); //overwriting. NOT adding to stage
        }
        Branch.saveHeadBranch();
        stage.saveStage();
    }

    //check if tracked: check the current commit
    public void checkout(String branchname) {
        headBranch = Branch.getHeadBranch();
        stage = StagingArea.getStage();
        HEADCommit = Commit.getHEADCommit();
        if (!Branch.branchExist(branchname)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        if (branchname.equals(branch)) {
            System.out.println("No need to checkout to current branch.");
            System.exit(0);
        }
        String checkedOutHEAD = Branch.getBranch(branchname).getLastCommitID();
        Commit checkedOutCommit = Commit.getCommit(checkedOutHEAD);
        HashMap<String, Blob> checkedOutBlobs = checkedOutCommit.blobs;
        //if not in commit, but in cwd & in checkout branch & diff version: warning msg
        for (String filename : checkedOutBlobs.keySet()) {
            File f = Utils.join(CWD, filename);
            if (f.exists() && (!HEADCommit.blobs.containsKey(filename))) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
            checkout("--", checkedOutCommit.getCommitID(), filename);
        }
        // in current Commit but not in check-out branch: delete
        for (String filename : HEADCommit.blobs.keySet()) {
            if (!checkedOutBlobs.containsKey(filename)) {
                File f = Utils.join(CWD, filename);
                Utils.restrictedDelete(f); // calling restricted delete on files in CWD, but not in GITLET_DIR
            }
        }
        headBranch = Branch.getBranch(branchname);
        headBranch.lastCommitID = checkedOutHEAD;
        HEADCommit = Commit.getCommit(headBranch.lastCommitID);
        Branch.saveHeadBranch();
        Commit.saveHEADCommit();
        stage.wipe();
        stage.saveStage();
    }

    public void rm(String filename) {
        headBranch = Branch.getHeadBranch();
        stage = StagingArea.getStage();
        HEADCommit = Commit.getHEADCommit();
        if (!stage.adds.contains(filename) && (!HEADCommit.blobs.containsKey(filename))) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        } else if (stage.adds.contains(filename)) {
            stage.adds.remove(filename);
        } else if (HEADCommit.blobs.containsKey(filename)) {
            stage.removes.add(filename);
            File f = Utils.join(CWD, filename);
            Utils.restrictedDelete(f);
        }
        stage.saveStage();
    }

    public void log() {
        HEADCommit = Commit.getHEADCommit();
        headBranch = Branch.getHeadBranch();
        stage = StagingArea.getStage();
        Commit temp = HEADCommit;
        while (temp != null) {
            System.out.println("===");
            System.out.print("commit ");
            System.out.println(temp.getCommitID());
            System.out.print("Date: ");
            System.out.println(temp.getTimeStamp());
            System.out.println(temp.getMessage());
            System.out.println();
            if (temp.getParent() == null) {
                return;
            }
            temp = Commit.getCommit(temp.getParent().substring(0, 6));
        }
    }

    public void status() {
        HEADCommit = Commit.getHEADCommit();
        headBranch = Branch.getHeadBranch();
        branch = headBranch.getBranchName();
        stage = StagingArea.getStage();
        System.out.println("=== Branches ===");
        for (String s : Utils.plainFilenamesIn(BRANCH_DIR)) {
            if (s.equals(branch)) {
                System.out.println('*' + s);
            }else if (s.equals("head")){
            }else {
                System.out.println(s);
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        for (String a : stage.adds) {
            System.out.println(a);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (String r : stage.removes) {
            System.out.println(r);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    public void globalLog() {
        HEADCommit = Commit.getHEADCommit();
        headBranch = Branch.getHeadBranch();
        branch = headBranch.getBranchName();
        stage = StagingArea.getStage();
        for (String s : Utils.plainFilenamesIn(COMMIT_DIR)) {
            Commit c = Commit.getCommit(s);
            System.out.println("===");
            System.out.print("commit ");
            System.out.println(c.getCommitID());
            System.out.print("Date: ");
            System.out.println(c.getTimeStamp());
            System.out.println(c.getMessage());
            System.out.println();
        }
    }

    public void find(String commitMsg) {
        int i = 0;
        for (String s : Utils.plainFilenamesIn(COMMIT_DIR)) {
            Commit c = Commit.getCommit(s);
            if (c.getMessage().equals(commitMsg)) {
                System.out.println(c.getCommitID());
                i++;
            }
        }
        if (i == 0) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }

    public void branch(String bName) {
        HEADCommit = Commit.getHEADCommit();
        headBranch = Branch.getHeadBranch();
        HEAD = Commit.getHEADCommit().getCommitID();
        if (Branch.branchExist(bName)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        Branch newBranch = new Branch(bName);
        newBranch.lastCommitID = HEAD;
        newBranch.saveBranch();
    }

    public void rmBranch(String bName){
        HEADCommit = Commit.getHEADCommit();
        headBranch = Branch.getHeadBranch();
        HEAD = Commit.getHEADCommit().getCommitID();
        if (Branch.branchExist(bName)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        if (bName.equals(headBranch.getBranchName())){
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        File f = Utils.join(BRANCH_DIR, bName);
        Utils.restrictedDelete(f);
    }

    public void reset (String commitIDtoResetTo){
        HEADCommit = Commit.getHEADCommit();
        headBranch = Branch.getHeadBranch();
        branch = headBranch.getBranchName();
        stage = StagingArea.getStage();
        HashMap<String, Blob> currentBranchBlobs = HEADCommit.blobs;
        HashMap<String, Blob> checkedOutBlobs = Commit.getCommit(commitIDtoResetTo.substring(0, 6)).blobs;
        for (String fileName : checkedOutBlobs.keySet()){
            checkout("--", commitIDtoResetTo, fileName);
        }
        for (String fileName: currentBranchBlobs.keySet()){
            if (!checkedOutBlobs.containsKey(fileName)){
                rm(fileName);
            }
        }
        for (String filename : currentBranchBlobs.keySet()) {
            File f = Utils.join(CWD, filename);
            if (f.exists() && (!currentBranchBlobs.containsKey(filename))) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }

        }HEADCommit = Commit.getCommit(commitIDtoResetTo.substring(0, 6));
        headBranch.lastCommitID = commitIDtoResetTo;
        stage.wipe();
        stage.saveStage();
        HEADCommit.saveCommit();
        Commit.saveHEADCommit();
    }


}






