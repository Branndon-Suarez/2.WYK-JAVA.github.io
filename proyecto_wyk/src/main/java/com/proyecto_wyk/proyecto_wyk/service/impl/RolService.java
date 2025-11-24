package com.proyecto_wyk.proyecto_wyk.service.impl;

import org.springframework.stereotype.Service;
import com.proyecto_wyk.proyecto_wyk.entity.Rol;
import com.proyecto_wyk.proyecto_wyk.repository.RolRepository;
import com.proyecto_wyk.proyecto_wyk.service.IRolService;

import java.util.List;

@Service
public class RolService implements IRolService {
    public final RolRepository repository;

    public RolService(RolRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Rol> listarRol() {
        return repository.findAll();
    }

    @Override
    public long cantRolesExistentes() {
        return repository.count();
    }

    @Override
    public long cantRolesActivos() {
        return repository.findAll().stream()
                .filter(Rol::getEstadoRol)
                .count();
    }

    @Override
    public long cantRolesInactivos() {
        return repository.findAll().stream()
                .filter(rol -> !rol.getEstadoRol())
                .count();
    }

    @Override
    public Rol guardarRol(Rol rol) {
        return repository.save(rol);
    }

    @Override
    public boolean existeRol(String nombreRol) {
        return repository.existsByRol(nombreRol);
    }

    @Override
    public Rol buscarPorId(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void eliminarRol(Integer id) {
        repository.deleteById(id);
    }
}
