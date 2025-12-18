package com.xpto.cars.controller;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.xpto.cars.dto.AuthUserDTO;
import com.xpto.cars.exception.AuthenticationException;
import com.xpto.cars.model.Usuario;
import com.xpto.cars.payload.AuthPayload;
import com.xpto.cars.service.SecurityService;
import com.xpto.cars.service.TokenService;
import com.xpto.cars.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final SecurityService securityService;
    private final TokenService tokenService;

    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuario() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @PostMapping("/login")
    public ResponseEntity<?> autenticate(@RequestBody AuthUserDTO authUserDTO) {
        try {
            String authenticate = securityService.authenticate(authUserDTO);
            return ResponseEntity.ok(new AuthPayload(authenticate));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Usuario ou senha invalidos"));
        }
    }

    @GetMapping("/my-profile")
    public ResponseEntity<?> getMyProfile(
            @RequestHeader(value = "authorization", required = true) String token) {
        try {
            String usuario = tokenService.getUsuario(tokenService.isValid(token));
            Optional<Usuario> byId = usuarioService.findById(Long.valueOf(usuario));
            return byId.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (SignatureVerificationException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Not Autorized"));
        }
    }
}
