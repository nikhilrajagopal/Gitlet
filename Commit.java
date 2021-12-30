package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

/** Represents a gitlet commit object.
 *  does at a high level.
 *
 *  @author Nikhil Rajagopal
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    private TreeMap<String, String> blobs;
    /** The message of this Commit. */
    private String message;
    /** The timestamp. */
    private String timestamp;
    /** ID of the file. */
    private String id;
    /**Pointer to parent of last commit. will be the sha-id*/
    private String parent;
    /**Merge Thing*/
    private ArrayList<String> mergeParents;
    /**Boolean for merge*/
    private boolean mergedChecker;

    public Commit(String messageThing, String parentCommit, TreeMap<String, String> blobThings) {
        message = messageThing;
        mergedChecker = false;
        if (parentCommit == null) {
            Date date = new Date(0);
            String dateOutput = String.format("%1$ta %1$tb %1$te %1$tT %1$tY %1$tz", date);
            timestamp = dateOutput;
        } else {
            Date date = new Date();
            String dateOutput = String.format("%1$ta %1$tb %1$te %1$tT %1$tY %1$tz", date);
            timestamp = dateOutput;
        }
        parent = parentCommit;
        blobs = blobThings;
    }

    public Commit(ArrayList<String> ls, String messageThing, TreeMap<String, String> blobThings) {
        mergedChecker = true;
        message = messageThing;
        Date date = new Date();
        String dateOutput = String.format("%1$ta %1$tb %1$te %1$tT %1$tY %1$tz", date);
        timestamp = dateOutput;
        mergeParents = ls;
        blobs = blobThings;
    }
   /* Source for the Date part above:
   https://www.guru99.com/java-date.html
    */

    public void setSha() {
        if (blobs != null) {
            String inputSha = message + timestamp;
            for (String contents : blobs.values()) {
                inputSha = inputSha + contents;
            }
            id = Utils.sha1(inputSha);
        } else {
            String inputSha = message + timestamp;
            id = Utils.sha1(inputSha);
        }
    }

    public String getID() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getParent() {
        return parent;
    }

    public ArrayList<String> getMergeParents() {
        return mergeParents;
    }

    public TreeMap<String, String> getBlobs() {
        return blobs;
    }

    public boolean isMergedChecker() {
        return mergedChecker;
    }
}
