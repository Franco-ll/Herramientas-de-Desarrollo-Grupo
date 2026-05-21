package com.scholarstay.app.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

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
        return actualizarPerfil(usuarioId, dto, null);
    }

    public PerfilAcademico actualizarPerfil(Long usuarioId, PerfilAcademicoDTO dto, MultipartFile avatarFile) {
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
        
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String avatarPath = saveAvatarFile(avatarFile, usuarioId);
            if (avatarPath != null) {
                perfil.setAvatar(avatarPath);
            }
        }
        
        perfil.setCarrera(dto.getCarrera());
        perfil.setHorarioEstudio(dto.getHorarioEstudio());
        perfil.setHabitosRuido(dto.getHabitosRuido());
        perfil.setHabitosSueno(dto.getHabitosSueno());
        perfil.setNivelTolerancia(dto.getNivelTolerancia());
        perfil.setBiografia(dto.getBiografia());
        perfil.setHumoVapeo(dto.getHumoVapeo() != null);
        perfil.setMascotas(dto.getMascotas() != null);
        
        perfil = perfilAcademicoRepository.save(perfil);
        usuario.setPerfilAcademico(perfil);
        usuarioRepository.save(usuario);
        
        // Recalcular compatibilidades
        compatibilidadService.calcularCompatibilidadesParaUsuario(usuario);
        
        return perfil;
    }

    private String saveAvatarFile(MultipartFile avatarFile, Long usuarioId) {
        if (avatarFile == null || avatarFile.isEmpty()) {
            return null;
        }

        String filename = StringUtils.cleanPath(avatarFile.getOriginalFilename());
        if (filename.contains("..")) {
            return null;
        }

        String extension = StringUtils.getFilenameExtension(filename);
        String storedName = usuarioId + "_" + System.currentTimeMillis() + (extension != null ? "." + extension : "");
        Path uploadDir = Paths.get("uploads", "perfiles");

        try {
            Files.createDirectories(uploadDir);
            Path destination = uploadDir.resolve(storedName);
            try (InputStream inputStream = avatarFile.getInputStream()) {
                Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
            }
            return "perfiles/" + storedName;
        } catch (IOException e) {
            throw new RuntimeException("Error guardando la imagen de perfil", e);
        }
    }

    public PerfilAcademico obtenerPorUsuario(Long usuarioId) {
        return perfilAcademicoRepository.findByUsuarioId(usuarioId);
    }
}
