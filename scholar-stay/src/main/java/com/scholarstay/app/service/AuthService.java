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

    public Usuario authenticate(String email, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isPresent() && usuario.get().getPassword().equals(password)) {
            return usuario.get();
        }
        return null;
    }
    public Usuario register(Usuario usuario) {
        // En una aplicación real, aquí se cifraría la contraseña
        return usuarioRepository.save(usuario);
    }
}
