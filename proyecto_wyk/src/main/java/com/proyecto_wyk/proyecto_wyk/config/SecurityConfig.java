package com.proyecto_wyk.proyecto_wyk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // Define el codificador de contraseñas.
    // BCrypt es el estándar recomendado.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Define las reglas de autorización (qué roles acceden a qué URLs)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Permite acceso solo a la raíz y recursos estáticos. ELIMINAMOS /registro
                        .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/lord-icon/**").permitAll()

                        // --- 🎯 REGLAS GENERALES BASADAS EN ROL ---
                        .requestMatchers("/admin/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers("/caja/**").hasAuthority("CAJERO")
                        .requestMatchers("/cocina/**").hasAuthority("COCINERO")
                        .requestMatchers("/mesas/**").hasAuthority("MESERO")

                        // -----------------------------------------------------------------
                        // 🎯 0. ACCESO AL DASHBOARD INICIAL
                        .requestMatchers("/dashboard")
                        .hasAnyAuthority("ADMINISTRADOR", "MESERO", "CAJERO", "COCINERO")

                        // -----------------------------------------------------------------
                        // 🎯 1. REGLAS PARA ROLES (CRUD COMPLETO)
                        // Vistas (GETs: Listar, formGuardar, formAct)
                        .requestMatchers(HttpMethod.GET,"/roles", "/roles/**")
                        .hasAuthority("ADMINISTRADOR")

                        // -----------------------------------------------------------------
                        // 🎯 2. REGLAS PARA USUARIOS (CRUD)
                        // Vistas (GETs: Listar, formGuardar, formAct)
                        .requestMatchers(HttpMethod.GET,"/usuarios", "/usuarios/**")
                        .hasAuthority("ADMINISTRADOR")

                        // -----------------------------------------------------------------
                        // 🎯 3. REGLAS PARA TAREAS (VISTA Y API)
                        // Vistas (GETs: Listar, formGuardar, formAct)
                        .requestMatchers(HttpMethod.GET, "/tareas", "/tareas/**")
                        .hasAnyAuthority("ADMINISTRADOR", "MESERO")

                        // Acciones POST
                        .requestMatchers(HttpMethod.POST, "/tareas/guardar", "/tareas/actualizar", "/tareas/delete", "/tareas/updateState")
                        .hasAnyAuthority("ADMINISTRADOR")

                        // -----------------------------------------------------------------
                        // 🎯 4. REGLAS PARA PRODUCTO (VISTA Y API)
                        // Vistas (GETs: Listar, formGuardar, formAct)
                        .requestMatchers(HttpMethod.GET, "/productos", "/productos/**")
                        .hasAnyAuthority("ADMINISTRADOR")

                        // Acciones POST
                        .requestMatchers(HttpMethod.POST, "/tareas/guardar", "/tareas/actualizar", "/tareas/delete", "/tareas/updateState")
                        .hasAnyAuthority("ADMINISTRADOR", "COCINERO")

                        // -----------------------------------------------------------------
                        // 🎯 5. REGLAS PARA VENTA (VISTA Y API)
                        // Vistas (GETs: Listar, formGuardar, formAct)
                        .requestMatchers(HttpMethod.GET, "/ventas", "/ventas/**")
                        .hasAnyAuthority("ADMINISTRADOR", "MESERO")

                        // Acciones POST
                        .requestMatchers(HttpMethod.POST, "/ventas/guardar", "/ventas/actualizar")
                        .hasAnyAuthority("ADMINISTRADOR", "MESERO")

                        // -----------------------------------------------------------------
                        // 🎯 6. REGLAS PARA COMPRA (VISTA Y API)
                        // Vistas (GETs: Listar, formGuardar, formAct)
                        .requestMatchers(HttpMethod.GET, "/compras", "/compras/**")
                        .hasAnyAuthority("ADMINISTRADOR", "MESERO")

                        // Acciones POST
                        .requestMatchers(HttpMethod.POST, "/compras/guardar", "/compras/actualizar")
                        .hasAnyAuthority("ADMINISTRADOR", "MESERO")

                        // Cualquier otra solicitud requiere autenticación
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        // **NO SE ESPECIFICA usernameParameter** -> Spring Security usará el campo 'username' por defecto.
                        .defaultSuccessUrl("/dashboard", true)
                        .failureHandler(authenticationFailureHandler())
//                        .failureUrl("/login?error")
                )
                .logout(logout -> logout
                        .permitAll()
                        .logoutSuccessUrl("/login?logout")
                );

        return http.build();
    }

    // BEAN: Handler para manejar errores específicos de autenticación (usuario inactivo)
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            String redirectUrl = "/login?error";

            // La excepción lanzada cuando isEnabled() es false es DisabledException
            if (exception instanceof DisabledException) {
                redirectUrl = "/login?error=disabled";
            }

            response.sendRedirect(request.getContextPath() + redirectUrl);
        };
    }
}
