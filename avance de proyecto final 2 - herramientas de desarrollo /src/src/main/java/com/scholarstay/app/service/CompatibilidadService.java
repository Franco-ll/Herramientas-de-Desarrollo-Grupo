package com.scholarstay.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scholarstay.app.model.Compatibilidad;
import com.scholarstay.app.model.PerfilAcademico;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.repository.CompatibilidadRepository;
import com.scholarstay.app.repository.UsuarioRepository;

@Service
public class CompatibilidadService {

    private final CompatibilidadRepository compatibilidadRepository;
    private final UsuarioRepository usuarioRepository;

    public CompatibilidadService(CompatibilidadRepository compatibilidadRepository, UsuarioRepository usuarioRepository) {
        this.compatibilidadRepository = compatibilidadRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public void calcularCompatibilidadesParaUsuario(Usuario usuarioA) {
        if (usuarioA.getPerfilAcademico() == null) return;
        
        List<Usuario> todosLosUsuarios = usuarioRepository.findAll();
        
        for (Usuario usuarioB : todosLosUsuarios) {
            if (usuarioA.getId().equals(usuarioB.getId()) || usuarioB.getPerfilAcademico() == null) {
                continue;
            }
            
            calcularYGuardar(usuarioA, usuarioB);
            calcularYGuardar(usuarioB, usuarioA); // Relación bidireccional si se desea
        }
    }
    
    private void calcularYGuardar(Usuario u1, Usuario u2) {
        PerfilAcademico p1 = u1.getPerfilAcademico();
        PerfilAcademico p2 = u2.getPerfilAcademico();
        
        double porcentaje = 0.0;
        StringBuilder criterios = new StringBuilder();
        
        if (p1.getCarrera() != null && p1.getCarrera().equalsIgnoreCase(p2.getCarrera())) {
            porcentaje += 30.0;
            criterios.append("Misma carrera (+30%). ");
        }
        
        if (p1.getHorarioEstudio() != null && p1.getHorarioEstudio().equalsIgnoreCase(p2.getHorarioEstudio())) {
            porcentaje += 30.0;
            criterios.append("Horarios similares (+30%). ");
        }
        
        if (p1.getHabitosRuido() != null && p1.getHabitosSueno() != null &&
            p1.getHabitosRuido().equalsIgnoreCase(p2.getHabitosRuido()) && 
            p1.getHabitosSueno().equalsIgnoreCase(p2.getHabitosSueno())) {
            porcentaje += 20.0;
            criterios.append("Hábitos compatibles (+20%). ");
        }
        
        if (p1.getNivelTolerancia() != null && p1.getNivelTolerancia().equalsIgnoreCase(p2.getNivelTolerancia())) {
            porcentaje += 20.0;
            criterios.append("Nivel de tolerancia similar (+20%).");
        }
        
        Compatibilidad comp = compatibilidadRepository.findByUsuario1IdAndUsuario2Id(u1.getId(), u2.getId());
        if (comp == null) {
            comp = new Compatibilidad(u1, u2, porcentaje, criterios.toString().trim());
        } else {
            comp.setPorcentaje(porcentaje);
            comp.setCriteriosEvaluados(criterios.toString().trim());
        }
        
        compatibilidadRepository.save(comp);
    }

    public List<Compatibilidad> obtenerCompatibilidades(Long usuarioId) {
        return compatibilidadRepository.findByUsuario1Id(usuarioId);
    }
}
