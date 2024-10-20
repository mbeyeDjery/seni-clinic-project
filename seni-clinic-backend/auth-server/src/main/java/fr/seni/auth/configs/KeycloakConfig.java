package fr.seni.auth.configs;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class KeycloakConfig {

    @Value("${seni-app.keycloak.clientId}")
    private String clientId;

    @Value("${seni-app.keycloak.clientSecret}")
    private String clientSecret;

    @Value("${seni-app.keycloak.host}")
    private String host;

    @Value("${seni-app.keycloak.realm}")
    private String realm;

    @Bean
    public Keycloak keycloak(){
        return KeycloakBuilder.builder()
                .serverUrl(host)
                .realm(realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }

}