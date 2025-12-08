package com.proyecto_wyk.proyecto_wyk.repository;

import com.proyecto_wyk.proyecto_wyk.entity.MateriaPrima;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MateriaPrimaRepository extends JpaRepository<MateriaPrima, Long> {
    // Método que necesitarás para el modal (asumiendo que buscas activas o todas)
    List<MateriaPrima> findAll(); // Opcional: findByEstado(boolean estado) si tuvieras estado
}