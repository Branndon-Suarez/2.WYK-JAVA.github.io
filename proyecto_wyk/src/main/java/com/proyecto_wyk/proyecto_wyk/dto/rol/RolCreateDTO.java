package com.proyecto_wyk.proyecto_wyk.dto.rol;

import com.proyecto_wyk.proyecto_wyk.entity.Rol;
import jakarta.validation.constraints.*;

public class RolCreateDTO {
    @NotBlank(message = "El nombre del rol debe ser *OBLIGATORIO*.")
    @Size(
            min = 3, max = 50,
            message = "El rol debe tener entre 3 y 50 carácteres."
    )
    @Pattern(
            regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$",
            message = "El rol solo puede contener letras"
    )
    private String rol;

    @NotEmpty(message = "Debe seleccionar una clasificación.")
    private String clasificacion;

    // Getters y setters
    public String getRol() {
        return rol;
    }
    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getClasificacion() {
        return clasificacion;
    }
    public void setClasificacion(String clasificacion) {
        this.clasificacion = clasificacion;
    }
}
