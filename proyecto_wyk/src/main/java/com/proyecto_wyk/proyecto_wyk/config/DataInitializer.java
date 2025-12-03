package com.proyecto_wyk.proyecto_wyk.config;

import com.proyecto_wyk.proyecto_wyk.entity.Rol;
import com.proyecto_wyk.proyecto_wyk.entity.Usuario;
import com.proyecto_wyk.proyecto_wyk.repository.RolRepository;
import com.proyecto_wyk.proyecto_wyk.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

@Configuration
public class DataInitializer {
    // Método privado auxiliar para simplificar la creación de roles
    private Optional<Rol> crearRolSiNoExiste(RolRepository rolRepo, String nombreRol, Rol.Clasificacion clasificacion) {
        Optional<Rol> rolOpt = rolRepo.findByRol(nombreRol);

        if (rolOpt.isEmpty()) {
            Rol nuevoRol = new Rol();
            nuevoRol.setRol(nombreRol); // El permiso/autoridad: ADMINISTRADOR, MESERO, CAJERO, COCINERO
            nuevoRol.setClasificacion(clasificacion); // La clasificación de negocio: ADMINISTRADOR o EMPLEADO
            nuevoRol.setEstadoRol(true); // Siempre activo
            rolRepo.save(nuevoRol);
            System.out.println("Rol " + nombreRol + " creado.");
            return Optional.of(nuevoRol);
        }
        return rolOpt;
    }

    @Bean
    CommandLineRunner initRolesAndAdmin(RolRepository rolRepo, UsuarioRepository userRepo, PasswordEncoder encoder) {
        return args -> {

            // --- 1. Crear Roles de Negocio ---

            // 1.1. ROL: ADMINISTRADOR (Clasificación ADMINISTRADOR)
            Optional<Rol> adminRoleOpt = crearRolSiNoExiste(rolRepo, "ADMINISTRADOR", Rol.Clasificacion.ADMINISTRADOR);

            // 1.2. ROL: MESERO (Clasificación EMPLEADO)
            crearRolSiNoExiste(rolRepo, "MESERO", Rol.Clasificacion.EMPLEADO);

            // 1.3. ROL: CAJERO (Clasificación EMPLEADO)
            crearRolSiNoExiste(rolRepo, "CAJERO", Rol.Clasificacion.EMPLEADO);

            // 1.4. ROL: COCINERO (Clasificación EMPLEADO)
            crearRolSiNoExiste(rolRepo, "COCINERO", Rol.Clasificacion.EMPLEADO);


            // --- 2. Crear usuario ADMIN si no existe ---

            String adminEmail = "admin@wyk.com";
            if (userRepo.findByEmailUsuario(adminEmail).isEmpty() && adminRoleOpt.isPresent()) {
                Usuario admin = new Usuario();

                // Datos del administrador base
                admin.setNumDoc(1000000000L);
                admin.setNombre("Administrador Base");
                admin.setPasswordUsuario(encoder.encode("admin123")); // Contraseña codificada
                admin.setTelUsuario(3001234567L);
                admin.setEmailUsuario(adminEmail);
                admin.setFechaRegistro(LocalDateTime.now());
                admin.setRol(adminRoleOpt.get()); // Asigna el objeto Rol ADMINISTRADOR
                admin.setEstadoUsuario(true);

                userRepo.save(admin);
                System.out.println("Usuario ADMIN creado: " + adminEmail + " / admin123");
            }
        };
    }
}
