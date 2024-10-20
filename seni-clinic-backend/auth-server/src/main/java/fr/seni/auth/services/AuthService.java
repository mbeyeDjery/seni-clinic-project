package fr.seni.auth.services;


import fr.seni.core.dtos.AuthRequest;
import fr.seni.core.dtos.AuthResponse;
import fr.seni.core.exceptions.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final RestTemplate restTemplate;

    @Value("${seni-app.keycloak.clientId}")
    private String clientId;

    @Value("${seni-app.keycloak.clientSecret}")
    private String clientSecret;

    @Value("${seni-app.keycloak.host}")
    private String host;

    @Value("${seni-app.keycloak.realm}")
    private String realm;

    @Value("${seni-app.keycloak.authBaseUrl}")
    private String authBaseUrl;

    public AuthResponse login(AuthRequest authRequest) {
        try {
            ResponseEntity<AuthResponse> responseEntity = restTemplate.postForEntity(authBaseUrl+"/token", getMultiValueMapHttpEntity(authRequest, true) , AuthResponse.class);
            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                return responseEntity.getBody();
            } else {
                throw new CustomException("Incorrect name or password", HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception e){
            log.error("Internal error : {} ", e.getMessage());
            throw new CustomException("Incorrect name or password", HttpStatus.UNAUTHORIZED);
        }
    }

    public AuthResponse refreshToken(AuthRequest authRequest){
        try {
            ResponseEntity<AuthResponse> responseEntity = restTemplate.postForEntity(authBaseUrl+"/token", getMultiValueMapHttpEntity(authRequest, false) , AuthResponse.class);
            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                return responseEntity.getBody();
            } else {
                throw new CustomException("Incorrect name or password", HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception e){
            log.error("Internal error : {} ", e.getMessage());
            throw new CustomException("Expired session", HttpStatus.UNAUTHORIZED);
        }
    }

    public void logout(AuthRequest authRequest){
        try {
            ResponseEntity<Object> responseEntity = restTemplate.postForEntity(authBaseUrl+"/logout", getMultiValueMapHttpEntity(authRequest, false) , Object.class);
        }catch (Exception e){
            log.error("Internal error : {} ", e.getMessage());
            throw new CustomException("Internal error", HttpStatus.UNAUTHORIZED);
        }
    }

    private HttpEntity<MultiValueMap<String, String>> getMultiValueMapHttpEntity(AuthRequest authRequest, Boolean login) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);

        if (login){
            requestBody.add("grant_type", "password");
            requestBody.add("username", authRequest.username());
            requestBody.add("password", authRequest.password());
        }else {
            requestBody.add("grant_type", "refresh_token");
            requestBody.add("refresh_token", authRequest.token());
        }

        return new HttpEntity<>(requestBody, headers);
    }
}
