package com.proyecto_wyk.proyecto_wyk.service;


import com.proyecto_wyk.proyecto_wyk.entity.Tarea;

import java.util.List;
import java.util.Map;

public interface ITareaService {
    List<Tarea> listarTarea();
//    List<Tarea> listarTareasFiltrados(Map<String, String> params);

    long cantTareasExistentes();
    long cantTareasPendientes();
    long cantTareasCompletada();
    long cantTareasCancelada();

    Tarea guardarTarea(Tarea tarea);
    boolean existeTarea(String nombreTarea);

    Tarea buscarPorID(Long id);
    void eliminarTarea(Long id);
}
