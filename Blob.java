package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

//import static gitlet.Utils.join;

public class Blob implements Serializable {

    private String shortSha;
    private String shaId;
    private byte[] contents;
    private String filename;

    //Constructor
    //string = file name
    //every blob file is in BLOB_DIR
    public Blob(String fileName) {
        filename = fileName;
        //TODO: might not always be in CWD. check the gitlet folder instead.
        File f = Utils.join(Repository.CWD, fileName);
        contents = Utils.readContents(f);
        shaId = Utils.sha1(Utils.serialize(this));
        shortSha = shaId.substring(0, 6);
    }

    public String getFilename() {
        return filename;
    }
    public String getSha() {
        return shortSha;
    }
    public byte[] getContents() {
        return contents;
    }

    // PERSISTENCE
    public void saveBlob() {
        File blob = Utils.join(Repository.BLOB_DIR, shortSha);
        try {
            blob.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Utils.writeObject(blob, this);
    }

    public static Blob getBlob(String blobSha) {
        File f = Utils.join(Repository.BLOB_DIR, blobSha);
        return Utils.readObject(f, Blob.class);
    }
}
