package pe.albatross.zelpers.cloud.credentials;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import javax.annotation.PostConstruct;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Configuration
@ConditionalOnProperty(prefix = "aws", value = "key")
@ConfigurationProperties(prefix = "aws")
public class S3Credentials {

    String key;
    String secret;
    String region;

    private BasicAWSCredentials credentials;

    @PostConstruct
    public void newCredentials() {
        credentials = new BasicAWSCredentials(key, secret);
    }

    public AmazonS3 getAmazonS3() {

        Regions reg = Regions.fromName(region);

        if (reg == null) {
            reg = Regions.DEFAULT_REGION;
        }

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(reg)
                .build();

        return s3Client;
    }

}
