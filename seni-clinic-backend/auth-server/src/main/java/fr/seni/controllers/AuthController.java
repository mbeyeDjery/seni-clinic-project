package fr.seni.controllers;


import fr.seni.dtos.AuthRequest;
import fr.seni.dtos.AuthResponse;
import fr.seni.services.ServiceAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class AuthController {

    private final ServiceAuth serviceAuth;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest){
        return ResponseEntity.ok().body(serviceAuth.login(authRequest));
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody AuthRequest authRequest){
        return ResponseEntity.ok().body(serviceAuth.refreshToken(authRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody AuthRequest authRequest){
        serviceAuth.logout(authRequest);
        return ResponseEntity.ok().build();
    }

}
