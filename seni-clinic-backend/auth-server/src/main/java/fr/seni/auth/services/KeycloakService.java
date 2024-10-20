package fr.seni.auth.services;

import fr.seni.auth.configs.KeycloakCredentials;
import fr.seni.core.dtos.AppRoleDto;
import fr.seni.core.dtos.AppUserDto;
import fr.seni.core.exceptions.CustomException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakService {

    @Value("${seni-app.keycloak.realm}")
    private String realm;
    private final Keycloak keycloak;

    public AppUserDto createAppUser(AppUserDto appUserDto) {
        try {
            Response response = getUsersResource().create(mapAppUserToKeycloakUser(appUserDto));
            if(!Objects.equals(201,response.getStatus())){
                if (Objects.equals(409,response.getStatus())) {
                    throw new CustomException("Nom d'utilisateur déja utilisé", HttpStatus.CONFLICT);
                }else {
                    throw new CustomException("Internal error", HttpStatus.BAD_REQUEST);
                }
            }
            String userId = CreatedResponseUtil.getCreatedId(response);
            appUserDto.getRoles().forEach(role -> addRoleToUser(userId, role.getRoleName()));
            return getUserById(userId);
        }catch (Exception e){
            log.error("Internal error : {} ", e.getMessage());
            throw new CustomException("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public AppUserDto updateUser(String userId, AppUserDto appUserDto) {
        try {
            getUsersResource().get(userId).update(mapAppUserToKeycloakUser(appUserDto));
            return getUserById(userId);
        }catch (Exception e){
            log.error("Internal error : {} ", e.getMessage());
            if (e instanceof NotFoundException){
                throw new CustomException("Account not found", HttpStatus.NOT_FOUND);
            }else {
                throw new CustomException("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    public void deleteUserById(String userId) {
        try {
            Response response = getUsersResource().delete(userId);
        }catch (Exception e){
            throw new CustomException("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<AppUserDto> getAllUsers() {
        try {
            return getUsersResource().list().stream()
                    .map(this::mapKeycloakUserToAppUser)
                    .collect(Collectors.toList());
        }catch (Exception e){
            log.error("Internal error : {} ", e.getMessage());
            throw new CustomException("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public AppUserDto getUserById(String userId) {
        try {
            return mapKeycloakUserToAppUser(getUsersResource().get(userId).toRepresentation());
        }catch (Exception e){
            log.error("Internal error : {} ", e.getMessage());
            if (e instanceof NotFoundException){
                throw new CustomException("Account not found", HttpStatus.NOT_FOUND);
            }else {
                throw new CustomException("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    public List<AppUserDto> searchByUsername(String username, boolean exact) {
        return getUsersResource().searchByUsername(username, exact).stream().map(this::mapKeycloakUserToAppUser).collect(Collectors.toList());

    }

    public List<AppUserDto> searchByEmail(String email, boolean exact) {
        return getUsersResource().searchByEmail(email, exact).stream().map(this::mapKeycloakUserToAppUser).collect(Collectors.toList());

    }

    public void updatePassword(String userId, String newPassword) {
        try {
            getUsersResource().get(userId).resetPassword(KeycloakCredentials.createPasswordCredentials(newPassword));
        }catch (Exception e){
            log.error("Internal error : {} ", e.getMessage());
            throw new CustomException("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void emailVerification(String userId) {
        getUsersResource().get(userId).sendVerifyEmail();
    }

    public Set<AppRoleDto> getUserRole(String userId) {
        return getUsersResource().get(userId).roles().realmLevel()
                .listAll()
                .stream()
                .filter(role -> role.getName().startsWith("ROLE_"))
                .map(role -> AppRoleDto.builder()
                        .roleId("")
                        .roleName(role.getName())
                        .build())
                .collect(Collectors.toSet());

    }

    public void addRoleToUser(String userId, String roleName) {
        try {
            List<RoleRepresentation> roleToAdd = new LinkedList<>();
            roleToAdd.add(getRolesResource().get(roleName).toRepresentation());
            getUsersResource().get(userId).roles().realmLevel().add(roleToAdd);
        }catch (Exception e){
            log.error("Internal error : {} ", e.getMessage());
            if (e instanceof NotFoundException){
                throw new CustomException("Internal error", HttpStatus.NOT_FOUND);
            }else {
                throw new CustomException("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    public void removeRoleFromUser(String userId, String roleName) {
        try {
            List<RoleRepresentation> roleToRemove = new LinkedList<>();
            roleToRemove.add(getRolesResource().get(roleName).toRepresentation());
            getUsersResource().get(userId).roles().realmLevel().remove(roleToRemove);
        }catch (Exception e){
            log.error("Internal error : {} ", e.getMessage());
            if (e instanceof NotFoundException){
                throw new CustomException("Internal error", HttpStatus.NOT_FOUND);
            }else {
                throw new CustomException("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    public List<RoleRepresentation> getAllRealmRoles() {
        return getRolesResource().list();
    }

    public List<RoleRepresentation> getAllClientmRoles(String clientId) {
        ClientsResource clientsResource = keycloak.realm(realm).clients() ;
        Optional<ClientRepresentation> client = clientsResource.findByClientId(clientId).stream().findFirst();
        List<RoleRepresentation> roles = new ArrayList<>();
        if(client.isPresent()){
            String id = client.orElseThrow().getId();
            roles = clientsResource.get(id).roles().list();
        }

        return roles;
    }

    private static UserRepresentation mapAppUserToKeycloakUser(AppUserDto appUserDto) {
        UserRepresentation userRepresentation= new UserRepresentation();

//        Definir les infos principales de l'utilisateur
        userRepresentation.setEmail(appUserDto.getEmail());
        userRepresentation.setEnabled(appUserDto.getEnabled());
        userRepresentation.setLastName(appUserDto.getLastName());
        userRepresentation.setUsername(appUserDto.getUsername());
        userRepresentation.setFirstName(appUserDto.getFirstName());
        userRepresentation.setEmailVerified(appUserDto.getEnabled());

//        Definir les attributs  supplementaire
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("telephone", appUserDto.getTelephone() != null ? List.of(appUserDto.getTelephone()) : null);
        userRepresentation.setAttributes(attributes);

//        Definit le mot de passe si c'est pas null
        if (appUserDto.getPassword() != null && !appUserDto.getPassword().isBlank()){
            userRepresentation.setCredentials(Collections.singletonList(KeycloakCredentials.createPasswordCredentials(appUserDto.getPassword())));
        }

        return userRepresentation;
    }

    public AppUserDto mapKeycloakUserToAppUser(UserRepresentation userRepresentation) {
        AppUserDto appUserDto = new AppUserDto();
        appUserDto.setEmail(userRepresentation.getEmail());
        appUserDto.setEnabled(userRepresentation.isEnabled());
        appUserDto.setLastName(userRepresentation.getLastName());
        appUserDto.setUsername(userRepresentation.getUsername());
        appUserDto.setFirstName(userRepresentation.getFirstName());
//        appUserDto.setCreatedDate(Instant.ofEpochMilli(userRepresentation.getCreatedTimestamp()));
        appUserDto.setIdUser(userRepresentation.getId());
        appUserDto.setRoles(getUserRole(userRepresentation.getId()));
        Map<String, List<String>> attributes = userRepresentation.getAttributes();
        if (attributes != null) {
            appUserDto.setTelephone(attributes.get("telephone") != null ? attributes.get("telephone").get(0) : null);
        }
        return appUserDto;
    }

    private UsersResource getUsersResource(){
        return keycloak.realm(realm).users();
    }
    private RolesResource getRolesResource(){
        return keycloak.realm(realm).roles();
    }
}
