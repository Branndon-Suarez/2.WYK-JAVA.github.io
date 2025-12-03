package com.proyecto_wyk.proyecto_wyk.service.impl;

import com.proyecto_wyk.proyecto_wyk.entity.Tarea;
import com.proyecto_wyk.proyecto_wyk.repository.TareaRepository;
import com.proyecto_wyk.proyecto_wyk.service.ITareaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TareaService implements ITareaService {
    public final TareaRepository repository;
    public TareaService(TareaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Tarea> listarTarea() {
        return repository.findAll();
    }

//    @Override
//    public List<Tarea> listarTareasFiltrados(Map<String, String> params) {
//        péndiente
//    }

    @Override
    public long cantTareasExistentes() {
        return repository.count();
    }

    @Override
    public long cantTareasPendientes() {
        return repository.findAll().stream()
                .filter(t -> t.getEstadoTarea().equals(Tarea.EstadoTarea.PENDIENTE))
                .count();
    }

    @Override
    public long cantTareasCompletada() {
        return repository.findAll().stream()
                .filter(t -> t.getEstadoTarea().equals(Tarea.EstadoTarea.COMPLETADA))
                .count();
    }

    @Override
    public long cantTareasCancelada() {
        return repository.findAll().stream()
                .filter(t -> t.getEstadoTarea().equals(Tarea.EstadoTarea.CANCELADA))
                .count();
    }

    @Override
    public Tarea guardarTarea(Tarea tarea) {
        return repository.save(tarea);
    }

    @Override
    public boolean existeTarea(Long idTarea) {
        return repository.existsById(idTarea);
    }

    @Override
    public Tarea buscarPorID(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void eliminarTarea(Long id) {
        repository.deleteById(id);
    }

}
