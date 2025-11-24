package com.proyecto_wyk.proyecto_wyk.service.impl;

import org.springframework.stereotype.Service;
import com.proyecto_wyk.proyecto_wyk.entity.Rol;
import com.proyecto_wyk.proyecto_wyk.repository.RolRepository;
import com.proyecto_wyk.proyecto_wyk.service.IRolService;

import java.util.List;
import java.util.Map;

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

    @Override
    public List<Rol> listarRolesFiltrados(Map<String, String> params) {
        List<Rol> lista = repository.findAll();

        // --- Filtro de búsqueda global ---
        String search = params.get("search");
        if (search != null && !search.isEmpty()) {
            String s = search.toLowerCase();
            lista.removeIf(r -> !(r.getRol().toLowerCase().contains(s)
                    || r.getClasificacion().name().toLowerCase().contains(s)));
        }

        // --- Filtro de estado ---
        String estado = params.get("estado");
        if (estado != null && !estado.equals("todos")) {
            boolean activo = estado.equals("activo");
            lista.removeIf(r -> r.getEstadoRol() != activo);
        }

        // --- Filtros dinámicos: chips y acordeones ---
        params.forEach((clave, valor) -> {
            if (clave.startsWith("filtro_") && valor != null && !valor.isEmpty()) {

                String columna = clave.replace("filtro_", "").toUpperCase();
                String[] filtros = valor.split(",");

                switch (columna) {
                    case "ROL":
                        lista.removeIf(r -> !java.util.Arrays.asList(filtros).contains(r.getRol()));
                        break;

                    case "CLASIFICACION":
                        lista.removeIf(r -> !java.util.Arrays.stream(filtros)
                                .anyMatch(f -> f.equalsIgnoreCase(r.getClasificacion().name())));
                        break;

                    // Agregar aquí más columnas si en el futuro existen otras.
                }
            }
        });

        return lista;
    }
}
