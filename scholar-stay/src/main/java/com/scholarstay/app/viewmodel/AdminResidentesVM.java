package com.scholarstay.app.viewmodel;

import com.scholarstay.app.dto.UsuarioDTO;
import java.util.List;

public class AdminResidentesVM {
    private int totalResidentes;
    private int nuevosEsteMes;
    private List<UsuarioDTO> residentes;
    private List<String> carrerasDisponibles;
    private List<String> propiedadesDisponibles;

    public AdminResidentesVM() {}

    public int getTotalResidentes() { return totalResidentes; }
    public void setTotalResidentes(int totalResidentes) { this.totalResidentes = totalResidentes; }

    public int getNuevosEsteMes() { return nuevosEsteMes; }
    public void setNuevosEsteMes(int nuevosEsteMes) { this.nuevosEsteMes = nuevosEsteMes; }

    public List<UsuarioDTO> getResidentes() { return residentes; }
    public void setResidentes(List<UsuarioDTO> residentes) { this.residentes = residentes; }

    public List<String> getCarrerasDisponibles() { return carrerasDisponibles; }
    public void setCarrerasDisponibles(List<String> carrerasDisponibles) { this.carrerasDisponibles = carrerasDisponibles; }

    public List<String> getPropiedadesDisponibles() { return propiedadesDisponibles; }
    public void setPropiedadesDisponibles(List<String> propiedadesDisponibles) { this.propiedadesDisponibles = propiedadesDisponibles; }
}
