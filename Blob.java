package gitlet;
import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable {
    String contents;
    String shaId;
    byte[] serialId;
    String nameOfFile;

    public Blob(File f, String filename) {
        contents = Utils.readContentsAsString(f);
        nameOfFile = filename;
        serialId = Utils.readContents(f);
    }

    public void setShaID() {
        shaId = Utils.sha1(contents + nameOfFile);
    }
}
