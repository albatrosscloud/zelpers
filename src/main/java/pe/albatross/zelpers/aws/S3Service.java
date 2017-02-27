package pe.albatross.zelpers.aws;

import java.io.InputStream;

public interface S3Service {

    void uploadFile(String bucket, String bucketDirectory, String localDirectory, String fileName, boolean publico);

    void uploadFileSync(String bucket, String bucketDirectory, String localDirectory, String fileName, boolean publico);

    void deleteFile(String buket, String directory, String fileName);

    InputStream getFile(String bucket, String directory, String fileName);

    boolean doesExist(String bucket, String directory, String fileName);
}
