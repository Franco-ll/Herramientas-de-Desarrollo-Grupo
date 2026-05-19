package com.scholarstay.app.viewmodel;

import com.scholarstay.app.dto.ReservaDTO;
import com.scholarstay.app.dto.UsuarioDTO;
import java.util.List;

public class AdminDashboardVM {
    private double ocupacionTotal;
    private double ingresosMensuales;
    private int residentesActivos;
    private double crecimientoOcupacion;
    private List<ReservaDTO> transaccionesRecientes;
    private List<UsuarioDTO> nuevosResidentes;

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
}
