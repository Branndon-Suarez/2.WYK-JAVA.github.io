package com.proyecto_wyk.proyecto_wyk.service;

import com.proyecto_wyk.proyecto_wyk.entity.AjusteInventario;
import java.util.List;
import java.util.Map;

public interface IAjusteInventarioService {

    // --- Métodos de Listado y Filtrado ---

    List<AjusteInventario> listarAjustes();
    List<AjusteInventario> listarAjustesFiltrados(Map<String, String> params);

    // --- Métodos de Conteo/Reporte ---

    long contarAjustesExistentes();
    long cantAjustesDañados();
    long cantAjustesRobo();
    long cantAjustesPerdida();
    long cantAjustesCaducados();
    long cantAjustesMuestra();

    // --- Métodos CRUD ---

    AjusteInventario guardarAjuste(AjusteInventario ajuste);
    AjusteInventario buscarPorID(Integer id);
    void eliminarAjuste(Integer id);
}