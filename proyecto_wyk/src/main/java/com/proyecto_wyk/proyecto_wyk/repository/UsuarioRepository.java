package com.proyecto_wyk.proyecto_wyk.repository;

import com.proyecto_wyk.proyecto_wyk.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByNumDoc(Long numDoc);

    boolean existsByEmailUsuario(String emailUsuario);

    Optional<Usuario> findByEmailUsuario(String emailUsuario);
}
