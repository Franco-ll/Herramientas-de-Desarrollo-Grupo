package com.scholarstay.app.viewmodel;

import java.util.List;

import com.scholarstay.app.dto.ReservaDTO;
import com.scholarstay.app.dto.UsuarioDTO;

public class AdminDashboardVM {
    private double ocupacionTotal;
    private double ingresosMensuales;
    private int residentesActivos;
    private double crecimientoOcupacion;
    private List<ReservaDTO> transaccionesRecientes;
    private List<UsuarioDTO> nuevosResidentes;

    // NUEVO: propiedades más reservadas
    private List<String> topPropiedadesNombres;
    private List<Integer> topPropiedadesReservas;

    // NUEVO: ingresos por mes (últimos 6 meses)
    private List<String> mesesLabels;
    private List<Double> ingresosPorMes;

    public AdminDashboardVM() {}

    public double getOcupacionTotal() { return ocupacionTotal; }
    public void setOcupacionTotal(double ocupacionTotal) { this.ocupacionTotal = ocupacionTotal; }

    public double getIngresosMensuales() { return ingresosMensuales; }
    public void setIngresosMensuales(double ingresosMensuales) { this.ingresosMensuales = ingresosMensuales; }

    public int getResidentesActivos() { return residentesActivos; }
    public void setResidentesActivos(int residentesActivos) { this.residentesActivos = residentesActivos; }

    public double getCrecimientoOcupacion() { return crecimientoOcupacion; }
    public void setCrecimientoOcupacion(double crecimientoOcupacion) { this.crecimientoOcupacion = crecimientoOcupacion; }

    public List<ReservaDTO> getTransaccionesRecientes() { return transaccionesRecientes; }
    public void setTransaccionesRecientes(List<ReservaDTO> transaccionesRecientes) { this.transaccionesRecientes = transaccionesRecientes; }

    public List<UsuarioDTO> getNuevosResidentes() { return nuevosResidentes; }
    public void setNuevosResidentes(List<UsuarioDTO> nuevosResidentes) { this.nuevosResidentes = nuevosResidentes; }

    public List<String> getTopPropiedadesNombres() { return topPropiedadesNombres; }
    public void setTopPropiedadesNombres(List<String> topPropiedadesNombres) { this.topPropiedadesNombres = topPropiedadesNombres; }

    public List<Integer> getTopPropiedadesReservas() { return topPropiedadesReservas; }
    public void setTopPropiedadesReservas(List<Integer> topPropiedadesReservas) { this.topPropiedadesReservas = topPropiedadesReservas; }

    public List<String> getMesesLabels() { return mesesLabels; }
    public void setMesesLabels(List<String> mesesLabels) { this.mesesLabels = mesesLabels; }

    public List<Double> getIngresosPorMes() { return ingresosPorMes; }
    public void setIngresosPorMes(List<Double> ingresosPorMes) { this.ingresosPorMes = ingresosPorMes; }
}