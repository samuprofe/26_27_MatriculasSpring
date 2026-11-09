package es.iesjuanbosco.matriculasspring.controller;

import es.iesjuanbosco.matriculasspring.DTO.UsuarioRequest;
import es.iesjuanbosco.matriculasspring.DTO.UsuarioResponse;
import es.iesjuanbosco.matriculasspring.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> register(@Valid @RequestBody UsuarioRequest usuarioRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(usuarioRequest));
    }

}
