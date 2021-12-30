package gitlet;

import java.io.Serializable;
import java.util.ArrayList;

public class Branch implements Serializable {
    private ArrayList<String> branchArray;

    public Branch(ArrayList<String> array) {
        branchArray = array;
    }

    public void addID(String commitID) {
        branchArray.add(commitID);
    }

    public int getSize() {
        return branchArray.size();
    }

    public ArrayList<String> getBranchArray() {
        return branchArray;
    }
}

