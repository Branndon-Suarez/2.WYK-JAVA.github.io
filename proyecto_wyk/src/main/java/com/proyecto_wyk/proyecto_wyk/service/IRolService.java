package com.proyecto_wyk.proyecto_wyk.service;

import com.proyecto_wyk.proyecto_wyk.entity.Rol;

import java.util.List;
import java.util.Map;

public interface IRolService {
    List<Rol> listarRol();
    long cantRolesExistentes();
    long cantRolesActivos();
    long cantRolesInactivos();

    Rol guardarRol(Rol rol);
    boolean existeRol(String nombreRol);

    Rol buscarPorId(Integer id);
    void eliminarRol(Integer id);

    List<Rol> listarRolesFiltrados(Map<String, String> params);

}
