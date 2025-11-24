package com.proyecto_wyk.proyecto_wyk.service;

import com.proyecto_wyk.proyecto_wyk.entity.Rol;

import java.util.List;

public interface IRolService {
    List<Rol> listarRol();
    long cantRolesExistentes();
    long cantRolesActivos();
    long cantRolesInactivos();

    Rol guardarRol(Rol rol);
    boolean existeRol(String nombreRol);

    Rol buscarPorId(Integer id);
    void eliminarRol(Integer id);
}
