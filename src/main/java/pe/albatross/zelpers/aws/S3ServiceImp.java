package pe.albatross.zelpers.aws;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class S3ServiceImp implements S3Service {

    @Autowired
    BasicAWSCredentials awsCredentials;

    @Async
    @Override
    public void uploadFile(String bucket, String bucketDirectory, String localDirectory, String fileName, boolean publico) {

        File file = new File(localDirectory + fileName);

        AmazonS3 s3Client = new AmazonS3Client(awsCredentials);

        PutObjectRequest objectRequest = new PutObjectRequest(bucket, bucketDirectory + fileName, file);

        if (publico) {
            objectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
        }

        s3Client.putObject(objectRequest);
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

        try {
            S3Object object = s3client.getObject(
                    new GetObjectRequest(bucket, directory + fileName));
            InputStream objectData = object.getObjectContent();

            objectData.close();

            return objectData;

        } catch (IOException ex) {
            System.out.println("ERROR AVATAR DOWNLOAD");
        }

        return null;
    }
}
