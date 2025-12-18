package com.xpto.cars.service;

import com.xpto.cars.dto.AuthUserDTO;
import com.xpto.cars.exception.AuthenticationException;
import com.xpto.cars.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private final UsuarioService usuarioService;
    private final TokenService tokenService;

    public String authenticate(AuthUserDTO authUserDTO) throws AuthenticationException {
        Optional<Usuario> byEmail = usuarioService.findByEmail(authUserDTO.email());

        if (byEmail.isEmpty())
            throw new AuthenticationException("Usuario ou senha incorretos");

        Usuario usuario = byEmail.get();

        if (usuario.getPassword().equals(authUserDTO.password()))
            return tokenService.generateToken(usuario);
        else
            throw new AuthenticationException("Usuario ou senha incorretos");
    }
}