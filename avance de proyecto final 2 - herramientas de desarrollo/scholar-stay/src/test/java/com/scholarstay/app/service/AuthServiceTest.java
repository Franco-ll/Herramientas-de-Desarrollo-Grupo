package com.scholarstay.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setNombre("Clara Belmonte");
        usuario.setEmail("clara.b@universidad.edu");
        usuario.setPassword("miPasswordSeguro123");
    }

    @Test
    void register_EncriptaPasswordYGuardaUsuario_Exito() {
        when(passwordEncoder.encode("miPasswordSeguro123")).thenReturn("passwordEncriptadoBCrypt");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario resultado = authService.register(usuario);

        assertNotNull(resultado);
        assertEquals("passwordEncriptadoBCrypt", resultado.getPassword());
        assertEquals("Clara Belmonte", resultado.getNombre());
        assertEquals("clara.b@universidad.edu", resultado.getEmail());

        verify(passwordEncoder, times(1)).encode("miPasswordSeguro123");
        verify(usuarioRepository, times(1)).save(usuario);
    }
}
