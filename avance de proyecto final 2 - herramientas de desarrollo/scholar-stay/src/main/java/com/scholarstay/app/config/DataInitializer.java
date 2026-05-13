package com.scholarstay.app.config;

import com.scholarstay.app.model.Alojamiento;
import com.scholarstay.app.model.Rol;
import com.scholarstay.app.model.Usuario;
import com.scholarstay.app.repository.AlojamientoRepository;
import com.scholarstay.app.repository.RolRepository;
import com.scholarstay.app.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(AlojamientoRepository repository,
                               RolRepository rolRepository,
                               UsuarioRepository usuarioRepository) {
        return args -> {
            // --- Seed Roles ---
            Rol rolAdmin = rolRepository.findByNombre("ROLE_ADMIN");
            if (rolAdmin == null) {
                rolAdmin = rolRepository.save(new Rol("ROLE_ADMIN"));
            }
            Rol rolEstudiante = rolRepository.findByNombre("ROLE_ESTUDIANTE");
            if (rolEstudiante == null) {
                rolEstudiante = rolRepository.save(new Rol("ROLE_ESTUDIANTE"));
            }

            // --- Seed Admin User ---
            if (usuarioRepository.findByEmail("admin@scholarstay.com").isEmpty()) {
                Usuario admin = new Usuario("Administrador", "admin@scholarstay.com", "admin123");
                admin.setRol(rolAdmin);
                usuarioRepository.save(admin);
            }

            // --- Seed Alojamientos ---
            if (repository.count() == 0) {
                Alojamiento a1 = new Alojamiento();
                a1.setTitulo("Loft Moderno Cerca de la Universidad");
                a1.setDescripcion("Estudio totalmente equipado con luz natural y área de estudio dedicada. Ideal para estudiantes que buscan independencia y comodidad.");
                a1.setPrecioMensual(850.0);
                a1.setUbicacion("Zona Universitaria, Piura");
                a1.setImagen("alojamiento1.png");
                a1.setImagenes(Arrays.asList("alojamiento1.png", "alojamiento2.png", "alojamiento3.png"));
                a1.setHabitaciones(1);
                a1.setBanos(1);
                a1.setCalificacionPromedio(4.8);
                a1.setServicios(Arrays.asList("Wifi Alta Velocidad", "AC Central", "Escritorio Ergonómico", "Cocina Equipada"));
                a1.setReglas(Arrays.asList("No fumar", "Silencio después de las 10 PM", "No mascotas"));

                Alojamiento a2 = new Alojamiento();
                a2.setTitulo("Habitación en Residencia Premium");
                a2.setDescripcion("Habitación compartida con todas las comodidades y servicios incluidos. Ambiente multicultural y vibrante.");
                a2.setPrecioMensual(450.0);
                a2.setUbicacion("Centro Histórico, Piura");
                a2.setImagen("alojamiento2.png");
                a2.setImagenes(Arrays.asList("alojamiento2.png", "alojamiento4.png", "alojamiento5.png"));
                a2.setHabitaciones(2);
                a2.setBanos(1);
                a2.setCalificacionPromedio(4.9);
                a2.setServicios(Arrays.asList("Limpieza Semanal", "Gimnasio", "Zona de Co-working", "Lavandería"));
                a2.setReglas(Arrays.asList("Visitas hasta las 9 PM", "Respetar turnos de limpieza", "No ruidos fuertes"));

                Alojamiento a3 = new Alojamiento();
                a3.setTitulo("Apartamento Amplio para Estudiantes");
                a3.setDescripcion("Sala de estar espaciosa y cocina moderna. Perfecto para grupos de amigos que quieren compartir gastos y experiencias.");
                a3.setPrecioMensual(1200.0);
                a3.setUbicacion("Urbanización Santa Isabel, Piura");
                a3.setImagen("alojamiento3.png");
                a3.setImagenes(Arrays.asList("alojamiento3.png", "alojamiento1.png", "alojamiento4.png"));
                a3.setHabitaciones(3);
                a3.setBanos(2);
                a3.setCalificacionPromedio(4.7);
                a3.setServicios(Arrays.asList("Estacionamiento", "Balcón", "Seguridad 24/7", "TV por cable"));
                a3.setReglas(Arrays.asList("No fiestas grandes", "Mantener áreas comunes ordenadas", "Cuidado con los muebles"));

                Alojamiento a4 = new Alojamiento();
                a4.setTitulo("Estudio Privado con Vista a la Ciudad");
                a4.setDescripcion("Compacto pero funcional, diseñado específicamente para el enfoque académico. Vista inspiradora.");
                a4.setPrecioMensual(700.0);
                a4.setUbicacion("Miraflores, Piura");
                a4.setImagen("alojamiento4.png");
                a4.setImagenes(Arrays.asList("alojamiento4.png", "alojamiento5.png", "alojamiento2.png"));
                a4.setHabitaciones(1);
                a4.setBanos(1);
                a4.setCalificacionPromedio(4.5);
                a4.setServicios(Arrays.asList("Fibra Óptica", "Escritorio", "Luz Natural", "Microondas"));
                a4.setReglas(Arrays.asList("Solo una persona", "No se permite fumar", "Pago puntual"));

                Alojamiento a5 = new Alojamiento();
                a5.setTitulo("Suite Ejecutiva para Investigadores");
                a5.setDescripcion("Ambiente tranquilo y social en áreas comunes. Servicios premium para quienes necesitan concentración máxima.");
                a5.setPrecioMensual(950.0);
                a5.setUbicacion("Castilla, Piura");
                a5.setImagen("alojamiento5.png");
                a5.setImagenes(Arrays.asList("alojamiento5.png", "alojamiento3.png", "alojamiento1.png"));
                a5.setHabitaciones(1);
                a5.setBanos(1);
                a5.setCalificacionPromedio(4.9);
                a5.setServicios(Arrays.asList("Desayuno Incluido", "Limpieza diaria", "Biblioteca", "Jardín"));
                a5.setReglas(Arrays.asList("Silencio total en pasillos", "Uso responsable de biblioteca", "Cuidado de áreas verdes"));

                Alojamiento a6 = new Alojamiento();
                a6.setTitulo("Residencia Estudiantil Central");
                a6.setDescripcion("Habitación amplia con todos los servicios en el corazón de la ciudad. Cerca de todo lo que necesitas.");
                a6.setPrecioMensual(550.0);
                a6.setUbicacion("Centro de Piura");
                a6.setImagen("alojamiento6.png");
                a6.setImagenes(Arrays.asList("alojamiento6.png", "alojamiento2.png", "alojamiento1.png"));
                a6.setHabitaciones(1);
                a6.setBanos(1);
                a6.setCalificacionPromedio(4.6);
                a6.setServicios(Arrays.asList("Wifi", "Agua Caliente", "Cocina Compartida", "Luz incluida"));
                a6.setReglas(Arrays.asList("No visitas nocturnas", "Limpiar después de cocinar", "Ahorro de energía"));

                Alojamiento a7 = new Alojamiento();
                a7.setTitulo("Studio Minimalista San Eduardo");
                a7.setDescripcion("Diseño moderno y funcional, ideal para estudiantes de postgrado que buscan paz y orden.");
                a7.setPrecioMensual(900.0);
                a7.setUbicacion("San Eduardo, Piura");
                a7.setImagen("alojamiento2.png");
                a7.setImagenes(Arrays.asList("alojamiento2.png", "alojamiento3.png", "alojamiento6.png"));
                a7.setHabitaciones(1);
                a7.setBanos(1);
                a7.setCalificacionPromedio(4.9);
                a7.setServicios(Arrays.asList("Cámaras de seguridad", "Intercomunicador", "Área de lectura", "Aire Acondicionado"));
                a7.setReglas(Arrays.asList("Prohibido fumar", "No mascotas", "Respetar a los vecinos"));

                Alojamiento a8 = new Alojamiento();
                a8.setTitulo("Casa Compartida Universitaria");
                a8.setDescripcion("Ambiente familiar y tranquilo, a pocas cuadras de la facultad. Patio trasero ideal para estudiar al aire libre.");
                a8.setPrecioMensual(400.0);
                a8.setUbicacion("Urb. Miraflores, Piura");
                a8.setImagen("alojamiento3.png");
                a8.setImagenes(Arrays.asList("alojamiento3.png", "alojamiento4.png", "alojamiento6.png"));
                a8.setHabitaciones(4);
                a8.setBanos(2);
                a8.setCalificacionPromedio(4.4);
                a8.setServicios(Arrays.asList("Jardín", "Parrilla", "Lavadora", "Bicicletero"));
                a8.setReglas(Arrays.asList("Turnos de limpieza", "No ruidos después de las 11 PM", "Compartir gastos comunes"));

                Alojamiento a9 = new Alojamiento();
                a9.setTitulo("Apartamento Ejecutivo Los Geranios");
                a9.setDescripcion("Acabados de lujo y seguridad 24 horas. Cerca de centros comerciales y zonas de ocio universitario.");
                a9.setPrecioMensual(1100.0);
                a9.setUbicacion("Los Geranios, Piura");
                a9.setImagen("alojamiento4.png");
                a9.setImagenes(Arrays.asList("alojamiento4.png", "alojamiento5.png", "alojamiento6.png"));
                a9.setHabitaciones(2);
                a9.setBanos(2);
                a9.setCalificacionPromedio(4.8);
                a9.setServicios(Arrays.asList("Portería 24h", "Ascensor", "Piscina", "Gimnasio"));
                a9.setReglas(Arrays.asList("No ruidos molestos", "Uso correcto de piscina", "Identificación en portería"));

                Alojamiento a10 = new Alojamiento();
                a10.setTitulo("Habitación Económica Estudiantil");
                a10.setDescripcion("Opción accesible con internet de alta velocidad incluido. Todo lo básico para un estudiante enfocado.");
                a10.setPrecioMensual(350.0);
                a10.setUbicacion("Castilla, Piura");
                a10.setImagen("alojamiento5.png");
                a10.setImagenes(Arrays.asList("alojamiento5.png", "alojamiento1.png", "alojamiento6.png"));
                a10.setHabitaciones(1);
                a10.setBanos(1);
                a10.setCalificacionPromedio(4.3);
                a10.setServicios(Arrays.asList("Internet 500Mbps", "Ventilador", "Mesa de estudio", "Cerca a paraderos"));
                a10.setReglas(Arrays.asList("No fumar en cuarto", "Uso moderado de luz", "Pago adelantado"));

                repository.saveAll(Arrays.asList(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10));
            }
        };
    }
}
