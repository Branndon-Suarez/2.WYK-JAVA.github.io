package com.proyecto_wyk.proyecto_wyk.repository;

import com.proyecto_wyk.proyecto_wyk.entity.Usuario;
import org.springframework.stereotype.Repository;
import com.proyecto_wyk.proyecto_wyk.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    /*  Métodos de Consulta Derivada en Spring Data JPA
    - ¿Qué son?
     Potente función que permiten realizar/generar consultas comunes a BD con solo nombrar el método. Algunas palabras son:
     Nota: Los 'campo' se refiere a los atributos de la entidad respectiva.
    1.findBy, getBy, readBy:
        - Busca registros donde el campo coincida exactamente.  - Sintaxis: findByCampo(...)
    2.countBy:
        - Cuenta cuántos registros tienen ese valor.    - Sintaxis: countByCampo(...)
    3.deleteBy, removeBy:
        - Elimina registros que cumplen la condición.   - Sintaxis: deleteByCampo(...)
    4. existsBy
        - Devuelve true/false si existe al menos un registro con ese valor. - Sintaxis: existsByCampo(...)
   Estos son los principales, pero hay más.*/
    boolean existsByRol(String rol);
    Optional<Rol> findByRol(String rol);
}
