package com.scholarstay.app.service;

import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // El método authenticate() manual fue eliminado porque Spring Security (AuthenticationManager)
    // ahora se encarga automáticamente de procesar el POST /login y validar credenciales.

    public Usuario register(Usuario usuario) {
        // Encriptar la contraseña con BCrypt antes de guardar en la base de datos
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }
}
