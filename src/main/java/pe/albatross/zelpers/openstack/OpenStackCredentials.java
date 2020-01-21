package pe.albatross.zelpers.openstack;

import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;

public class OpenStackCredentials {

    private String authAPI;
    private String user;
    private String pass;
    private String domainId;
    private String projectId;
    private String urlBase;

    public OpenStackCredentials(String authAPI, String user, String pass, String domainId, String projectId) {
        this.authAPI = authAPI;
        this.user = user;
        this.pass = pass;
        this.domainId = domainId;
        this.projectId = projectId;
    }

    public OSClientV3 autenticate() {

        return OSFactory.builderV3()
                .endpoint(authAPI)
                .credentials(user, pass, Identifier.byId(domainId))
                .scopeToProject(Identifier.byId(projectId))
                .authenticate();
    }

    public String getAuthAPI() {
        return authAPI;
    }

    public void setAuthAPI(String authAPI) {
        this.authAPI = authAPI;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public String getUrlBase() {
        return urlBase;
    }

    public void setUrlBase(String urlBase) {
        this.urlBase = urlBase;
    }

}
