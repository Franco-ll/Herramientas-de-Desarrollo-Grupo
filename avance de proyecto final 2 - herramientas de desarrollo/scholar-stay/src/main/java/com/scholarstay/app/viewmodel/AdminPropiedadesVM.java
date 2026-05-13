package com.scholarstay.app.viewmodel;

import com.scholarstay.app.model.Alojamiento;
import java.util.List;

public class AdminPropiedadesVM {
    private int totalPropiedades;
    private int totalHabitaciones;
    private double calificacionPromedioTotal;
    private List<Alojamiento> propiedades;

    public AdminPropiedadesVM() {}

    public int getTotalPropiedades() { return totalPropiedades; }
    public void setTotalPropiedades(int totalPropiedades) { this.totalPropiedades = totalPropiedades; }

    public int getTotalHabitaciones() { return totalHabitaciones; }
    public void setTotalHabitaciones(int totalHabitaciones) { this.totalHabitaciones = totalHabitaciones; }

    public List<Alojamiento> getPropiedades() { return propiedades; }
    public void setPropiedades(List<Alojamiento> propiedades) { this.propiedades = propiedades; }

    public double getCalificacionPromedioTotal() { return calificacionPromedioTotal; }
    public void setCalificacionPromedioTotal(double calificacionPromedioTotal) { this.calificacionPromedioTotal = calificacionPromedioTotal; }
}
