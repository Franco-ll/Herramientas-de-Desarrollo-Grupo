package com.scholarstay.app.viewmodel;

import com.scholarstay.app.dto.ReservaDTO;
import java.util.List;

public class AdminFinanzasVM {
    private double ingresosTotales;
    private double ingresosMensuales;
    private double pagosPendientes;
    private double metaMensualPorcentaje;
    private List<ReservaDTO> transacciones;

    public AdminFinanzasVM() {}

    public double getIngresosTotales() { return ingresosTotales; }
    public void setIngresosTotales(double ingresosTotales) { this.ingresosTotales = ingresosTotales; }

    public double getIngresosMensuales() { return ingresosMensuales; }
    public void setIngresosMensuales(double ingresosMensuales) { this.ingresosMensuales = ingresosMensuales; }

    public double getPagosPendientes() { return pagosPendientes; }
    public void setPagosPendientes(double pagosPendientes) { this.pagosPendientes = pagosPendientes; }

    public double getMetaMensualPorcentaje() { return metaMensualPorcentaje; }
    public void setMetaMensualPorcentaje(double metaMensualPorcentaje) { this.metaMensualPorcentaje = metaMensualPorcentaje; }

    public List<ReservaDTO> getTransacciones() { return transacciones; }
    public void setTransacciones(List<ReservaDTO> transacciones) { this.transacciones = transacciones; }
}
