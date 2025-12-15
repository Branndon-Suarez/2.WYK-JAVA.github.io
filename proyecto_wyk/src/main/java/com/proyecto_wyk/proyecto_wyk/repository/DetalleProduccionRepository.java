package com.proyecto_wyk.proyecto_wyk.repository;

import com.proyecto_wyk.proyecto_wyk.entity.DetalleProduccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleProduccionRepository extends JpaRepository<DetalleProduccion,Long> {
    List<DetalleProduccion> findByProduccion_IdProduccion(Long idProduccion);
}
