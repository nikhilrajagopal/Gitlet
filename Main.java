package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Nikhil Rajagopal
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        Repository r = new Repository();
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        if (!args[0].equals("init") && !Repository.GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        String command = args[0];
        if (command.equals("commit") && args[1].equals("")) {
            System.out.println("Please enter a commit message.");
            return;
        }
        switch(command) {
            case "init":
                r.init();
                return;
            case "add":
                try {
                    r.add(args[1]);
                } catch (ArrayIndexOutOfBoundsException a) {
                    System.out.println("Incorrect operands.");
                    return;
                }
                return;
            case "commit":
                try {
                    r.commit(args[1]);
                } catch (ArrayIndexOutOfBoundsException a) {
                    System.out.println("Incorrect operands.");
                    return;
                }
                return;
            case "rm":
                try {
                    r.rm(args[1]);
                } catch (ArrayIndexOutOfBoundsException a) {
                    System.out.println("Incorrect operands.");
                    return;
                }
                return;
            case "log":
                r.log();
                return;
            case "global-log":
                r.globalLog();
                return;
            case "find":
                try {
                    r.find(args[1]);
                } catch (ArrayIndexOutOfBoundsException a) {
                    System.out.println("Incorrect operands.");
                    return;
                }
                return;
            case "status":
                r.status();
                return;
            case "checkout":
                if (args.length == 2) {
                    r.checkoutThree(args[1]);;
                    return;
                } else if (args.length == 3) {
                    if (args[1].equals("--")) {
                        r.checkoutOne(args[1], args[2]);
                        return;
                    } else {
                        System.out.println("Incorrect operands.");
                        return;
                    }
                } else if (args.length == 4) {
                    if (args[2].equals("--")) {
                        r.checkoutTwo(args[1], args[2], args[3]);
                        return;
                    } else {
                        System.out.println("Incorrect operands.");
                        return;
                    }
                } else {
                    System.out.println("Incorrect operands.");
                    return;
                }
            case "branch":
                try {
                    r.branch(args[1]);
                } catch (ArrayIndexOutOfBoundsException a) {
                    System.out.println("Incorrect operands.");
                    return;
                }
                return;
            case "rm-branch":
                try {
                    r.removeBranch(args[1]);
                } catch (ArrayIndexOutOfBoundsException a) {
                    System.out.println("Incorrect operands.");
                    return;
                }
                return;
            case "reset":
                try {
                    r.reset(args[1]);
                } catch (ArrayIndexOutOfBoundsException a) {
                    System.out.println("Incorrect operands.");
                    return;
                }
                return;
            case "merge":
                try {
                    r.merge(args[1]);
                } catch (ArrayIndexOutOfBoundsException a) {
                    System.out.println("Incorrect operands.");
                    return;
                }
                return;
            default:
                System.out.println("No command with that name exists.");
        }
    }
}
