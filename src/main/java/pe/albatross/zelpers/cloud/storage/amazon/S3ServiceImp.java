package pe.albatross.zelpers.cloud.storage.amazon;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pe.albatross.zelpers.cloud.credentials.S3Credentials;
import pe.albatross.zelpers.cloud.storage.StorageService;
import pe.albatross.zelpers.file.model.Inode;

@Slf4j
@Lazy
@Service("s3Service")
@ConditionalOnSingleCandidate(S3Credentials.class)
public class S3ServiceImp implements StorageService {

    @Autowired
    BasicAWSCredentials awsCredentials;

    private static final String DELIMITER = File.separator;

    @Override
    public void uploadFileSync(String bucket, String bucketDirectory, String localDirectory, String fileName, boolean publico, boolean replace) {

        if (!localDirectory.endsWith(DELIMITER)) {
            localDirectory += DELIMITER;
        }

        if (!bucketDirectory.endsWith(DELIMITER)) {
            bucketDirectory += DELIMITER;
        }

        if (bucketDirectory.startsWith(DELIMITER)) {
            bucketDirectory = bucketDirectory.substring(1);
        }

        File file = new File(localDirectory + fileName);

        String s3Path = bucketDirectory + fileName;

        if (!replace && this.doesExist(bucket, s3Path)) {
            log.info("Already exists on S3 (Use replace to force upload)");
            return;
        }

        log.debug("Upload S3 {}:/{} - {}", bucket, s3Path, localDirectory);

        AmazonS3 s3Client = this.getAmazonS3();

        PutObjectRequest objectRequest = new PutObjectRequest(bucket, s3Path, file);

        if (publico) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setCacheControl("max-age=604800, must-revalidate");

            objectRequest.withMetadata(metadata);
            objectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
        }

        s3Client.putObject(objectRequest);
    }

    @Override
    public void uploadFileSync(String bucket, String bucketDirectory, String localDirectory, String fileName, boolean publico) {

        this.uploadFile(bucket, bucketDirectory, localDirectory, fileName, publico, false);
    }

    @Async
    @Override
    public void uploadFile(String bucket, String bucketDirectory, String localDirectory, String fileName, boolean publico) {

        this.uploadFileSync(bucket, bucketDirectory, localDirectory, fileName, publico, false);
    }

    @Async
    @Override
    public void uploadFile(String bucket, String bucketDirectory, String localDirectory, String fileName, boolean publico, boolean replace) {

        this.uploadFileSync(bucket, bucketDirectory, localDirectory, fileName, publico, replace);
    }

    @Async
    @Override
    @Deprecated
    public void deleteFile(String buket, String directory, String fileName) {

        this.deleteFile(buket, directory + fileName);
    }

    @Async
    @Override
    public void deleteFile(String buket, String path) {

        AmazonS3 s3client = this.getAmazonS3();
        s3client.deleteObject(new DeleteObjectRequest(buket, path));
    }

    @Override
    @Deprecated
    public InputStream getFile(String bucket, String directory, String fileName) {

        return this.getFile(bucket, directory + fileName);
    }

    @Override
    public InputStream getFile(String bucket, String path) {

        AmazonS3 s3client = this.getAmazonS3();
        S3Object object = s3client.getObject(new GetObjectRequest(bucket, path));

        return object.getObjectContent();
    }

    @Async
    @Override
    public void downloadFile(String bucket, String path, String pathLocal) {
        log.debug("Download S3 {} {}", bucket, path);

        InputStream in = this.getFile(bucket, path);

        File targetFile = new File(pathLocal);
        try {
            FileUtils.copyInputStreamToFile(in, targetFile);

        } catch (Exception e) {
            log.debug(e.getLocalizedMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    @Deprecated
    public boolean doesExist(String bucket, String directory, String fileName) {
        return this.doesExist(bucket, directory + fileName);
    }

    @Override
    public boolean doesExist(String bucket, String path) {
        AmazonS3 s3client = this.getAmazonS3();
        return s3client.doesObjectExist(bucket, path);
    }

    @Override
    public boolean createDirectory(String bucket, String directory) {

        AmazonS3 s3client = this.getAmazonS3();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);

        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);

        if (!directory.endsWith(DELIMITER)) {
            directory += DELIMITER;
        }

        if (directory.startsWith(DELIMITER)) {
            directory = directory.substring(1);
        }

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, directory, emptyContent, metadata);

        s3client.putObject(putObjectRequest);

        return this.doesExist(bucket, directory);
    }

    @Override
    public Inode allFile(String bucket, String directory, boolean recursive) {

        AmazonS3 s3Client = this.getAmazonS3();

        if (!directory.endsWith(DELIMITER)) {
            directory += DELIMITER;
        }

        if (directory.startsWith(DELIMITER)) {
            directory = directory.substring(1);
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

                Inode inode = this.getInodeDirectory(bucket, dir);

                if (recursive) {
                    inode = this.allFile(bucket, inode.getPath(), recursive);
                }

                inodes.add(inode);
            }

            for (S3ObjectSummary o : result.getObjectSummaries()) {
                if (o.getKey().equals(directory)) {
                    continue;
                }
                inodes.add(this.getInodeFile(o));
            }

            request.setContinuationToken(result.getNextContinuationToken());

        } while (result.isTruncated());

        Inode inode = this.getInodeDirectory(bucket, directory);
        inode.setItems(inodes);
        return inode;
    }

    private Inode getInodeDirectory(String bucket, String pathDirectory) {

        Inode inode = new Inode();
        inode.setType(Inode.Type.DIRECTORY);
        inode.setPath(pathDirectory);
        inode.setBucket(bucket);

        File file = new File(pathDirectory);
        inode.setTitle(file.getName());
        inode.setFileName(file.getName());

        if (!StringUtils.isEmpty(file.getParent())) {
            inode.setParent(this.getInodeDirectory(bucket, file.getParent()));
        }

        return inode;
    }

    private Inode getInodeFile(S3ObjectSummary summary) {

        String bucket = summary.getBucketName();
        String path = summary.getKey();

        Inode inode = new Inode();
        inode.setType(Inode.Type.FILE);
        inode.setPath(path);
        inode.setBucket(bucket);
        inode.setSize(summary.getSize());

        inode.setTitle(FilenameUtils.getBaseName(path));
        inode.setFileName(FilenameUtils.getName(path));
        inode.setExtension(FilenameUtils.getExtension(path));

        String url = String.format("https://%s.s3.amazonaws.com/%s",
                bucket, path);

        inode.setUrl(url);

        return inode;
    }

    private AmazonS3 getAmazonS3() {

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(Regions.DEFAULT_REGION)
                .build();

        return s3Client;
    }

}
