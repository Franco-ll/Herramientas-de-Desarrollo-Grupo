package com.scholarstay.app.service;

import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public boolean authenticate(String email, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        // Simple password check without encryption for now
        return usuario.isPresent() && usuario.get().getPassword().equals(password);
    }
}
