package com.scholarstay.app.service;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
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

        // Residentes activos
        long residentes = reservas.stream()
                .filter(r -> "CONFIRMADA".equals(r.getEstado())
                        && !r.getFechaInicio().isAfter(LocalDate.now())
                        && !r.getFechaFin().isBefore(LocalDate.now()))
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

        // Acortar nombres largos para el grafico
        vm.setTopPropiedadesNombres(topPropiedades.stream()
                .map(e -> e.getKey().length() > 25 ? e.getKey().substring(0, 22) + "..." : e.getKey())
                .collect(Collectors.toList()));
        vm.setTopPropiedadesReservas(topPropiedades.stream()
                .map(e -> e.getValue().intValue()).collect(Collectors.toList()));

        // ===== INGRESOS POR MES (ultimos 6 meses) =====
        List<String> meses = new ArrayList<>();
        List<Double> ingresosMes = new ArrayList<>();
        LocalDate hoy = LocalDate.now();

        for (int i = 5; i >= 0; i--) {
            LocalDate mes = hoy.minusMonths(i);
            Month month = mes.getMonth();
            int year = mes.getYear();

            String label = month.getDisplayName(TextStyle.SHORT, new Locale("es", "PE"))
                    + " " + year;
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

        vm.setIngresosTotales(total);
        vm.setIngresosMensuales(mensual);
        vm.setPagosPendientes(pendiente);
        vm.setMetaMensualPorcentaje(Math.round(porcentajeMeta * 10.0) / 10.0);
        vm.setTransacciones(reservas.stream().sorted((a, b) -> b.getId().compareTo(a.getId()))
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
            double calificacion = reservaActiva.getAlojamiento().getCalificacionPromedio() != null
                    ? reservaActiva.getAlojamiento().getCalificacionPromedio() : 0;
            dto.setCompatibilidad((int) Math.round(calificacion * 20));
        } else {
            dto.setPropiedadAsignada("—");
            dto.setEstadoReserva("SIN_RESERVA");
            dto.setCompatibilidad(0);
        }
        return dto;
    }
}