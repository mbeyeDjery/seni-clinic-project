package fr.seni.controllers;


import fr.seni.dtos.AuthRequest;
import fr.seni.dtos.AuthResponse;
import fr.seni.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest){
        return ResponseEntity.ok().body(authService.login(authRequest));
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody AuthRequest authRequest){
        return ResponseEntity.ok().body(authService.refreshToken(authRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody AuthRequest authRequest){
        authService.logout(authRequest);
        return ResponseEntity.ok().build();
    }

}
