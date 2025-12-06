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

    private void crearUsuarioSiNoExiste(UsuarioRepository userRepo, PasswordEncoder encoder, Optional<Rol> rolOpt, String nombreBase, String email, Long numDoc, String password) {
        if (userRepo.findByEmailUsuario(email).isEmpty() && rolOpt.isPresent()) {
            Usuario usuario = new Usuario();

            usuario.setNumDoc(numDoc);
            usuario.setNombre(nombreBase + " Base");
            usuario.setPasswordUsuario(encoder.encode(password));
            usuario.setTelUsuario(3001234567L + numDoc); // Generar un teléfono único
            usuario.setEmailUsuario(email);
            usuario.setFechaRegistro(LocalDateTime.now());
            usuario.setRol(rolOpt.get());
            usuario.setEstadoUsuario(true);

            userRepo.save(usuario);
            System.out.println("Usuario " + nombreBase + " creado: " + email + " / " + password);
        }
    }

    @Bean
    CommandLineRunner initRolesAndAdmin(RolRepository rolRepo, UsuarioRepository userRepo, PasswordEncoder encoder) {
        return args -> {

            // --- 1. Crear Roles de Negocio ---
            // 1.1. ROL: ADMINISTRADOR (Clasificación ADMINISTRADOR)
            Optional<Rol> adminRoleOpt = crearRolSiNoExiste(rolRepo, "ADMINISTRADOR", Rol.Clasificacion.ADMINISTRADOR);
            // 1.2. ROL: MESERO (Clasificación EMPLEADO)
            Optional<Rol> meseroRoleOpt = crearRolSiNoExiste(rolRepo, "MESERO", Rol.Clasificacion.EMPLEADO);
            // 1.3. ROL: CAJERO (Clasificación EMPLEADO)
            Optional<Rol> cajeroRoleOpt = crearRolSiNoExiste(rolRepo, "CAJERO", Rol.Clasificacion.EMPLEADO);
            // 1.4. ROL: COCINERO (Clasificación EMPLEADO)
            Optional<Rol> cocineroRoleOpt = crearRolSiNoExiste(rolRepo, "COCINERO", Rol.Clasificacion.EMPLEADO);


            // --- 2. Crear usuario ADMIN si no existe ---
            // 2.1. Usuario ADMINISTRADOR
            crearUsuarioSiNoExiste(userRepo, encoder, adminRoleOpt, "Administrador", "admin@wyk.com", 1000000000L, "admin123");
            // 2.2. Usuario MESERO
            crearUsuarioSiNoExiste(userRepo, encoder, meseroRoleOpt, "Mesero", "mesero@wyk.com", 1000000001L, "mesero123");
            // 2.3. Usuario CAJERO
            crearUsuarioSiNoExiste(userRepo, encoder, cajeroRoleOpt, "Cajero", "cajero@wyk.com", 1000000002L, "cajero123");
            // 2.4. Usuario COCINERO
            crearUsuarioSiNoExiste(userRepo, encoder, cocineroRoleOpt, "Cocinero", "cocinero@wyk.com", 1000000003L, "cocinero123");
        };
    }
}
