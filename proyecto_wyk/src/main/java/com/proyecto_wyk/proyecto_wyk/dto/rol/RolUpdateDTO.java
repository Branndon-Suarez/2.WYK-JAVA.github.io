package com.proyecto_wyk.proyecto_wyk.dto.rol;

import com.proyecto_wyk.proyecto_wyk.entity.Rol;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RolUpdateDTO {
    @NotNull(message = "El ID del rol es obligatorio.")
    private Integer idRol;

    @NotNull(message = "El nombre del rol es obligatorio.")
    @Size(
            min = 3, max = 50,
            message = "El rol debe tener entre 3 y 50 carácteres."
    )
    @Pattern(
            regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$",
            message = "El rol solo puede contener letras."
    )
    private String rol;

    @NotNull(message = "Debe seleccionar una clasificación.")
    private Rol.Clasificacion clasificacion;

    @NotNull(message = "Debe seleccionar el estado del rol.")
    private Boolean estadoRol;

    // Getters y setters
    public Integer getIdRol() {
        return this.idRol;
    }
    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

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

    public boolean getEstadoRol() {
        return this.estadoRol;
    }
    public void setEstadoRol(boolean estadoRol) {
        this.estadoRol = estadoRol;
    }
}
