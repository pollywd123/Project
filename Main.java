package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author MOMO SIU and JESS MOULIA
 */
public class Main {
    static Repository newRepo = new Repository();
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = Utils.join(CWD, ".gitlet");

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        String firstArg = null;
        try {
            firstArg = args[0]; // the type of function to be executed
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        if (!firstArg.equals("init") && !GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        String secondArg=null; String thirdArg=null; String fourthArg = null;
        if (args.length > 1) {
            secondArg = args[1];
        } if (args.length > 2) {
            thirdArg = args[2];
        } if (args.length > 3) {
            fourthArg = args[3];
        }

        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                Repository.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                if (secondArg != null){
                    newRepo.add(secondArg);
                }
                break;
            case "commit":
                if (secondArg != null) {
                    newRepo.commit(secondArg);
                }
                break;
            case "checkout":
                if (secondArg == null) {
                    System.out.println("Insufficient number of args");
                    System.exit(0);
                }if (thirdArg == null) {
                    newRepo.checkout(secondArg);
                }else if (secondArg.equals("--")) {
                    newRepo.checkout(secondArg, thirdArg);
                }else if (thirdArg != null && thirdArg.equals("--")) {
                    newRepo.checkout(thirdArg, secondArg, fourthArg);
                }else {
                    System.out.println("Incorrect operands.");
                    System.exit(0);}
                break;

            case "log":
                newRepo.log();
                break;
            case "status":
                newRepo.status();
                break;
            case "rm":
                newRepo.rm(secondArg);
                break;
            case "global-log":
                newRepo.globalLog();
                break;
            case "find":
                newRepo.find(secondArg);
                break;
            case "branch":
                newRepo.branch(secondArg);
                break;
            case "rm-branch":
                newRepo.rmBranch(secondArg);
                break;
            case "reset":
                newRepo.reset(secondArg);
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
            // TODO: FILL THE REST IN
            // TODO: WE NEED TO ADD: RM-BRANCH, & RESET
        }
    }
}




//
//If a user inputs a command with the wrong number or format of operands, print the message  and exit.
//
//If a user inputs a command that requires being in an initialized Gitlet working directory (i.e.,
// one containing a .gitlet subdirectory), but is not in such a directory, print the message
// "Not in an initialized Gitlet directory."


