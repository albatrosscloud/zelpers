package pe.albatross.zelpers.cloud.credentials;

import com.amazonaws.auth.BasicAWSCredentials;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@Setter
@Configuration
@ConditionalOnProperty(prefix = "aws", value = "key")
@ConfigurationProperties(prefix = "aws")
public class S3Credentials {

    String key;
    String secret;

    @Bean
    public BasicAWSCredentials awsCredentials() {
        return new BasicAWSCredentials(key, secret);
    }

}
