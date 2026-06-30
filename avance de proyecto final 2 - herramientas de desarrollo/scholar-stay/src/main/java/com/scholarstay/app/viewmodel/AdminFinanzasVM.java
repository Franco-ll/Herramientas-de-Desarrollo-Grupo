package com.scholarstay.app.viewmodel;

import java.util.List;

import com.scholarstay.app.dto.ReservaDTO;

public class AdminFinanzasVM {
    private double ingresosTotales;
    private double ingresosMensuales;
    private double pagosPendientes;
    private int totalReservas;
    private double metaMensualPorcentaje;
    private List<ReservaDTO> transacciones;
    private List<String> mesesLabels;
    private List<Double> ingresosPorMes;
    private String mesActualLabel;

    public AdminFinanzasVM() {}

    public double getIngresosTotales() { return ingresosTotales; }
    public void setIngresosTotales(double ingresosTotales) { this.ingresosTotales = ingresosTotales; }

    public double getIngresosMensuales() { return ingresosMensuales; }
    public void setIngresosMensuales(double ingresosMensuales) { this.ingresosMensuales = ingresosMensuales; }

    public double getPagosPendientes() { return pagosPendientes; }
    public void setPagosPendientes(double pagosPendientes) { this.pagosPendientes = pagosPendientes; }

    public int getTotalReservas() { return totalReservas; }
    public void setTotalReservas(int totalReservas) { this.totalReservas = totalReservas; }

    public double getMetaMensualPorcentaje() { return metaMensualPorcentaje; }
    public void setMetaMensualPorcentaje(double metaMensualPorcentaje) { this.metaMensualPorcentaje = metaMensualPorcentaje; }

    public List<ReservaDTO> getTransacciones() { return transacciones; }
    public void setTransacciones(List<ReservaDTO> transacciones) { this.transacciones = transacciones; }

    public List<String> getMesesLabels() { return mesesLabels; }
    public void setMesesLabels(List<String> mesesLabels) { this.mesesLabels = mesesLabels; }

    public List<Double> getIngresosPorMes() { return ingresosPorMes; }
    public void setIngresosPorMes(List<Double> ingresosPorMes) { this.ingresosPorMes = ingresosPorMes; }

    public String getMesActualLabel() { return mesActualLabel; }
    public void setMesActualLabel(String mesActualLabel) { this.mesActualLabel = mesActualLabel; }
}