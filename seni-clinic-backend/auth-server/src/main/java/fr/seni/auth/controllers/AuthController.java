package fr.seni.auth.controllers;


import fr.seni.auth.services.AuthService;
import fr.seni.core.dtos.AuthRequest;
import fr.seni.core.dtos.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest){
        return ResponseEntity.ok(authService.login(authRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody AuthRequest authRequest){
        return ResponseEntity.ok(authService.refreshToken(authRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody AuthRequest authRequest){
        authService.logout(authRequest);
        return ResponseEntity.noContent().build();
    }
}



