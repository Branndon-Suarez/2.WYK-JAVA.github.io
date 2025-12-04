package com.proyecto_wyk.proyecto_wyk.security;

import com.proyecto_wyk.proyecto_wyk.entity.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    private final Usuario usuario;

    public CustomUserDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    // Spring Security usa 'Authorities' para los roles.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Ahora mapea el campo ROL de la entidad Rol (ej: "CAJERO", "ADMINISTRADOR")
        // Se asegura de convertir el nombre del rol a mayúsculas para un estándar
        return Collections.singletonList(
                new SimpleGrantedAuthority(usuario.getRol().getRol().toUpperCase())
        );
    }

    @Override
    public String getPassword() {
        return usuario.getPasswordUsuario();
    }

    @Override
    public String getUsername() {
        // CAMBIO: Usamos el EMAIL_USUARIO como "username" para Spring Security
        return usuario.getEmailUsuario();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Me permite saber el estado de la cuenta del usuario logueado
    @Override
    public boolean isEnabled() {
        return usuario.isEstadoUsuario();
    }
}
