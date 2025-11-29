package com.proyecto_wyk.proyecto_wyk.dto.usuario;

import com.proyecto_wyk.proyecto_wyk.entity.Rol;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

public class UsuarioCreateDTO {
    @NotBlank(message = "El número de documento es obligatorio.")
    @Size(
            min = 7, max = 11,
            message = "El número de documento debe tener entre 7 y 11 dígitos. "
    )
    @Pattern(
            regexp = "^[0-9]+$",
            message = "El número de documento solo puede contener números."
    )
    private String numDoc;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(
            min = 3, max = 50,
            message = "El nombre debe de tener entre 3 y 50 carácteres."
    )
    @Pattern(
            regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$",
            message = "El nombre solo puede contener letras."
    )
    private String nombre;

    @NotBlank(message = "La contraseña es obligatoria.")
    private String passwordUsuario;

    @NotBlank(message = "El número de teléfono es obligatorio.")
    @Size(
            min = 10, max = 10,
            message = "El número de teléfono debe tener 10 dígitos."
    )
    @Pattern(
            regexp = "^[0-9]+$",
            message = "El número de teléfono debe tener solo números."
    )
    private String telUsuario;

    @Email(message = "Correo inválido.")
    @NotBlank(message = "El correo es obligatorio.")
    private String emailUsuario;

    @NotNull(message = "Debe seleccionar mínimo un rol")
    private Integer rolId;


    // Getters y setters
    public String getNumDoc() {
        return numDoc;
    }
    public void setNumDoc(String numDoc) {
        this.numDoc = numDoc;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPasswordUsuario() {
        return passwordUsuario;
    }
    public void setPasswordUsuario(String passwordUsuario) {
        this.passwordUsuario = passwordUsuario;
    }

    public String getTelUsuario() {
        return telUsuario;
    }
    public void setTelUsuario(String telUsuario) {
        this.telUsuario = telUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }
    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public Integer getRolId() {
        return rolId;
    }
    public void setRolId(Integer rolId) {
        this.rolId = rolId;
    }
}
