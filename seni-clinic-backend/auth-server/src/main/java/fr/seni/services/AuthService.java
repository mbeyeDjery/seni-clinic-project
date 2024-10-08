package fr.seni.services;


import fr.seni.config.KeycloakUtils;
import fr.seni.dtos.AuthRequest;
import fr.seni.dtos.AuthResponse;
import fr.seni.exceptions.CustomException;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final Keycloak keycloak;
    private final RestTemplate restTemplate;
    private final KeycloakUtils keycloakUtils;

    @Value("${seni-app.keycloak.authBaseUrl}")
    private String keycloakAuthUrl;

    public AuthResponse login(AuthRequest authRequest){
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("grant_type", "password");
            requestBody.add("client_id", keycloakUtils.getKeycloakClientId());
            requestBody.add("client_secret", keycloakUtils.getKeycloakClientSecret());
            requestBody.add("username", authRequest.username());
            requestBody.add("password", authRequest.password());
            return getAuthResponse(headers, requestBody);
        } catch (HttpClientErrorException e){
            if (e.getMessage().contains("Account disabled")){
                throw new CustomException("Compte desactivé. Veuillez contacter le service assistance", HttpStatus.UNAUTHORIZED);
            }
            throw new CustomException("Nom ou mot de passe incorrect", HttpStatus.UNAUTHORIZED);
        } catch (Exception e){
            throw new CustomException("Connexion impossible", HttpStatus.UNAUTHORIZED);
        }
    }

    public AuthResponse refreshToken(AuthRequest authRequest){
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("grant_type", "refresh_token");
            requestBody.add("client_id", keycloakUtils.getKeycloakClientId());
            requestBody.add("client_secret", keycloakUtils.getKeycloakClientSecret());
            requestBody.add("refresh_token", authRequest.password());
            return getAuthResponse(headers, requestBody);
        }catch (HttpClientErrorException e){
            throw new CustomException("Session expirée", HttpStatus.UNAUTHORIZED);
        }catch (Exception e){
            throw new CustomException("Connexion impossible", HttpStatus.UNAUTHORIZED);
        }
    }

    public void logout(AuthRequest authRequest){
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("client_id", keycloakUtils.getKeycloakClientId());
            requestBody.add("client_secret", keycloakUtils.getKeycloakClientSecret());
            requestBody.add("refresh_token", authRequest.password());
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Void> responseEntity = restTemplate.postForEntity(keycloakAuthUrl+"/logout",
                    requestEntity , Void.class);

            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                throw new Exception("Erreur keycloak");
            }
        }catch (Exception e){
            throw new CustomException("Erreur lors de la déconnexion", HttpStatus.UNAUTHORIZED);
        }
    }

    private AuthResponse getAuthResponse(HttpHeaders headers, MultiValueMap<String, String> requestBody) throws Exception {
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<AuthResponse> responseEntity = restTemplate.postForEntity(keycloakAuthUrl+"/token",
                requestEntity , AuthResponse.class);

        if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
            return responseEntity.getBody();
        } else {
            throw new Exception("Erreur keycloak");
        }
    }

    private UsersResource getUsersResource(){
        return keycloak.realm(keycloakUtils.getKeycloakRealm()).users();
    }

    private RolesResource getRolesResource(){
        return keycloak.realm(keycloakUtils.getKeycloakRealm()).roles();
    }
}
