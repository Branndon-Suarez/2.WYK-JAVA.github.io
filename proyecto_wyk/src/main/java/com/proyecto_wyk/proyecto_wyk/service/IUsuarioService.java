package com.proyecto_wyk.proyecto_wyk.service;

import com.proyecto_wyk.proyecto_wyk.entity.Usuario;

import java.util.List;
import java.util.Map;

public interface IUsuarioService {
    List<Usuario> listarUsuario();
    List<Usuario> listarUsuariosFiltrados(Map<String, String> params);

    long cantUsuariosExistentes();
    long cantUsuariosActivos();
    long cantUsuariosInactivos();

    Usuario guardarUsuario(Usuario usuario);
    boolean existeUsuario(Long numDoc);

    Usuario buscarPorID(Long id);
    void eliminarRol(Long id);
}
