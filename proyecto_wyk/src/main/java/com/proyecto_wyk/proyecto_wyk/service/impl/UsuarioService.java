package com.proyecto_wyk.proyecto_wyk.service.impl;

import com.proyecto_wyk.proyecto_wyk.entity.Rol;
import com.proyecto_wyk.proyecto_wyk.entity.Usuario;
import com.proyecto_wyk.proyecto_wyk.repository.UsuarioRepository;
import com.proyecto_wyk.proyecto_wyk.service.IUsuarioService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class UsuarioService implements IUsuarioService {
    public final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Usuario> listarUsuario() {
        return repository.findAll();
    }

    @Override
    public List<Usuario> listarUsuariosFiltrados(Map<String, String> params) {
        List<Usuario> lista = repository.findAll();

        // --- Filtro de búsqueda global ---
        String search = params.get("search");
        if (search != null && !search.isEmpty()) {
            String s = search.toLowerCase();
            lista.removeIf(u ->
                    !(String.valueOf(u.getNumDoc()).contains(s)
                    || u.getNombre().toLowerCase().contains(s)
                    || String.valueOf(u.getTelUsuario()).contains(s)
                    || u.getEmailUsuario().toLowerCase().contains(s)
                    || String.valueOf(u.getFechaRegistro()).contains(s)
                    )
            );
        }

        // --- Filtro de estado ---
        String estado = params.get("estado");
        if (estado !=null && !estado.equals("todos")) {
            boolean activo = estado.equals("activo");
            lista.removeIf(u -> u.isEstadoUsuario() != activo);
        }

        // --- Filtros dinámicos: chips y acordeones ---
        params.forEach((clave, valor) -> {
            if (clave.startsWith("filtro_") && valor != null && !valor.isEmpty()) {
                String columna = clave.replace("filtro_","").toLowerCase();
                String[] filtros = valor.split(",");

                switch (columna) {

                    case "ROL":
                        lista.removeIf(u ->
                                !Arrays.stream(filtros)
                                        .anyMatch(f -> f.equalsIgnoreCase(u.getRol().getRol()))
                        );
                        break;

                    case "CLASIFICACION":
                        lista.removeIf(u ->
                                !Arrays.stream(filtros)
                                        .anyMatch(f -> f.equalsIgnoreCase(u.getRol().getClasificacion().name()))
                        );
                        break;

                    case "NOMBRE":
                        lista.removeIf(u ->
                                !Arrays.stream(filtros)
                                        .anyMatch(f -> u.getNombre().equalsIgnoreCase(f))
                        );
                        break;

                    case "DOCUMENTO":
                        lista.removeIf(u ->
                                !Arrays.stream(filtros)
                                        .anyMatch(f -> f.equals(String.valueOf(u.getNumDoc())))
                        );
                        break;

                    case "TELEFONO":
                        lista.removeIf(u ->
                                !Arrays.stream(filtros)
                                        .anyMatch(f -> f.equals(String.valueOf(u.getTelUsuario())))
                        );
                        break;

                    case "EMAIL":
                        lista.removeIf(u ->
                                !Arrays.stream(filtros)
                                        .anyMatch(f -> u.getEmailUsuario().equalsIgnoreCase(f))
                        );
                        break;
                }
            }
        });

        return lista;
    }

    @Override
    public long cantUsuariosExistentes() {
        return repository.count();
    }

    @Override
    public long cantUsuariosActivos() {
        return repository.findAll().stream()
                .filter(Usuario::isEstadoUsuario)
                .count();
    }

    @Override
    public long cantUsuariosInactivos() {
        return repository.findAll().stream()
                .filter(u -> !u.isEstadoUsuario())
                .count();
    }

    @Override
    public Usuario guardarUsuario(Usuario usuario) {
        return repository.save(usuario);
    }

    @Override
    public boolean existeUsuario(Long numDoc) {
        return repository.existsByNumDoc(numDoc);
    }

    @Override
    public Usuario buscarPorID(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void eliminarRol(Long id) {
        repository.deleteById(id);
    }
}
