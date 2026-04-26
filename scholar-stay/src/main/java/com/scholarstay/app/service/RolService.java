package com.scholarstay.app.service;

import com.scholarstay.app.model.Rol;
import com.scholarstay.app.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolService {

    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    public List<Rol> listar() {
        return rolRepository.findAll();
    }

    public Rol crear(Rol rol) {
        return rolRepository.save(rol);
    }

    public Rol findOrCreateByName(String nombre) {
        Rol rol = rolRepository.findByNombre(nombre);
        if (rol == null) {
            rol = new Rol(nombre);
            rol = rolRepository.save(rol);
        }
        return rol;
    }
}
