package com.proyecto_wyk.proyecto_wyk.repository;

import com.proyecto_wyk.proyecto_wyk.entity.AjusteInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// Usa Integer para el tipo de la clave primaria (ID_AJUSTE INT)
public interface AjusteInventarioRepository extends JpaRepository<AjusteInventario, Integer> {

    // Puedes agregar métodos personalizados aquí si es necesario

}