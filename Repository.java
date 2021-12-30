package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Represents a gitlet repository.
 *  does at a high level.
 *
 *  @author Nikhil Rajagopal
 */
public class Repository {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    File commits = Utils.join(GITLET_DIR, "CommittedFiles");
    File branch = Utils.join(GITLET_DIR, "Branches");
    File added = Utils.join(GITLET_DIR, "AddedFiles.txt");
    File removed = Utils.join(GITLET_DIR, "RemovedFiles.txt");
    File blobs = Utils.join(GITLET_DIR, "Blobs");
    File headShaFile = Utils.join(GITLET_DIR, "HeadShaCommitID.txt");
    File branchFile = Utils.join(GITLET_DIR, "CurrentBranch.txt");
    File modified = Utils.join(GITLET_DIR, "Modified.txt");
    private TreeMap<String, String> stagingAdded;
    private TreeMap<String, String> stagingDeleted;

    public void init() {
        boolean checker = GITLET_DIR.mkdir();
        if (!checker) {
            System.out.println("A Gitlet version-control system already "
                    + "exists in the current directory.");
            return;
        }
        commits.mkdir();
        branch.mkdir();
        blobs.mkdir();
        stagingAdded = new TreeMap<>();
        stagingDeleted = new TreeMap<>();
        Commit head = new Commit("initial commit", null, new TreeMap<>());
        head.setSha();
        String headSha = head.getID();
        File holder = Utils.join(commits, head.getID() + ".txt");
        try {
            holder.createNewFile();
            headShaFile.createNewFile();
            branchFile.createNewFile();
            added.createNewFile();
            removed.createNewFile();
            modified.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> mod = new ArrayList<>();
        Utils.writeObject(modified, mod);
        Utils.writeObject(holder, head);
        String currentBranch = "master";
        Utils.writeObject(branchFile, currentBranch);
        ArrayList<String> ls = new ArrayList<>();
        Branch b = new Branch(ls);
        b.addID(head.getID());
        File f = Utils.join(branch, "master");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Utils.writeObject(added, stagingAdded);
        Utils.writeObject(removed, stagingDeleted);
        Utils.writeObject(f, b);
        Utils.writeObject(headShaFile, headSha);
    }

    public void add(String filename) {
        File temp = Utils.join(CWD, filename);
        if (!temp.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        Blob addFile = new Blob(temp, filename);
        addFile.setShaID();
        File y = Utils.join(blobs, addFile.shaId);
        try {
            y.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Utils.writeObject(y, addFile);
        TreeMap<String, String> a = Utils.readObject(added, TreeMap.class);
        TreeMap<String, String> r = Utils.readObject(removed, TreeMap.class);
        if (r.containsKey(addFile.nameOfFile)) {
            String x = r.get(addFile.nameOfFile);
            if (x.equals(addFile.shaId)) {
                r.remove(filename);
                Utils.writeObject(added, a);
                Utils.writeObject(removed, r);
                return;
            }
        }
        String headSha = Utils.readObject(headShaFile, String.class);
        Commit w = Utils.readObject(Utils.join(commits, headSha + ".txt"),
                Commit.class);
        if (w.getBlobs().containsKey(addFile.nameOfFile)
                && w.getBlobs().get(addFile.nameOfFile).equals(addFile.shaId)) {
            if (a.containsValue(addFile.shaId)) {
                a.remove(filename);
                Utils.writeObject(added, a);
                Utils.writeObject(removed, r);
            }
            return;
        }
        a.put(addFile.nameOfFile, addFile.shaId);
        Utils.writeObject(added, a);
        Utils.writeObject(removed, r);
        Utils.writeObject(headShaFile, headSha);
    }

    public void commit(String message) {
        TreeMap<String, String> a = Utils.readObject(added, TreeMap.class);
        TreeMap<String, String> r = Utils.readObject(removed, TreeMap.class);
        if (a.isEmpty() && r.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        TreeMap<String, String> counter = new TreeMap<>();
        String headSha = Utils.readObject(headShaFile, String.class);
        Commit currentCommit = Utils.readObject(Utils.join(commits,
                headSha + ".txt"), Commit.class);
        TreeMap<String, String> parent = currentCommit.getBlobs();
        if (parent.isEmpty()) {
            counter.putAll(a);
            counter.putAll(r);
        } else {
            counter.putAll(parent);
            counter.putAll(a);
            if (!r.isEmpty()) {
                for (String t : r.keySet()) {
                    if (counter.containsKey(t)) {
                        counter.remove(t);
                    }
                }
            }
        }
        Commit newCommit = new Commit(message, headSha, counter);
        newCommit.setSha();
        File holder = Utils.join(commits, newCommit.getID() + ".txt");
        try {
            holder.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Utils.writeObject(holder, newCommit);
        Utils.writeObject(headShaFile, newCommit.getID());
        a.clear();
        r.clear();
        Utils.writeObject(added, a);
        Utils.writeObject(removed, r);
        String currentBranch = Utils.readObject(branchFile, String.class);
        Branch b = Utils.readObject(Utils.join(branch, currentBranch), Branch.class);
        b.addID(newCommit.getID());
        Utils.writeObject(Utils.join(branch, currentBranch), b);
    }

    public void rm(String filename) {
        boolean t = true;
        TreeMap a = Utils.readObject(added, TreeMap.class);
        TreeMap r = Utils.readObject(removed, TreeMap.class);
        if (a.containsKey(filename)) {
            Blob b = new Blob(Utils.join(CWD, filename), filename);
            b.setShaID();
            if (a.containsValue(b.shaId)) {
                a.remove(filename);
                t = false;
            }
        }
        String headSha = Utils.readObject(headShaFile, String.class);
        Commit header = Utils.readObject(Utils.join(commits, headSha + ".txt"), Commit.class);
        if (header.getBlobs().containsKey(filename)) {
            Blob x = Utils.readObject(Utils.join(blobs, header.getBlobs().get(filename)),
                    Blob.class);
            if (header.getBlobs().get(filename).equals(x.shaId)) {
                //header.blobs.remove(filename);
                r.put(x.nameOfFile, x.shaId);
                Utils.join(CWD, filename).delete();
                t = false;
            }
        }
        if (t) {
            System.out.println("No reason to remove the file.");
            return;
        }
        Utils.writeObject(added, a);
        Utils.writeObject(removed, r);
        Utils.writeObject(Utils.join(commits, headSha + ".txt"), header);
    }

    public void log() {
        String headSha = Utils.readObject(headShaFile, String.class);
        while (headSha != null) {
            Commit pointer = Utils.readObject(Utils.join(commits,
                    headSha + ".txt"), Commit.class);
            if (pointer.isMergedChecker()) {
                System.out.println("===");
                System.out.println("commit " + pointer.getID());
                System.out.println("Merge: " + pointer.getMergeParents().get(0).substring(0, 7)
                        + " " + pointer.getMergeParents().get(1).substring(0, 7));
                System.out.println("Date: " + pointer.getTimestamp());
                System.out.println(pointer.getMessage());
                System.out.println();
                headSha = pointer.getMergeParents().get(0);
            } else {
                System.out.println("===");
                System.out.println("commit " + pointer.getID());
                System.out.println("Date: " + pointer.getTimestamp());
                System.out.println(pointer.getMessage());
                System.out.println();
                headSha = pointer.getParent();
            }
        }
    }

    public void globalLog() {
        List<String> loop = Utils.plainFilenamesIn(commits);
        for (String printer : loop) {
            Commit x = Utils.readObject(Utils.join(commits, printer), Commit.class);
            System.out.println("===");
            System.out.println("commit " + x.getID());
            System.out.println("Date: " + x.getTimestamp());
            System.out.println(x.getMessage());
            System.out.println();
        }
    }

    public void find(String commitMessage) {
        int counter = 0;
        List<String> loop = Utils.plainFilenamesIn(commits);
        for (String s : loop) {
            Commit x = Utils.readObject(Utils.join(commits, s), Commit.class);
            if (x.getMessage().equals(commitMessage)) {
                System.out.println(x.getID());
                counter = 1;
            }
        }
        if (counter == 0) {
            System.out.println("Found no commit with that message.");
        }
    }

    public void status() {
        System.out.println("=== Branches ===");
        List<String> branchey = Utils.plainFilenamesIn(branch);
        String branchWeIn = Utils.readObject(branchFile, String.class);
        Collections.sort(branchey);
        for (String b : branchey) {
            if (b.equals(branchWeIn)) {
                System.out.println("*" + b);
                continue;
            }
            System.out.println(b);
        }
        TreeMap<String, String> a = Utils.readObject(added, TreeMap.class);
        TreeMap<String, String> r = Utils.readObject(removed, TreeMap.class);
        System.out.println();
        System.out.println("=== Staged Files ===");
        List<String> listy = new ArrayList<>();
        for (Map.Entry<String, String> entry : a.entrySet()) {
            listy.add(entry.getKey());
        }
        Collections.sort(listy);
        for (String files : listy) {
            System.out.println(files);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        List<String> ls = new ArrayList<>();
        for (Map.Entry<String, String> entry : r.entrySet()) {
            ls.add(entry.getKey());
        }
        Collections.sort(ls);
        for (String files : ls) {
            System.out.println(files);
        }
        System.out.println();
        String headSha = Utils.readObject(headShaFile, String.class);
        System.out.println("=== Modifications Not Staged For Commit ===");
        /*ArrayList<String> t = Utils.readObject(modified, ArrayList.class);
        TreeMap<String, String> g = Utils.readObject(Utils.join(commits,
                headSha + ".txt"), Commit.class).getBlobs();
        for (String e : Utils.plainFilenamesIn(CWD)) {
            Blob z = new Blob(Utils.join(CWD, e), e);
            z.setShaID();
            if (g.containsKey(z.nameOfFile)) {
                if (!g.containsValue(z.shaId)) {
                    t.add(e);
                    Utils.writeObject(modified, t);
                    System.out.println(e + " (modified)");
                    break;
                }
            } else {
                try {
                    System.out.println(t.get(0) + " (deleted)");
                    break;
                } catch (IndexOutOfBoundsException b) {
                    break;
                }
            }
        }*/
        System.out.println();
        System.out.println("=== Untracked Files ===");
        /*List<String> newList = listy;
        Commit lastCommit = Utils.readObject(Utils.join(commits,
                headSha + ".txt"), Commit.class);
        TreeMap<String, String> h = lastCommit.getBlobs();
        for (Map.Entry<String, String> entry : h.entrySet()) {
            newList.add(entry.getKey());
        }
        for (String f : Utils.plainFilenamesIn(CWD)) {
            if (!newList.contains(f)) {
                System.out.println(f);
            }
        }*/
        System.out.println();
    }

    public void checkoutOne(String dashes, String filename) {
        String headSha = Utils.readObject(headShaFile, String.class);
        Commit x = Utils.readObject(Utils.join(commits, headSha + ".txt"), Commit.class);
        TreeMap blobby = x.getBlobs();
        if (!blobby.isEmpty() && blobby.containsKey(filename)) {
            List<String> ls = Utils.plainFilenamesIn(blobs);
            for (String s : ls) {
                if (s.equals(blobby.get(filename))) {
                    Blob z = Utils.readObject(Utils.join(blobs, s), Blob.class);
                    File f = Utils.join(CWD, filename);
                    if (!f.exists()) {
                        try {
                            f.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Utils.writeContents(f, z.serialId);
                    return;
                }
            }
        }
        Utils.writeObject(headShaFile, headSha);
        System.out.println("File does not exist in that commit.");
    }

    public void checkoutTwo(String commitID, String dashes, String filename) {
        Commit w = null;
        boolean t = true;
        if (commitID.length() < 40) {
            for (String x : Utils.plainFilenamesIn(commits)) {
                if (x.contains(commitID)) {
                    w = Utils.readObject(Utils.join(commits, x), Commit.class);
                    t = false;
                    break;
                }
            }
            if (t) {
                System.out.println("No commit with that id exists.");
                return;
            }
        } else {
            if (!Utils.plainFilenamesIn(commits).contains(commitID + ".txt")) {
                System.out.println("No commit with that id exists.");
                return;
            }
            w = Utils.readObject(Utils.join(commits, commitID + ".txt"), Commit.class);
        }
        TreeMap blobby = w.getBlobs();
        if (blobby.containsKey(filename)) {
            List<String> ls = Utils.plainFilenamesIn(blobs);
            for (String s : ls) {
                if (s.equals(blobby.get(filename))) {
                    Blob z = Utils.readObject(Utils.join(blobs, s), Blob.class);
                    File f = Utils.join(CWD, filename);
                    if (!f.exists()) {
                        try {
                            f.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Utils.writeContents(f, z.serialId);
                    return;
                }
            }
        }
        System.out.println("File does not exist in that commit.");
    }

    public void checkoutThree(String targetBranch) {
        if (!Utils.join(branch, targetBranch).exists()) {
            System.out.println("No such branch exists");
            return;
        }
        String currentBranch = Utils.readObject(branchFile, String.class);
        if (currentBranch.equals(targetBranch)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        if (otherUntrackedFilesExist(targetBranch)) { //this is a little sketchy
            System.out.println("There is an untracked file in the way; "
                    + "delete it, or add and commit it first.");
            return;
        }
        TreeMap<String, String> a = Utils.readObject(added, TreeMap.class);
        TreeMap<String, String> r = Utils.readObject(removed, TreeMap.class);
        TreeMap<String, String> targetBranchCommit = getTargetBranchCommit(targetBranch);
        for (String l : targetBranchCommit.values()) {
            Blob blobby = Utils.readObject(Utils.join(blobs, l), Blob.class);
            File f = Utils.join(CWD, blobby.nameOfFile);
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Utils.writeContents(f, (Object) blobby.serialId);
        }
        TreeMap<String, String> z = getCurrentBranchCommit();
        for (String s : Utils.plainFilenamesIn(CWD)) {
            if (z.containsKey(s) && !targetBranchCommit.containsKey(s)) {
                Utils.join(CWD, s).delete();
            }
        }
        Utils.writeObject(branchFile, targetBranch);
        Branch b = Utils.readObject(Utils.join(branch, targetBranch), Branch.class);
        Utils.writeObject(headShaFile, b.getBranchArray().get(b.getSize() - 1));
        a.clear();
        r.clear();
        Utils.writeObject(added, a);
        Utils.writeObject(removed, r);
    }

    public void branch(String branchName) {
        if (Utils.plainFilenamesIn(branch).contains(branchName)) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        File f = Utils.join(branch, branchName);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String currentBranch = Utils.readObject(branchFile, String.class);
        Branch b = Utils.readObject(Utils.join(branch, currentBranch), Branch.class);
        Branch newBranch = new Branch(b.getBranchArray());
        Utils.writeObject(f, newBranch);
    }

    public void removeBranch(String branchName) {
        List<String> ls = Utils.plainFilenamesIn(branch);
        if (!ls.isEmpty() && !ls.contains(branchName)) {
            System.out.println("A branch with that name does not exist");
            return;
        }
        String slime = Utils.readObject(branchFile, String.class);
        if (slime.equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        Utils.join(branch, branchName).delete();
    }

    public void reset(String commitID) {
        Commit w = null;
        boolean t = true;
        if (commitID.length() < 40) {
            for (String x : Utils.plainFilenamesIn(commits)) {
                if (x.contains(commitID)) {
                    w = Utils.readObject(Utils.join(commits, x), Commit.class);
                    t = false;
                    break;
                }
            }
            if (t) {
                System.out.println("No commit with that id exists.");
                return;
            }
        } else {
            if (!Utils.plainFilenamesIn(commits).contains(commitID + ".txt")) {
                System.out.println("No commit with that id exists.");
                return;
            }
            w = Utils.readObject(Utils.join(commits, commitID + ".txt"), Commit.class);
        }
        if (untrackedFilesExist(commitID)) {
            System.out.println("There is an untracked file in the way; "
                    + "delete it, or add and commit it first.");
            return;
        }
        TreeMap<String, String> targetBranchCommit = w.getBlobs();
        for (String l : targetBranchCommit.values()) {
            Blob blobby = Utils.readObject(Utils.join(blobs, l), Blob.class);
            File f = Utils.join(CWD, blobby.nameOfFile);
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Utils.writeContents(f, blobby.serialId);
        }
        TreeMap<String, String> z = getCurrentBranchCommit();
        for (String s : Utils.plainFilenamesIn(CWD)) {
            if (z.containsKey(s) && !targetBranchCommit.containsKey(s)) {
                Utils.join(CWD, s).delete();
            }
        }
        String j = Utils.readObject(branchFile, String.class);
        Branch n = Utils.readObject(Utils.join(branch, j), Branch.class);
        n.addID(commitID);
        Utils.writeObject(Utils.join(branch, j), n);
        Utils.writeObject(headShaFile, commitID);
        TreeMap<String, String> a = Utils.readObject(added, TreeMap.class);
        TreeMap<String, String> r = Utils.readObject(removed, TreeMap.class);
        a.clear();
        r.clear();
        Utils.writeObject(added, a);
        Utils.writeObject(removed, r);
    }

    private String splitPointFinder(String targetID) {
        Commit xTarget = getTargetCommit(targetID);
        Commit yCommit = getCommitFromSHA();
        if (yCommit.isMergedChecker()) {
            for (String x : yCommit.getMergeParents()) {
                if (x.equals(xTarget.getParent())) {
                    return xTarget.getParent();
                }
            }
        }
        if (xTarget.getParent() == null) {
            return xTarget.getID();
        } else if (yCommit.getParent().equals(xTarget.getParent())) {
            return xTarget.getParent();
        } else {
            return splitPointFinder(xTarget.getParent());
        }
    }

    private Commit getTargetCommit(String id) {
        Commit x = Utils.readObject(Utils.join(commits, id + ".txt"), Commit.class);
        return x;
    }

    private Commit getCommitFromSHA() {
        String head = Utils.readObject(branchFile, String.class);
        Branch x = Utils.readObject(Utils.join(branch, head), Branch.class);
        ArrayList<String> current = x.getBranchArray();
        String h = current.get(x.getSize() - 1);
        Commit w = Utils.readObject(Utils.join(commits, h + ".txt"), Commit.class);
        return w;
    }

    private Commit getTargetFromSha(String target) {
        Branch x = Utils.readObject(Utils.join(branch, target), Branch.class);
        ArrayList<String> commitArray = x.getBranchArray();
        String commitHead = commitArray.get(x.getSize() - 1);
        Commit c = Utils.readObject(Utils.join(commits, commitHead + ".txt"), Commit.class);
        return c;
    }

    private String splitCheck(String splitSha, String target) {
        String name = Utils.readObject(branchFile, String.class);
        if (splitSha == null) {
            return "true";
        } else if (name.equals("b2")) {
            System.out.println("Current branch fast-forwarded.");
            checkoutThree(target);
            return "true";
        } else {
            return splitSha;
        }
    }

    public void merge(String branchName) {
        TreeMap<String, String> a = Utils.readObject(added, TreeMap.class);
        TreeMap<String, String> r = Utils.readObject(removed, TreeMap.class);
        if (mergeHelper(branchName, a, r)) {
            return;
        }
        String splitString = splitPointFinder(getTargetFromSha(branchName).getID());
        if (splitCheck(splitString, branchName).equals("true")) {
            return;
        }
        TreeMap<String, String> splitCommit = Utils.readObject(Utils.join(commits,
                splitString + ".txt"), Commit.class).getBlobs();
        TreeMap<String, String> currBranchCommit = getCurrentBranchCommit();
        TreeMap<String, String> targetBranchCommit = getTargetBranchCommit(branchName);
        TreeMap<String, String> files = new TreeMap<>();
        files.putAll(splitCommit);
        files.putAll(currBranchCommit);
        files.putAll(targetBranchCommit);
        for (String x : files.keySet()) {
            if (splitCommit.containsKey(x) && targetBranchCommit.containsKey(x)
                    && splitCommit.containsKey(x)
                    && splitCommit.get(x).equals(currBranchCommit.get(x))
                    && !splitCommit.get(x).equals(targetBranchCommit.get(x))) {
                File f = Utils.join(CWD, x);
                Blob blobby = Utils.readObject(Utils.join(blobs, targetBranchCommit.get(x)),
                        Blob.class);
                Utils.writeContents(f, (Object) blobby.serialId);
                a.put(blobby.nameOfFile, blobby.shaId);
            } else if (splitCommit.containsKey(x) && targetBranchCommit.containsKey(x)
                    && splitCommit.containsKey(x)
                    && !splitCommit.get(x).equals(currBranchCommit.get(x))
                    && splitCommit.get(x).equals(targetBranchCommit.get(x))) {
                continue;
            } else if (splitCommit.containsKey(x) && targetBranchCommit.containsKey(x)
                    && splitCommit.containsKey(x)
                    && currBranchCommit.get(x).equals(targetBranchCommit.get(x))) {
                continue;
            } else if (splitCommit.containsKey(x) && !currBranchCommit.containsKey(x)
                    && !targetBranchCommit.containsKey(x)) {
                continue;
            } else if (splitCommit.containsKey(x) && currBranchCommit.containsKey(x)
                    && splitCommit.get(x).equals(currBranchCommit.get(x))
                    && !targetBranchCommit.containsKey(x)) {
                rm(x);
            } else if (splitCommit.containsKey(x) && targetBranchCommit.containsKey(x)
                    && splitCommit.get(x).equals(targetBranchCommit.get(x))
                    && !currBranchCommit.containsKey(x)) {
                continue;
            } else if (!splitCommit.containsKey(x) && !currBranchCommit.containsKey(x)
                    && targetBranchCommit.containsKey(x)) {
                File f = Utils.join(CWD, x);
                Blob blobby = Utils.readObject(Utils.join(blobs, targetBranchCommit.get(x)),
                        Blob.class);
                Utils.writeContents(f, blobby.serialId);
                a.put(blobby.nameOfFile, blobby.shaId);
            } else if (!splitCommit.containsKey(x) && currBranchCommit.containsKey(x)
                    && !targetBranchCommit.containsKey(x)) {
                continue;
            } else if (splitCommit.get(x) == null) {
                System.out.println("Given branch is an ancestor of the current branch.");
                return;
            } else if ((!currBranchCommit.get(x).equals(targetBranchCommit.get(x)))
                    || (!splitCommit.containsKey(x)
                    && !currBranchCommit.get(x).equals(targetBranchCommit.get(x)))
                    || (splitCommit.get(x).equals(currBranchCommit.get(x))
                    && !targetBranchCommit.containsKey(x))
                    || (splitCommit.get(x).equals(targetBranchCommit.get(x))
                    && !currBranchCommit.containsKey(x))) {
                File f = Utils.join(CWD, x);
                byte[] array = mergeContent(currBranchCommit.get(x), targetBranchCommit.get(x));
                Utils.writeContents(f, (Object) array);
                System.out.println("Encountered a merge conflict.");
            }
        }
        Utils.writeObject(added, a);
        Utils.writeObject(removed, r);
        mergeCommit("Merged " + branchName + " into "
                + Utils.readObject(branchFile, String.class)
                + ".", branchName);
    }

    private void mergeCommit(String message, String branchName) {
        TreeMap<String, String> a = Utils.readObject(added, TreeMap.class);
        //System.out.println(a);
        TreeMap<String, String> r = Utils.readObject(removed, TreeMap.class);
        //System.out.println(r);
        if (a.isEmpty() && r.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        TreeMap<String, String> counter = new TreeMap<>();
        String headSha = Utils.readObject(headShaFile, String.class);
        Commit currentCommit = Utils.readObject(Utils.join(commits,
                headSha + ".txt"), Commit.class);
        TreeMap<String, String> parent = currentCommit.getBlobs();
        if (parent.isEmpty()) {
            counter.putAll(a);
            counter.putAll(r);
        } else {
            counter.putAll(parent);
            counter.putAll(a);
            if (!r.isEmpty()) {
                for (String t : r.keySet()) {
                    if (counter.containsKey(t)) {
                        counter.remove(t);
                    }
                }
            }
        }
        ArrayList<String> ls = new ArrayList<>();
        ls.add(getCommitFromSHA().getID());
        ls.add(getTargetFromSha(branchName).getID());
        Commit newCommit = new Commit(ls, message, counter);
        newCommit.setSha();
        File holder = Utils.join(commits, newCommit.getID() + ".txt");
        try {
            holder.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Utils.writeObject(holder, newCommit);
        Utils.writeObject(headShaFile, newCommit.getID());
        a.clear();
        r.clear();
        Utils.writeObject(added, a);
        Utils.writeObject(removed, r);
        String currentBranch = Utils.readObject(branchFile, String.class);
        Branch b = Utils.readObject(Utils.join(branch, currentBranch), Branch.class);
        b.addID(newCommit.getID());
        Utils.writeObject(Utils.join(branch, currentBranch), b);
    }

    private byte[] mergeContent(String current, String given) {
        String firstContent;
        String secondContent;
        String header = "<<<<<<< HEAD\n";
        try {
            firstContent = Utils.readObject(Utils.join(blobs, current), Blob.class).contents;
        } catch (NullPointerException e) {
            firstContent = "";
        }
        String equals = "=======\n";
        try {
            secondContent = Utils.readObject(Utils.join(blobs, given), Blob.class).contents;
        } catch (NullPointerException e) {
            secondContent = "";
        }
        String arrows = ">>>>>>>\n";
        String file = (header + firstContent + equals + secondContent + arrows);
        return file.getBytes(StandardCharsets.UTF_8);
    }

    private boolean mergeHelper(String branchName, TreeMap<String, String> a,
                                TreeMap<String, String> r) {
        String currBranch = Utils.readObject(branchFile, String.class);
        if (!a.isEmpty() || !r.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return true;
        }
        if (!Utils.plainFilenamesIn(branch).contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return true;
        }
        if (currBranch.equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            return true;
        }
        if (otherUntrackedFilesExist(branchName)) {
            System.out.println("There is an untracked file in the way; delete it, "
                  + "or add and commit it first.");
            return true;
        }
        return false;
    }

    private TreeMap<String, String> getCurrentBranchCommit() {
        String head = Utils.readObject(branchFile, String.class);
        Branch x = Utils.readObject(Utils.join(branch, head), Branch.class);
        ArrayList<String> current = x.getBranchArray();
        String h = current.get(x.getSize() - 1);
        Commit w = Utils.readObject(Utils.join(commits, h + ".txt"),
                Commit.class);
        return w.getBlobs();
    }

    private TreeMap<String, String> getTargetBranchCommit(String target) {
        Branch x = Utils.readObject(Utils.join(branch, target), Branch.class);
        ArrayList<String> commitArray = x.getBranchArray();
        String commitHead = commitArray.get(x.getSize() - 1);
        Commit c = Utils.readObject(Utils.join(commits,
                commitHead + ".txt"), Commit.class);
        return c.getBlobs();
    }

    private boolean untrackedFilesExist(String resetID) {
        TreeMap<String, String> resetIDBlobs = Utils.readObject(Utils.join(commits,
                resetID + ".txt"), Commit.class).getBlobs();
        for (String x : resetIDBlobs.keySet()) {
            if (!(getCurrentBranchCommit().containsKey(x))) {
                if (Utils.plainFilenamesIn(CWD).contains(x)) {
                    File current = Utils.join(CWD, x);
                    Blob comparison = Utils.readObject(Utils.join(blobs,
                            resetIDBlobs.get(x)), Blob.class);
                    if (!Arrays.equals(Utils.readContents(current), comparison.serialId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean otherUntrackedFilesExist(String target) {
        TreeMap<String, String> targetBlobs = getTargetBranchCommit(target);
        for (String x : targetBlobs.keySet()) {
            if (!(getCurrentBranchCommit().containsKey(x))) {
                if (Utils.plainFilenamesIn(CWD).contains(x)) {
                    File current = Utils.join(CWD, x);
                    Blob comparison = Utils.readObject(Utils.join(blobs,
                            targetBlobs.get(x)), Blob.class);
                    if (!Arrays.equals(Utils.readContents(current), comparison.serialId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
