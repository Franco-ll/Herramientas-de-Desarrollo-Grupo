package com.scholarstay.app.service;

import org.springframework.stereotype.Service;

import com.scholarstay.app.model.ConfiguracionSistema;
import com.scholarstay.app.repository.ConfiguracionSistemaRepository;

@Service
public class ConfiguracionService {

    private final ConfiguracionSistemaRepository configuracionRepository;

    public ConfiguracionService(ConfiguracionSistemaRepository configuracionRepository) {
        this.configuracionRepository = configuracionRepository;
    }

    /**
     * Obtiene la configuración del sistema. Si no existe, crea una por defecto.
     */
    public ConfiguracionSistema obtener() {
        return configuracionRepository.findAll().stream().findFirst()
                .orElseGet(() -> {
                    ConfiguracionSistema config = new ConfiguracionSistema();
                    config.setNombrePlataforma("Scholar Stay");
                    config.setPrecioMinimo(100.0);
                    config.setPrecioMaximo(5000.0);
                    config.setMaxReservasPorUsuario(3);
                    return configuracionRepository.save(config);
                });
    }

    /**
     * Guarda los cambios de configuración del sistema.
     */
    public ConfiguracionSistema guardarConfiguracion(String nombrePlataforma,
                                                      Double precioMinimo,
                                                      Double precioMaximo,
                                                      Integer maxReservas) {
        ConfiguracionSistema config = obtener();
        config.setNombrePlataforma(nombrePlataforma);
        config.setPrecioMinimo(precioMinimo);
        config.setPrecioMaximo(precioMaximo);
        config.setMaxReservasPorUsuario(maxReservas);
        return configuracionRepository.save(config);
    }
}