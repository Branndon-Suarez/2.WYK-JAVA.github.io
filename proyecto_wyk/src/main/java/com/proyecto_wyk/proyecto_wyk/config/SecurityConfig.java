package com.proyecto_wyk.proyecto_wyk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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

                        // Reglas de Autorización basadas en el campo ROL
                        .requestMatchers("/admin/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers("/caja/**").hasAuthority("CAJERO")
                        .requestMatchers("/cocina/**").hasAuthority("COCINERO")
                        .requestMatchers("/mesas/**").hasAuthority("MESERO")

                        // Cualquier otra solicitud requiere autenticación
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        // **NO SE ESPECIFICA usernameParameter** -> Spring Security usará el campo 'username' por defecto.
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error")
                )
                .logout(logout -> logout
                        .permitAll()
                        .logoutSuccessUrl("/login?logout")
                );

        return http.build();
    }
}
