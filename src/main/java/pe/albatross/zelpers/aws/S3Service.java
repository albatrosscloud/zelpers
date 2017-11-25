package pe.albatross.zelpers.aws;

import java.io.InputStream;
import java.util.List;
import pe.albatross.zelpers.file.model.Inode;

public interface S3Service {

    void uploadFile(String bucket, String bucketDirectory, String localDirectory, String fileName, boolean publico);

    void uploadFileSync(String bucket, String bucketDirectory, String localDirectory, String fileName, boolean publico);

    void deleteFile(String buket, String directory, String fileName);

    InputStream getFile(String bucket, String directory, String fileName);

    boolean doesExist(String bucket, String directory, String fileName);

    List<Inode> allFile(String bucket, String directory, boolean recursive);

}
