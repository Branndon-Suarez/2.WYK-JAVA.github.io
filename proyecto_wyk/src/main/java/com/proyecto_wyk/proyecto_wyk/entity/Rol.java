package com.proyecto_wyk.proyecto_wyk.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ROL")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ROL", nullable = false, unique = true)
    private Integer idRol;
    @Column(name = "ROL", nullable = false, length = 50)
    private String rol;
    @Enumerated(EnumType.STRING)
    @Column(name = "CLASIFICACION", nullable = false)
    private Clasificacion clasificacion;
    @Column(name = "ESTADO_ROL")
    private boolean estadoRol;

    // Enum interno para reflejar el ENUM de MySQL
    public enum Clasificacion {
        EMPLEADO, ADMINISTRADOR
    }

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

    public Clasificacion getClasificacion() {
        return clasificacion;
    }
    public void setClasificacion(Clasificacion clasificacion) {
        this.clasificacion = clasificacion;
    }

    public boolean getEstadoRol() {
        return this.estadoRol;
    }
    public void setEstadoRol(boolean estadoRol) {
        this.estadoRol = estadoRol;
    }
}
