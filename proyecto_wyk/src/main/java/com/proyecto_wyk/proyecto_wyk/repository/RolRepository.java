package com.proyecto_wyk.proyecto_wyk.repository;

import org.springframework.stereotype.Repository;
import com.proyecto_wyk.proyecto_wyk.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    boolean existsByRol(String rol);
}
