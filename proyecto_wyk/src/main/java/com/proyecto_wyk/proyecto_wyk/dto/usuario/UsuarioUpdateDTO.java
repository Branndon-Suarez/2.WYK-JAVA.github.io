package com.proyecto_wyk.proyecto_wyk.dto.usuario;

import jakarta.validation.constraints.*;

import java.util.List;

public class UsuarioUpdateDTO {
    @NotNull(message = "El ID del usuario es *OBLIGATORIO*.")
    private Long idUsuario;

    @NotBlank(message = "El número de documento es *OBLIGATORIO*.")
    @Size(
            min = 7, max = 11,
            message = "El número de documento debe tener entre 7 y 11 dígitos. "
    )
    @Pattern(
            regexp = "^[0-9]+$",
            message = "El número de documento solo puede contener números."
    )
    private String numDoc;

    @NotBlank(message = "El nombre es *OBLIGATORIO*.")
    @Size(
            min = 3, max = 50,
            message = "El nombre debe de tener entre 3 y 50 carácteres."
    )
    @Pattern(
            regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$",
            message = "El nombre solo puede contener letras."
    )
    private String nombre;

    /* Nota: No se pone @NotBlank porque en el form de actu se enviará ese campo vacío por seguridad e internamente se pondrá el que ya estaba
    * si no se pone un password nuevo.*/
    private String passwordUsuario;

    @NotBlank(message = "El número de teléfono es *OBLIGATORIO*.")
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
    @NotBlank(message = "El correo es *OBLIGATORIO*.")
    private String emailUsuario;

    @NotNull(message = "Debe seleccionar un rol.")
    @Min(value = 1, message = "El rol seleccionado no es válido.")
    private Integer rolId;

    @NotNull(message = "Debe seleccionar el estado.")
    private boolean estadoUsuario;

    // Getters y setters
    public Long getIdUsuario() {
        return idUsuario;
    }
    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

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

    public boolean isEstadoUsuario() {
        return estadoUsuario;
    }
    public void setEstadoUsuario(boolean estadoUsuario) {
        this.estadoUsuario = estadoUsuario;
    }
}
