package fr.seni.auth.controllers;


import fr.seni.auth.services.AppUserService;
import fr.seni.auth.services.KeycloakService;
import fr.seni.core.dtos.AppUserDto;
import fr.seni.core.exceptions.CustomException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final AppUserService appUserService;
    private final KeycloakService keycloakService;

    @PostMapping
    public ResponseEntity<AppUserDto> createUser(@Valid @RequestBody AppUserDto appUserDto) {
        if (appUserDto.getIdUser() != null) {
            throw new CustomException("A new user cannot already have an ID", HttpStatus.BAD_REQUEST);
        } else if (appUserService.findByUsername(appUserDto.getUsername()) != null) {
            throw new CustomException("Username already used", HttpStatus.CONFLICT);
        } else if (appUserService.findByEmail(appUserDto.getEmail()) != null) {
            throw new CustomException("Email already used", HttpStatus.CONFLICT);
        }else if (appUserService.findByTelephone(appUserDto.getTelephone()) != null) {
            throw new CustomException("Phone already used", HttpStatus.CONFLICT);
        }else {
            appUserDto.setEmail(appUserDto.getEmail().toUpperCase());
            appUserDto.setFirstName(appUserDto.getFirstName().toUpperCase());
            return ResponseEntity.ok(appUserService.create(keycloakService.createAppUser(appUserDto)));
        }
    }

    @PutMapping
    public ResponseEntity<AppUserDto> updateUser(@Valid @RequestBody AppUserDto appUserDto) {
       return ResponseEntity.ok(appUserService.update(keycloakService.updateUser(appUserDto.getIdUser(), appUserDto)));
    }

    @DeleteMapping("/{idUser}")
    public ResponseEntity<Void> deleteUser(@PathVariable("idUser") String idUser) {
        AppUserDto appUserDto = appUserService.findOne(idUser);
        if (appUserDto == null){
            throw new CustomException("Account not found", HttpStatus.NOT_FOUND);
        }
        keycloakService.deleteUserById(appUserDto.getIdUser());
        appUserService.delete(appUserDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<AppUserDto> getAllUsers(){
        List<AppUserDto> appUserDtoList = appUserService.findAll();
        for (AppUserDto appUserDto : appUserDtoList) {
            appUserDto.setRoles(keycloakService.getUserRole(appUserDto.getIdUser()));
        }
        return appUserDtoList;
    }

    @GetMapping("/{idUser}")
    public ResponseEntity<AppUserDto> getUser(@PathVariable("idUser") String idUser) {
        AppUserDto appUserDto = appUserService.findOne(idUser);
        if (appUserDto == null){
            throw new CustomException("Account not found", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(appUserDto);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<AppUserDto> searchUserByUsername(@PathVariable("username") String username) {
        AppUserDto appUserDto = appUserService.findByUsername(username);
        if (appUserDto == null){
            throw new CustomException("Account not found", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(appUserDto);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<AppUserDto> searchUserByEmail(@PathVariable("email") String email) {
        AppUserDto appUserDto = appUserService.findByTelephone(email);
        if (appUserDto == null){
            throw new CustomException("Account not found", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(appUserDto);
    }

    @GetMapping("/phone/{telephone}")
    public ResponseEntity<AppUserDto> searchUserByPhone(@PathVariable("telephone") String telephone) {
        AppUserDto appUserDto = appUserService.findByTelephone(telephone);
        if (appUserDto == null){
            throw new CustomException("Account not found", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(appUserDto);
    }

    @GetMapping("/enabled/{enabled}")
    public ResponseEntity<List<AppUserDto>> searchUserByEnabled(@PathVariable("enabled") Boolean enable) {
        return ResponseEntity.ok(appUserService.findAllByEnabled(enable));
    }
}
