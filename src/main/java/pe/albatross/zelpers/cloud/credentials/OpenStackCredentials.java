package pe.albatross.zelpers.cloud.credentials;

import lombok.Getter;
import lombok.Setter;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConditionalOnProperty(prefix = "swift", value = "auth-api")
@ConfigurationProperties(prefix = "swift")
public class OpenStackCredentials {

    private String authApi;
    private String user;
    private String pass;
    private String domain;
    private String project;
    private String urlBase;

    public OSClientV3 autenticate() {

        return OSFactory.builderV3()
                .endpoint(authApi)
                .credentials(user, pass, Identifier.byId(domain))
                .scopeToProject(Identifier.byId(project))
                .authenticate();
    }

}
