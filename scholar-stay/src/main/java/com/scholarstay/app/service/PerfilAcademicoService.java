package com.scholarstay.app.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.scholarstay.app.dto.PerfilAcademicoDTO;
import com.scholarstay.app.model.PerfilAcademico;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.repository.PerfilAcademicoRepository;
import com.scholarstay.app.repository.UsuarioRepository;

@Service
public class PerfilAcademicoService {

    private final PerfilAcademicoRepository perfilAcademicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CompatibilidadService compatibilidadService;

    public PerfilAcademicoService(PerfilAcademicoRepository perfilAcademicoRepository, UsuarioRepository usuarioRepository, CompatibilidadService compatibilidadService) {
        this.perfilAcademicoRepository = perfilAcademicoRepository;
        this.usuarioRepository = usuarioRepository;
        this.compatibilidadService = compatibilidadService;
    }

    public PerfilAcademico actualizarPerfil(Long usuarioId, PerfilAcademicoDTO dto) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (!usuarioOpt.isPresent()) {
            throw new RuntimeException("Usuario no encontrado");
        }
        
        Usuario usuario = usuarioOpt.get();
        PerfilAcademico perfil = perfilAcademicoRepository.findByUsuarioId(usuarioId);
        
        if (perfil == null) {
            perfil = new PerfilAcademico();
            perfil.setUsuario(usuario);
        }
        
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        
        perfil.setCarrera(dto.getCarrera());
        perfil.setHorarioEstudio(dto.getHorarioEstudio());
        perfil.setHabitosRuido(dto.getHabitosRuido());
        perfil.setHabitosSueno(dto.getHabitosSueno());
        perfil.setNivelTolerancia(dto.getNivelTolerancia());
        perfil.setBiografia(dto.getBiografia());
        
        perfil = perfilAcademicoRepository.save(perfil);
        usuario.setPerfilAcademico(perfil);
        usuarioRepository.save(usuario);
        
        // Recalcular compatibilidades
        compatibilidadService.calcularCompatibilidadesParaUsuario(usuario);
        
        return perfil;
    }

    public PerfilAcademico obtenerPorUsuario(Long usuarioId) {
        return perfilAcademicoRepository.findByUsuarioId(usuarioId);
    }
}
