package com.scholarstay.app.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scholarstay.app.dto.MatchDTO;
import com.scholarstay.app.dto.PerfilDTO;
import com.scholarstay.app.model.Grupo;
import com.scholarstay.app.model.PerfilAcademico;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.repository.GrupoRepository;
import com.scholarstay.app.repository.PerfilAcademicoRepository;

@Service
public class ComunidadService {

    private final PerfilAcademicoRepository perfilAcademicoRepository;
    private final GrupoRepository grupoRepository;

    public ComunidadService(PerfilAcademicoRepository perfilAcademicoRepository,
                            GrupoRepository grupoRepository) {
        this.perfilAcademicoRepository = perfilAcademicoRepository;
        this.grupoRepository = grupoRepository;

        // Seed inicial de grupos si la tabla está vacía
        if (this.grupoRepository.count() == 0) {
            Grupo g1 = new Grupo("Sustentabilidad en Espacios Reducidos",
                    "Análisis de optimización de m² y su impacto en la salud mental.",
                    "Arquitectura",
                    "sustentabilidad, diseño, eficiencia",
                    12);
            Grupo g2 = new Grupo("Bioética y el Genoma Humano",
                    "Resúmenes de papers sobre edición genética.",
                    "Medicina",
                    "bioética, genética, investigación",
                    8);
            Grupo g3 = new Grupo("Círculo de Programación",
                    "Algoritmos y estructuras de datos para concursos y tesis.",
                    "Ciencias de la Computación",
                    "algoritmos,programación,ia",
                    20);
            grupoRepository.saveAll(Arrays.asList(g1, g2, g3));
        }
    }

    public List<String> getAllCarreras() {
        return perfilAcademicoRepository.findAll().stream()
                .map(PerfilAcademico::getCarrera)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> getAllIntereses() {
        Set<String> intereses = new HashSet<>();
        perfilAcademicoRepository.findAll().forEach(p -> {
            if (p.getIntereses() != null && !p.getIntereses().isBlank()) {
                String[] parts = p.getIntereses().split(",");
                for (String part : parts) {
                    String t = part.trim();
                    if (!t.isEmpty()) intereses.add(t);
                }
            }
        });
        List<String> list = new ArrayList<>(intereses);
        Collections.sort(list);
        return list;
    }

    public List<MatchDTO> findMatches(Long currentUserId, String carreraFilter, String interesFilter) {
        List<PerfilAcademico> perfiles = perfilAcademicoRepository.findAll();

        PerfilAcademico current = null;
        if (currentUserId != null) {
            current = perfilAcademicoRepository.findByUsuarioId(currentUserId);
        }

        List<MatchDTO> results = new ArrayList<>();

        for (PerfilAcademico p : perfiles) {
            if (p.getUsuario() == null) continue;

            // Filtros de búsqueda
            if (carreraFilter != null && !carreraFilter.isBlank()) {
                if (p.getCarrera() == null || !p.getCarrera().toLowerCase().contains(carreraFilter.toLowerCase())) continue;
            }
            if (interesFilter != null && !interesFilter.isBlank()) {
                String intereses = p.getIntereses() == null ? "" : p.getIntereses();
                if (!intereses.toLowerCase().contains(interesFilter.toLowerCase())) continue;
            }

            double score = current != null ? computeMatchScore(current, p) : computeDefaultScore(p);
            if (score > 100) score = 100;
            if (score < 0) score = 0;

            Usuario u = p.getUsuario();
            String uni = p.getUniversidad() == null ? "" : p.getUniversidad();

            MatchDTO dto = new MatchDTO(u.getId(), u.getNombre(), p.getCarrera(), uni, p.getBiografia(), Math.round(score * 100.0) / 100.0);
            results.add(dto);
        }

        return results.stream()
                .sorted(Comparator.comparingDouble(MatchDTO::getPorcentaje).reversed())
                .limit(50)
                .collect(Collectors.toList());
    }

    private double computeMatchScore(PerfilAcademico current, PerfilAcademico target) {
        if (current == null || target == null) return 0.0;

        double score = 20.0;
        String carreraA = normalize(current.getCarrera());
        String carreraB = normalize(target.getCarrera());
        if (!carreraA.isBlank() && !carreraB.isBlank()) {
            if (carreraA.equals(carreraB)) score += 35;
            else if (careersShareToken(carreraA, carreraB)) score += 12;
            else score -= 10;
        }

        Set<String> interesesA = splitToSet(current.getIntereses());
        Set<String> interesesB = splitToSet(target.getIntereses());
        int sharedIntereses = 0;
        for (String item : interesesA) if (interesesB.contains(item)) sharedIntereses++;
        if (sharedIntereses > 0) score += Math.min(sharedIntereses * 8, 30);
        else score -= 10;

        String horarioA = normalize(current.getHorarioEstudio());
        String horarioB = normalize(target.getHorarioEstudio());
        if (!horarioA.isBlank() && horarioA.equals(horarioB)) score += 10;
        else if (isOppositeSchedule(horarioA, horarioB)) score -= 5;

        String toleranciaA = normalize(current.getNivelTolerancia());
        String toleranciaB = normalize(target.getNivelTolerancia());
        if (!toleranciaA.isBlank() && toleranciaA.equals(toleranciaB)) score += 10;
        else if (isOppositeTolerance(toleranciaA, toleranciaB)) score -= 5;

        String ruidoA = normalize(current.getHabitosRuido());
        String ruidoB = normalize(target.getHabitosRuido());
        if (!ruidoA.isBlank() && ruidoA.equals(ruidoB)) score += 8;
        else if (isOppositeNoiseHabit(ruidoA, ruidoB)) score -= 4;

        String suenoA = normalize(current.getHabitosSueno());
        String suenoB = normalize(target.getHabitosSueno());
        if (!suenoA.isBlank() && suenoA.equals(suenoB)) score += 8;
        else if (isOppositeSleepHabit(suenoA, suenoB)) score -= 5;

        if (!buildEstiloVida(current).isBlank() && buildEstiloVida(current).equals(buildEstiloVida(target))) {
            score += 5;
        }

        score += Math.min(calculateBioOverlap(current.getBiografia(), target.getBiografia()) * 3, 10);
        return score;
    }

    private double computeDefaultScore(PerfilAcademico target) {
        double score = 20.0;
        if (target.getCarrera() != null && !target.getCarrera().isBlank()) score += 25;
        if (target.getIntereses() != null && !target.getIntereses().isBlank()) score += 20;
        if (target.getHorarioEstudio() != null && !target.getHorarioEstudio().isBlank()) score += 10;
        if (target.getHabitosRuido() != null && !target.getHabitosRuido().isBlank()) score += 5;
        return Math.min(score, 100);
    }

    private Set<String> splitToSet(String csv) {
        if (csv == null || csv.isBlank()) return Collections.emptySet();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private boolean careersShareToken(String a, String b) {
        if (a.isBlank() || b.isBlank()) return false;
        Set<String> tokensA = Arrays.stream(a.split("\\s+"))
                .collect(Collectors.toSet());
        Set<String> tokensB = Arrays.stream(b.split("\\s+"))
                .collect(Collectors.toSet());
        for (String token : tokensA) {
            if (tokensB.contains(token)) return true;
        }
        return false;
    }

    private boolean isOppositeSchedule(String a, String b) {
        return (a.contains("mañana") && b.contains("noche")) || (a.contains("noche") && b.contains("mañana"));
    }

    private boolean isOppositeTolerance(String a, String b) {
        return (a.contains("alto") && b.contains("bajo")) || (a.contains("bajo") && b.contains("alto"));
    }

    private boolean isOppositeSleepHabit(String a, String b) {
        return (a.contains("madr") && b.contains("noct")) || (a.contains("noct") && b.contains("madr"));
    }

    private boolean isOppositeNoiseHabit(String a, String b) {
        return (a.contains("silencio") && b.contains("música")) || (a.contains("música") && b.contains("silencio")) ||
                (a.contains("silencio") && b.contains("ruido")) || (a.contains("ruido") && b.contains("silencio"));
    }

    private int calculateBioOverlap(String bioA, String bioB) {
        if (bioA == null || bioB == null) return 0;
        Set<String> keywordsA = Arrays.stream(normalize(bioA).split("[^a-z0-9áéíóúñ]+"))
                .filter(word -> word.length() > 3)
                .collect(Collectors.toSet());
        Set<String> keywordsB = Arrays.stream(normalize(bioB).split("[^a-z0-9áéíóúñ]+"))
                .filter(word -> word.length() > 3)
                .collect(Collectors.toSet());
        int overlap = 0;
        for (String word : keywordsA) {
            if (keywordsB.contains(word)) overlap++;
        }
        return overlap;
    }

    private String buildEstiloVida(PerfilAcademico perfil) {
        if (perfil == null) return "Estilo de vida equilibrado";

        String ruido = normalize(perfil.getHabitosRuido());
        String sueno = normalize(perfil.getHabitosSueno());
        String tolerancia = normalize(perfil.getNivelTolerancia());

        if (sueno.contains("noct")) {
            return "Noctámbulo creativo que encuentra foco en horarios tarde-noche";
        }
        if (sueno.contains("madr")) {
            return "Madrugador estructurado que prefiere ambientes tranquilos";
        }
        if (ruido.contains("música")) {
            return "Disfruta ambientes con música ligera para concentrarse";
        }
        if (ruido.contains("silencio")) {
            return "Prefiere espacios silenciosos y concentración profunda";
        }
        if (tolerancia.contains("alto")) {
            return "Flexible y abierto a distintos estilos de convivencia";
        }
        if (tolerancia.contains("bajo")) {
            return "Busca compañerismo con hábitos muy similares";
        }

        String assembled = String.join(" • ",
                perfil.getHorarioEstudio() == null ? "Horario indefinido" : perfil.getHorarioEstudio(),
                perfil.getHabitosSueno() == null ? "Rutina indefinida" : perfil.getHabitosSueno(),
                perfil.getHabitosRuido() == null ? "Ambiente variable" : perfil.getHabitosRuido());
        return assembled;
    }

    public List<Grupo> findGrupos(String carreraFilter, String interesFilter) {
        if ((carreraFilter == null || carreraFilter.isBlank()) && (interesFilter == null || interesFilter.isBlank())) {
            return grupoRepository.findAll();
        }
        // combinar resultados por carrera e interés
        Set<Grupo> res = new LinkedHashSet<>();
        if (carreraFilter != null && !carreraFilter.isBlank()) {
            res.addAll(grupoRepository.findByCarreraContainingIgnoreCase(carreraFilter));
        }
        if (interesFilter != null && !interesFilter.isBlank()) {
            res.addAll(grupoRepository.findByInteresesContainingIgnoreCase(interesFilter));
        }
        return new ArrayList<>(res);
    }

    /**
     * Obtiene un perfil completo por ID de usuario.
     * Utilizado para cargar perfiles dinámicos en la vista de detalle.
     */
    public PerfilDTO getPerfilByUsuarioId(Long usuarioId) {
        PerfilAcademico perfil = perfilAcademicoRepository.findByUsuarioId(usuarioId);
        if (perfil == null || perfil.getUsuario() == null) return null;
        
        Usuario usuario = perfil.getUsuario();
        String intereses = perfil.getIntereses() == null ? "" : perfil.getIntereses();
        PerfilDTO perfilDTO = new PerfilDTO(
            usuario.getId(),
            usuario.getNombre(),
            perfil.getCarrera(),
            perfil.getUniversidad(),
            perfil.getCiclo(),
            perfil.getBiografia(),
            intereses,
            perfil.getHorarioEstudio(),
            perfil.getHabitosRuido(),
            perfil.getHabitosSueno(),
            perfil.getNivelTolerancia()
        );
        perfilDTO.setEstiloVida(buildEstiloVida(perfil));
        perfilDTO.setAvatar(usuario.getAvatar());
        return perfilDTO;
    }

    /**
     * Calcula compatibilidad entre dos perfiles académicos.
     * Retorna un porcentaje de 0 a 100.
     */
    public Double calcularCompatibilidad(Long usuarioId1, Long usuarioId2) {
        PerfilAcademico p1 = perfilAcademicoRepository.findByUsuarioId(usuarioId1);
        PerfilAcademico p2 = perfilAcademicoRepository.findByUsuarioId(usuarioId2);
        
        if (p1 == null || p2 == null) return 0.0;
        
        return Math.round(computeMatchScore(p1, p2) * 100.0) / 100.0;
    }
}

