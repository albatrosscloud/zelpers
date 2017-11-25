package pe.albatross.zelpers.aws;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pe.albatross.zelpers.file.model.Inode;

@Lazy
@Service
public class S3ServiceImp implements S3Service {

    @Autowired
    BasicAWSCredentials awsCredentials;

    private static final String DELIMITER = "/";

    @Override
    public void uploadFileSync(String bucket, String bucketDirectory, String localDirectory, String fileName, boolean publico) {

        File file = new File(localDirectory + fileName);

        AmazonS3 s3Client = new AmazonS3Client(awsCredentials);

        PutObjectRequest objectRequest = new PutObjectRequest(bucket, bucketDirectory + fileName, file);

        if (publico) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setCacheControl("max-age=604800, must-revalidate");

            objectRequest.withMetadata(metadata);
            objectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
        }

        s3Client.putObject(objectRequest);

    }

    @Async
    @Override
    public void uploadFile(String bucket, String bucketDirectory, String localDirectory, String fileName, boolean publico) {
        this.uploadFileSync(bucket, bucketDirectory, localDirectory, fileName, publico);
    }

    @Async
    @Override
    public void deleteFile(String buket, String directory, String fileName) {

        AmazonS3 s3client = new AmazonS3Client(awsCredentials);
        s3client.deleteObject(new DeleteObjectRequest(buket, directory + fileName));
    }

    @Override
    public InputStream getFile(String bucket, String directory, String fileName) {

        AmazonS3 s3client = new AmazonS3Client(awsCredentials);
        S3Object object = s3client.getObject(new GetObjectRequest(bucket, directory + fileName));

        return object.getObjectContent();
    }

    @Override
    public boolean doesExist(String bucket, String directory, String fileName) {

        AmazonS3 s3client = new AmazonS3Client(awsCredentials);

        return s3client.doesObjectExist(bucket, directory + fileName);

    }

    @Override
    public List<Inode> allFile(String bucket, String directory, boolean recursive) {

        AmazonS3 s3Client = new AmazonS3Client(awsCredentials);

        if (!directory.endsWith(DELIMITER)) {
            directory += DELIMITER;
        }

        if (directory.equals(DELIMITER)) {
            directory = "";
        }

        ListObjectsV2Request request = new ListObjectsV2Request()
                .withBucketName(bucket)
                .withPrefix(directory)
                .withDelimiter(DELIMITER);

        List<Inode> inodes = new ArrayList();
        ListObjectsV2Result result;

        do {

            result = s3Client.listObjectsV2(request);

            for (String dir : result.getCommonPrefixes()) {

                Inode inode = this.getInodeDirectory(dir);

                if (recursive) {
                    this.allFile(bucket, inode.getPath(), recursive);
                }

                inodes.add(inode);
            }

            for (S3ObjectSummary o : result.getObjectSummaries()) {
                if (o.getKey().equals(directory)) {
                    continue;
                }
                inodes.add(this.getInode(o));
            }

            request.setContinuationToken(result.getNextContinuationToken());

        } while (result.isTruncated());

        return inodes;

    }

    private Inode getInodeDirectory(String pathDirectory) {

        Inode inode = new Inode();
        inode.setType(Inode.Type.DIRECTORY);
        inode.setPath(pathDirectory);

        File file = new File(pathDirectory);
        inode.setTitle(file.getName());
        inode.setFileName(file.getName());

        return inode;
    }

    private Inode getInode(S3ObjectSummary summary) {

        String path = summary.getKey();

        Inode inode = new Inode();
        inode.setType(Inode.Type.FILE);
        inode.setPath(path);
        inode.setSize(summary.getSize());

        inode.setTitle(FilenameUtils.getBaseName(path));
        inode.setFileName(FilenameUtils.getName(path));
        inode.setExtension(FilenameUtils.getExtension(path));

        return inode;
    }

}
