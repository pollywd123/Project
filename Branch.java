package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


//import static gitlet.Utils.join;

public class Branch implements Serializable {

    public String branchName;
    public String lastCommitID;

    public Branch(String name, String s) {
        if (branchExist(name)){
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        branchName = name;
        lastCommitID = s;
    }
    public Branch(String s) {
        lastCommitID = s;
    }

    public String getBranchName() {return branchName;}

    public String getLastCommitID() {
        return lastCommitID;
    }

    // PERSISTENCE

    public static boolean branchExist(String branchName){
        File f = Utils.join(Repository.BRANCH_DIR, branchName);
        return f.exists();
    }

    public void saveBranch() {
        File f = Utils.join(Repository.BRANCH_DIR, branchName);
        Utils.writeObject(f, this);
    }

    public static void saveHeadBranch() {
        File f = Utils.join(Repository.BRANCH_DIR, "head");
        Utils.writeObject(f, Repository.headBranch);
    }

    public static Branch getHeadBranch() {
        File f = Utils.join(Repository.BRANCH_DIR, "head");
        return Utils.readObject(f, Branch.class);
    }

    public static Branch getBranch(String branchName) {
        File f = Utils.join(Repository.BRANCH_DIR, branchName);
        return Utils.readObject(f, Branch.class);
    }
}
