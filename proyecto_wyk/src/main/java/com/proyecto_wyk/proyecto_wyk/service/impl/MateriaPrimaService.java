package com.proyecto_wyk.proyecto_wyk.service.impl;

import com.proyecto_wyk.proyecto_wyk.entity.MateriaPrima;
import com.proyecto_wyk.proyecto_wyk.repository.MateriaPrimaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MateriaPrimaService {
    private final MateriaPrimaRepository materiaPrimaRepository;

    public MateriaPrimaService(MateriaPrimaRepository materiaPrimaRepository) {
        this.materiaPrimaRepository = materiaPrimaRepository;
    }

    // Método para listar todas las materias primas (usado en el modal)
    public List<MateriaPrima> listarTodas() {
        return materiaPrimaRepository.findAll();
    }

    public List<MateriaPrima> listarTodasActivas() {
        return materiaPrimaRepository.findByEstadoMateriaPrimaTrue();
    }

    // Método para buscar por ID (necesario para la Compra)
    public MateriaPrima buscarPorId(Long id) {
        return materiaPrimaRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Materia Prima con ID " + id + " no encontrada.")
        );
    }
}
