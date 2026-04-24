package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("ocupacion", "94.2%");
        model.addAttribute("ingresos", "S/ 42,850");
        model.addAttribute("residentesActivos", 128);

        return "admin/dashboard";
    }

    @GetMapping("/admin/residentes")
    public String residentes(Model model) {
        List<Map<String, Object>> lista = List.of(
                Map.of("nombre", "Mateo Fernández", "codigo", "SS-2024-001", "carrera", "Ingeniería de Software", "estado", "Activo", "compatibilidad", 94),
                Map.of("nombre", "Sofía Ramírez", "codigo", "SS-2024-002", "carrera", "Arquitectura", "estado", "Activo", "compatibilidad", 78),
                Map.of("nombre", "Javier Ortiz", "codigo", "SS-2023-045", "carrera", "Medicina", "estado", "Inactivo", "compatibilidad", 52),
                Map.of("nombre", "Elena Vargas", "codigo", "SS-2024-009", "carrera", "Bellas Artes", "estado", "Activo", "compatibilidad", 88)
        );

        model.addAttribute("residentes", lista);
        return "admin/residentes";
    }

    @GetMapping("/admin/propiedades")
    public String propiedades(Model model) {
        List<Map<String, Object>> lista = List.of(
                Map.of("nombre", "Residencia El Greco", "tipo", "Loft Premium", "ubicacion", "Distrito Universitario", "precio", 850, "estado", "Disponible"),
                Map.of("nombre", "Apartamento Cervantes", "tipo", "Habitación Individual", "ubicacion", "Centro Histórico", "precio", 420, "estado", "Ocupado"),
                Map.of("nombre", "Residencia Nobel", "tipo", "Suite Ejecutiva", "ubicacion", "Barrio Norte", "precio", 1100, "estado", "Disponible")
        );

        model.addAttribute("propiedades", lista);
        return "admin/propiedades";
    }

    @GetMapping("/admin/finanzas")
    public String finanzas(Model model) {

        model.addAttribute("ingresosTotales", "S/ 245,800.00");
        model.addAttribute("ingresosMensuales", "S/ 32,450.00");
        model.addAttribute("pagosPendientes", "S/ 4,120.00");

        List<Map<String, Object>> transacciones = List.of(
                Map.of("fecha", "12 May, 2024", "hora", "14:30 PM", "usuario", "Lucas Contreras", "habitacion", "Habitación 402", "monto", "S/ 1,200.00", "estado", "Pagado"),
                Map.of("fecha", "10 May, 2024", "hora", "09:15 AM", "usuario", "Elena Martínez", "habitacion", "Habitación 105", "monto", "S/ 950.00", "estado", "Pendiente"),
                Map.of("fecha", "08 May, 2024", "hora", "18:45 PM", "usuario", "Santiago Vaca", "habitacion", "Habitación 220", "monto", "S/ 1,450.00", "estado", "Pagado"),
                Map.of("fecha", "05 May, 2024", "hora", "11:00 AM", "usuario", "Sofía Herrera", "habitacion", "Habitación 312", "monto", "S/ 1,100.00", "estado", "Pendiente")
        );

        model.addAttribute("transacciones", transacciones);

        return "admin/finanzas";
    }

    @GetMapping("/admin/configuracion")
    public String configuracion() {
        return "admin/configuracion";
    }
}
