package fr.seni.config.keycloak;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter(AccessLevel.PROTECTED)
public class KeycloakUtils {

    @Value("${seni-app.keycloak.realm}")
    private String keycloakRealm;

    @Value("${seni-app.keycloak.admin.clientId}")
    private String keycloakClientId;

    @Value("${seni-app.keycloak.serverUrl}")
    private String keycloakServerUrl;

    @Value("${seni-app.keycloak.admin.clientSecret}")
    private String keycloakClientSecret;

    @Value("${seni-app.keycloak.admin.username}")
    private String keycloakUsername;

    @Value("${seni-app.keycloak.admin.password}")
    private String keycloakPassword;
}
