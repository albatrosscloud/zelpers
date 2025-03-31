package pe.albatross.zelpers.cloud.storage;

import java.io.InputStream;
import pe.albatross.zelpers.file.model.Inode;

public interface StorageService {

    void uploadFileSync(String bucket, String bucketDirectory, String localDirectory, String fileName, boolean publico, boolean replace);

    void uploadFileSync(String bucket, String bucketDirectory, String localDirectory, String fileName, boolean publico);

    void uploadFile(String bucket, String bucketDirectory, String localDirectory, String fileName, boolean publico, boolean replace);

    void uploadFile(String bucket, String bucketDirectory, String localDirectory, String fileName, boolean publico);

    void deleteFile(String buket, String path);

    void deleteFile(String buket, String directory, String fileName);

    InputStream getFile(String bucket, String path);

    InputStream getFile(String bucket, String directory, String fileName);

    void downloadFileSync(String bucket, String path, String pathLocal);

    void downloadFile(String bucket, String path, String localPath);

    boolean doesExist(String bucket, String path);

    boolean doesExist(String bucket, String directory, String fileName);

    Inode allFile(String bucket, String directory, boolean recursive);

    boolean createDirectory(String bucket, String directory);

    void createBucket(String bucket);

    void makeFilePublic(String bucket, String objectPath);

    String generateTemporaryAccessUrl(String bucket, String objectPath, int expirationInSeconds);
}
