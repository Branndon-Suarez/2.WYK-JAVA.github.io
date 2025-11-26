package com.proyecto_wyk.proyecto_wyk.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "USUARIO")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USUARIO", nullable = false)
    private Integer idUsuario;
    @Column(name = "NUM_DOC", unique = true, nullable = false)
    private Long numDoc;
    @Column(name = "NOMBRE", length = 50, nullable = false)
    private String nombre;
    @Column(name = "PASSWORD_USUARIO", length = 150, nullable = false)
    private String passwordUsuario;
    @Column(name = "TEL_USUARIO", nullable = false)
    private Long telUsuario;
    @Column(name = "EMAIL_USUARIO", length = 50, unique = true, nullable = false)
    private String emailUsuario;
    @Column(name = "FECHA_REGISTRO", nullable = false)
    private LocalDateTime fechaRegistro;

    // 1-M usuarios pertenecen a un rol y un rol pertenece a 1-M usuarios.
    @ManyToOne
    @JoinColumn(name = "ROL_FK_USUARIO", nullable = false)
    private Rol rol;

    @Column(name = "ESTADO_USUARIO", nullable = false)
    private boolean estadoUsuario;

    // Getters y setters

    public Integer getIdUsuario() {
        return idUsuario;
    }
    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getNumDoc() {
        return numDoc;
    }
    public void setNumDoc(Long numDoc) {
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

    public Long getTelUsuario() {
        return telUsuario;
    }
    public void setTelUsuario(Long telUsuario) {
        this.telUsuario = telUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }
    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Rol getRol() {
        return rol;
    }
    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean isEstadoUsuario() {
        return estadoUsuario;
    }
    public void setEstadoUsuario(boolean estadoUsuario) {
        this.estadoUsuario = estadoUsuario;
    }
}
