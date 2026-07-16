package com.scholarstay.app.service;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scholarstay.app.dto.ReservaDTO;
import com.scholarstay.app.dto.UsuarioDTO;
import com.scholarstay.app.model.Alojamiento;
import com.scholarstay.app.model.Reserva;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.repository.AlojamientoRepository;
import com.scholarstay.app.repository.ReservaRepository;
import com.scholarstay.app.repository.UsuarioRepository;
import com.scholarstay.app.viewmodel.AdminDashboardVM;
import com.scholarstay.app.viewmodel.AdminFinanzasVM;
import com.scholarstay.app.viewmodel.AdminPropiedadesVM;
import com.scholarstay.app.viewmodel.AdminResidentesVM;

@Service
public class AdminDashboardService {

    private final AlojamientoRepository alojamientoRepository;
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;

    public AdminDashboardService(AlojamientoRepository alojamientoRepository,
                                 ReservaRepository reservaRepository,
                                 UsuarioRepository usuarioRepository) {
        this.alojamientoRepository = alojamientoRepository;
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public AdminDashboardVM getDashboardStats() {
        AdminDashboardVM vm = new AdminDashboardVM();

        List<Alojamiento> alojamientos = alojamientoRepository.findAll();
        List<Reserva> reservas = reservaRepository.findAll();
        List<Usuario> usuarios = usuarioRepository.findAll();

        // Ocupacion
        int totalHabitaciones = alojamientos.stream().mapToInt(a -> a.getHabitaciones() != null ? a.getHabitaciones() : 0).sum();
        long reservasActivas = reservas.stream()
                .filter(r -> "CONFIRMADA".equals(r.getEstado()) &&
                        !r.getFechaInicio().isAfter(LocalDate.now()) &&
                        !r.getFechaFin().isBefore(LocalDate.now()))
                .count();
        double ocupacion = totalHabitaciones > 0 ? (double) reservasActivas / totalHabitaciones * 100 : 0;
        vm.setOcupacionTotal(Math.round(ocupacion * 10.0) / 10.0);
        vm.setCrecimientoOcupacion(2.4);

        // Ingresos mensuales
        double ingresos = reservas.stream()
                .filter(r -> "CONFIRMADA".equals(r.getEstado()) &&
                        r.getFechaInicio().getMonth() == LocalDate.now().getMonth() &&
                        r.getFechaInicio().getYear() == LocalDate.now().getYear())
                .mapToDouble(Reserva::getPrecioTotal).sum();
        vm.setIngresosMensuales(ingresos);

        // Residentes activos - todos los que tienen al menos una reserva confirmada
        long residentes = reservas.stream()
                .filter(r -> "CONFIRMADA".equals(r.getEstado()))
                .map(r -> r.getUsuario().getId()).distinct().count();
        vm.setResidentesActivos((int) residentes);

        // Transacciones recientes
        vm.setTransaccionesRecientes(reservas.stream()
                .sorted((a, b) -> b.getId().compareTo(a.getId()))
                .limit(5).map(this::mapToReservaDTO).collect(Collectors.toList()));

        // Nuevos residentes
        List<Long> idsConReserva = reservas.stream()
                .sorted((a, b) -> b.getId().compareTo(a.getId()))
                .map(r -> r.getUsuario().getId()).distinct().limit(5).collect(Collectors.toList());
        List<UsuarioDTO> nuevosResidentes = idsConReserva.stream()
                .map(uid -> usuarios.stream().filter(u -> u.getId().equals(uid)).findFirst().orElse(null))
                .filter(u -> u != null)
                .map(u -> mapToUsuarioDTOConReserva(u, reservas)).collect(Collectors.toList());
        vm.setNuevosResidentes(nuevosResidentes);

        // ===== TOP 5 PROPIEDADES MAS RESERVADAS =====
        Map<String, Long> reservasPorPropiedad = reservas.stream()
                .filter(r -> "CONFIRMADA".equals(r.getEstado()))
                .collect(Collectors.groupingBy(r -> r.getAlojamiento().getTitulo(), Collectors.counting()));

        List<Map.Entry<String, Long>> topPropiedades = reservasPorPropiedad.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5).collect(Collectors.toList());

        vm.setTopPropiedadesNombres(topPropiedades.stream()
                .map(e -> e.getKey().length() > 25 ? e.getKey().substring(0, 22) + "..." : e.getKey())
                .collect(Collectors.toList()));
        vm.setTopPropiedadesReservas(topPropiedades.stream()
                .map(e -> e.getValue().intValue()).collect(Collectors.toList()));

        // ===== INGRESOS POR MES (ultimos 6 meses desde la reserva mas reciente) =====
        List<String> meses = new ArrayList<>();
        List<Double> ingresosMes = new ArrayList<>();
        LocalDate fechaReferencia = reservas.stream()
                .map(Reserva::getFechaInicio)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());
        if (fechaReferencia.isBefore(LocalDate.now())) {
            fechaReferencia = LocalDate.now();
        }

        for (int i = 5; i >= 0; i--) {
            LocalDate mes = fechaReferencia.minusMonths(i);
            Month month = mes.getMonth();
            int year = mes.getYear();

            String label = month.getDisplayName(TextStyle.SHORT, new Locale("es", "PE")) + " " + year;
            meses.add(label);

            double total = reservas.stream()
                    .filter(r -> "CONFIRMADA".equals(r.getEstado()))
                    .filter(r -> r.getFechaInicio().getMonth() == month &&
                            r.getFechaInicio().getYear() == year)
                    .mapToDouble(Reserva::getPrecioTotal).sum();
            ingresosMes.add(Math.round(total * 100.0) / 100.0);
        }

        vm.setMesesLabels(meses);
        vm.setIngresosPorMes(ingresosMes);

        return vm;
    }

    public AdminFinanzasVM getFinanzasStats() {
        AdminFinanzasVM vm = new AdminFinanzasVM();
        List<Reserva> reservas = reservaRepository.findAll();

        double total = reservas.stream().filter(r -> "CONFIRMADA".equals(r.getEstado()))
                .mapToDouble(Reserva::getPrecioTotal).sum();
        double mensual = reservas.stream()
                .filter(r -> "CONFIRMADA".equals(r.getEstado())
                        && r.getFechaInicio().getMonth() == LocalDate.now().getMonth()
                        && r.getFechaInicio().getYear() == LocalDate.now().getYear())
                .mapToDouble(Reserva::getPrecioTotal).sum();
        double pendiente = reservas.stream().filter(r -> "PENDIENTE".equals(r.getEstado()))
                .mapToDouble(Reserva::getPrecioTotal).sum();

        double metaFija = 5000.0;
        double porcentajeMeta = metaFija > 0 ? Math.min((mensual / metaFija) * 100, 100) : 0;

        int totalReservas = (int) reservas.stream()
                .filter(r -> "CONFIRMADA".equals(r.getEstado())).count();

        // Ingresos por mes para el grafico de finanzas (ultimos 6 meses desde la reserva mas reciente)
        List<String> meses = new ArrayList<>();
        List<Double> ingresosMes = new ArrayList<>();
        LocalDate fechaReferenciaFin = reservas.stream()
                .map(Reserva::getFechaInicio)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());
        if (fechaReferenciaFin.isBefore(LocalDate.now())) {
            fechaReferenciaFin = LocalDate.now();
        }
        for (int i = 5; i >= 0; i--) {
            LocalDate mes = fechaReferenciaFin.minusMonths(i);
            Month month = mes.getMonth();
            int year = mes.getYear();
            String label = month.getDisplayName(TextStyle.SHORT, new Locale("es", "PE")) + " " + year;
            meses.add(label);
            double totalMes = reservas.stream()
                    .filter(r -> "CONFIRMADA".equals(r.getEstado()))
                    .filter(r -> r.getFechaInicio().getMonth() == month && r.getFechaInicio().getYear() == year)
                    .mapToDouble(Reserva::getPrecioTotal).sum();
            ingresosMes.add(Math.round(totalMes * 100.0) / 100.0);
        }

        vm.setIngresosTotales(total);
        vm.setIngresosMensuales(mensual);
        vm.setPagosPendientes(pendiente);
        vm.setTotalReservas(totalReservas);
        vm.setMesesLabels(meses);
        vm.setIngresosPorMes(ingresosMes);
        vm.setMesActualLabel(LocalDate.now().getMonth().getDisplayName(TextStyle.SHORT, new Locale("es", "PE"))
                + " " + LocalDate.now().getYear());
        vm.setMetaMensualPorcentaje(Math.round(porcentajeMeta * 10.0) / 10.0);
        vm.setTransacciones(reservas.stream()
                .sorted((a, b) -> {
                    long durA = java.time.temporal.ChronoUnit.DAYS.between(a.getFechaInicio(), a.getFechaFin());
                    long durB = java.time.temporal.ChronoUnit.DAYS.between(b.getFechaInicio(), b.getFechaFin());
                    return Long.compare(durB, durA);
                })
                .map(this::mapToReservaDTO).collect(Collectors.toList()));

        return vm;
    }

    public AdminResidentesVM getResidentesStats() {
        AdminResidentesVM vm = new AdminResidentesVM();
        List<Reserva> reservas = reservaRepository.findAll();
        List<Usuario> usuarios = usuarioRepository.findAll();

        List<Long> idsResidentes = reservas.stream().map(r -> r.getUsuario().getId())
                .distinct().collect(Collectors.toList());
        List<Usuario> residentes = usuarios.stream()
                .filter(u -> idsResidentes.contains(u.getId())).collect(Collectors.toList());

        long nuevosEsteMes = reservas.stream()
                .filter(r -> r.getFechaInicio().getMonth() == LocalDate.now().getMonth()
                        && r.getFechaInicio().getYear() == LocalDate.now().getYear())
                .map(r -> r.getUsuario().getId()).distinct().count();

        List<String> carreras = residentes.stream()
                .filter(u -> u.getPerfilAcademico() != null && u.getPerfilAcademico().getCarrera() != null)
                .map(u -> u.getPerfilAcademico().getCarrera()).distinct().sorted().collect(Collectors.toList());

        List<String> propiedades = reservas.stream()
                .map(r -> r.getAlojamiento().getTitulo()).distinct().sorted().collect(Collectors.toList());

        vm.setTotalResidentes(residentes.size());
        vm.setNuevosEsteMes((int) nuevosEsteMes);
        vm.setCarrerasDisponibles(carreras);
        vm.setPropiedadesDisponibles(propiedades);
        vm.setResidentes(residentes.stream().map(u -> mapToUsuarioDTOConReserva(u, reservas))
                .collect(Collectors.toList()));

        return vm;
    }

    public List<ReservaDTO> getReservasDeUsuario(Long usuarioId) {
        return reservaRepository.findAll().stream()
                .filter(r -> r.getUsuario().getId().equals(usuarioId))
                .sorted((a, b) -> b.getId().compareTo(a.getId()))
                .map(this::mapToReservaDTO)
                .collect(Collectors.toList());
    }

    public AdminPropiedadesVM getPropiedadesStats() {
        AdminPropiedadesVM vm = new AdminPropiedadesVM();
        List<Alojamiento> alojamientos = alojamientoRepository.findAll();

        vm.setTotalPropiedades(alojamientos.size());
        vm.setTotalHabitaciones(alojamientos.stream()
                .mapToInt(a -> a.getHabitaciones() != null ? a.getHabitaciones() : 0).sum());

        double promedioCalif = alojamientos.stream()
                .filter(a -> a.getCalificacionPromedio() != null)
                .mapToDouble(Alojamiento::getCalificacionPromedio).average().orElse(0.0);
        vm.setCalificacionPromedioTotal(Math.round(promedioCalif * 10.0) / 10.0);
        vm.setPropiedades(alojamientos);

        return vm;
    }

    private ReservaDTO mapToReservaDTO(Reserva r) {
        ReservaDTO dto = new ReservaDTO();
        dto.setId(r.getId());
        dto.setTituloAlojamiento(r.getAlojamiento().getTitulo());
        dto.setFechaInicio(r.getFechaInicio());
        dto.setFechaFin(r.getFechaFin());
        dto.setEstado(r.getEstado());
        dto.setMonto(r.getPrecioTotal());
        dto.setNombreUsuario(r.getUsuario().getNombre());
        dto.setCapacidadEstudiantes(r.getAlojamiento().getCapacidadEstudiantes());
        return dto;
    }

    private UsuarioDTO mapToUsuarioDTO(Usuario u) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(u.getId());
        dto.setNombre(u.getNombre());
        dto.setEmail(u.getEmail());
        dto.setRol(u.getRol() != null ? u.getRol().getNombre() : "—");
        if (u.getPerfilAcademico() != null) dto.setCarrera(u.getPerfilAcademico().getCarrera());
        return dto;
    }

    private UsuarioDTO mapToUsuarioDTOConReserva(Usuario u, List<Reserva> todasLasReservas) {
        UsuarioDTO dto = mapToUsuarioDTO(u);
        Reserva reservaActiva = todasLasReservas.stream()
                .filter(r -> r.getUsuario().getId().equals(u.getId()))
                .filter(r -> "CONFIRMADA".equals(r.getEstado()))
                .filter(r -> !r.getFechaInicio().isAfter(LocalDate.now()) && !r.getFechaFin().isBefore(LocalDate.now()))
                .findFirst().orElse(null);

        if (reservaActiva == null) {
            reservaActiva = todasLasReservas.stream()
                    .filter(r -> r.getUsuario().getId().equals(u.getId()))
                    .max((a, b) -> a.getId().compareTo(b.getId())).orElse(null);
        }

        if (reservaActiva != null) {
            dto.setPropiedadAsignada(reservaActiva.getAlojamiento().getTitulo());
            dto.setEstadoReserva(reservaActiva.getEstado());
        } else {
            dto.setPropiedadAsignada("—");
            dto.setEstadoReserva("SIN_RESERVA");
        }
        return dto;
    }

    public List<Map<String, Object>> getEventosCalendario() {
        // Colores por propiedad (rotando entre una paleta)
        String[] colores = {
            "#3d637e", "#46674b", "#6a5e46", "#7b4f71", "#4a6e8a",
            "#2e7d5a", "#8a5c3e", "#5a4a7a", "#3d7e6e", "#7e5a3d"
        };

        List<Reserva> reservas = reservaRepository.findAll().stream()
                .filter(r -> "CONFIRMADA".equals(r.getEstado()))
                .collect(Collectors.toList());

        // Asignar un color único por propiedad
        Map<Long, String> colorPorPropiedad = new HashMap<>();
        int[] colorIdx = {0};
        reservas.forEach(r -> {
            Long propId = r.getAlojamiento().getId();
            if (!colorPorPropiedad.containsKey(propId)) {
                colorPorPropiedad.put(propId, colores[colorIdx[0] % colores.length]);
                colorIdx[0]++;
            }
        });

        return reservas.stream().map(r -> {
            Map<String, Object> evento = new HashMap<>();
            String titulo = r.getAlojamiento().getTitulo();
            String residente = r.getUsuario().getNombre();
            long dias = java.time.temporal.ChronoUnit.DAYS.between(r.getFechaInicio(), r.getFechaFin());
            long meses = java.time.temporal.ChronoUnit.MONTHS.between(r.getFechaInicio(), r.getFechaFin());
            String duracion = meses >= 1 ? meses + (meses == 1 ? " mes" : " meses") : dias + " días";
            String tituloCorto = titulo.length() > 18 ? titulo.substring(0, 18) + "..." : titulo;
            evento.put("title", residente + " · " + tituloCorto + " (" + duracion + ")");
            evento.put("start", r.getFechaInicio().toString());
            evento.put("color", colorPorPropiedad.get(r.getAlojamiento().getId()));
            evento.put("extendedProps", Map.of(
                "residente", residente,
                "propiedad", r.getAlojamiento().getTitulo(),
                "monto", r.getPrecioTotal(),
                "estado", r.getEstado(),
                "fechaInicio", r.getFechaInicio().toString(),
                "fechaFin", r.getFechaFin().toString(),
                "duracion", duracion
            ));
            return evento;
        }).collect(Collectors.toList());
    }
}