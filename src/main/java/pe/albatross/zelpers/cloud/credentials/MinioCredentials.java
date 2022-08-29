package pe.albatross.zelpers.cloud.credentials;

import io.minio.MinioClient;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioCredentials {

    String endpoint;
    String user;
    String pass;

    public MinioClient autenticate() {

        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(user, pass)
                .build();
    }

}
