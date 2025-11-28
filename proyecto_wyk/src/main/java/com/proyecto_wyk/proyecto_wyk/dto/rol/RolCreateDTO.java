package com.proyecto_wyk.proyecto_wyk.dto.rol;

import com.proyecto_wyk.proyecto_wyk.entity.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RolCreateDTO {
    @NotBlank(message = "El nombre del rol debe ser obligatorio.")
    @Size(
            min = 3, max = 50,
            message = "El rol debe tener entre 3 y 50 carácteres."
    )
    @Pattern(
            regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$",
            message = "El rol solo puede contener letras"
    )
    private String rol;

    @NotNull(message = "Debe seleccionar una clasificación.")
    private Rol.Clasificacion clasificacion;

    // Getters y setters
    public String getRol() {
        return rol;
    }
    public void setRol(String rol) {
        this.rol = rol;
    }

    public Rol.Clasificacion getClasificacion() {
        return clasificacion;
    }
    public void setClasificacion(Rol.Clasificacion clasificacion) {
        this.clasificacion = clasificacion;
    }
}
