package com.proyecto_wyk.proyecto_wyk.service.impl;

import com.proyecto_wyk.proyecto_wyk.entity.Rol;
import com.proyecto_wyk.proyecto_wyk.entity.Usuario;
import com.proyecto_wyk.proyecto_wyk.repository.UsuarioRepository;
import com.proyecto_wyk.proyecto_wyk.service.IUsuarioService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

        // --- 1️⃣ BÚSQUEDA GLOBAL ---
        // formateador para fechaRegistro
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String search = params.get("search");
        if (search != null && !search.isEmpty()) {
            String s = search.toLowerCase();

            lista.removeIf(u -> {
                String numDocStr = u.getNumDoc() != null ? String.valueOf(u.getNumDoc()) : "";
                String nombre = u.getNombre() != null ? u.getNombre().toLowerCase() : "";
                String telStr = u.getTelUsuario() != null ? String.valueOf(u.getTelUsuario()) : "";
                String email = u.getEmailUsuario() != null ? u.getEmailUsuario().toLowerCase() : "";
                String fechaStr = u.getFechaRegistro() != null ? u.getFechaRegistro().format(dtf).toLowerCase() : "";
                String rolStr = (u.getRol() != null && u.getRol().getRol() != null) ? u.getRol().getRol().toLowerCase() : "";

                return !(
                        numDocStr.toLowerCase().contains(s)
                                || nombre.contains(s)
                                || telStr.toLowerCase().contains(s)
                                || email.contains(s)
                                || fechaStr.contains(s)
                                || rolStr.contains(s)
                );
            });
        }

        // --- 2️⃣ FILTRO DE ESTADO ---
        String estado = params.get("estado");
        if (estado != null && !estado.equals("todos")) {
            boolean activo = estado.equals("activo");
            lista.removeIf(u -> u.isEstadoUsuario() != activo);
        }

        // --- 3️⃣ FILTROS DINÁMICOS (chips) ---
        params.forEach((clave, valor) -> {

            if (clave.startsWith("filtro_") && valor != null && !valor.isEmpty()) {

                String columna = clave.replace("filtro_", "").toUpperCase();
                String[] filtros = valor.split(",");

                switch (columna) {
                    case "NUMERO_DOC":
                        lista.removeIf(u -> !Arrays.asList(filtros).contains(String.valueOf(u.getNumDoc())));
                        break;

                    case "NOMBRE":
                        lista.removeIf(u ->
                                !Arrays.stream(filtros)
                                        .anyMatch(f -> f.equalsIgnoreCase(u.getNombre())));
                        break;

                    case "TELEFONO":
                        lista.removeIf(u ->
                                !Arrays.asList(filtros).contains(String.valueOf(u.getTelUsuario())));
                        break;

                    case "EMAIL":
                        lista.removeIf(u ->
                                !Arrays.asList(filtros).contains(u.getEmailUsuario()));
                        break;

                    case "FECHA_REGISTRO":
                        lista.removeIf(u ->
                                !Arrays.stream(filtros)
                                        .anyMatch(f -> f.equalsIgnoreCase(u.getFechaRegistro().format(dtf)))
                        );
                        break;

                    case "ROL":
                        lista.removeIf(u ->
                                !Arrays.stream(filtros)
                                        .anyMatch(f -> f.equalsIgnoreCase(u.getRol().getRol())));
                        break;
                }
            }
        });


//        // --- 4️⃣ FILTRO POR FECHAS (día, datetime o rango) ---
//        String fechaInicioParam = params.get("fecha_inicio");
//        String fechaFinParam = params.get("fecha_fin");
//        String diaCompleto = params.get("diaCompleto");
//
//        // Si no enviaron nada, no filtra fechas
//        if (fechaInicioParam != null || fechaFinParam != null) {
//
//            DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//            DateTimeFormatter formatterDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
//
//            LocalDateTime fechaInicio = null;
//            LocalDateTime fechaFin = null;
//
//            try {
//
//                // ------- FECHA INICIO -------
//                if (fechaInicioParam != null && !fechaInicioParam.isEmpty()) {
//
//                    if (fechaInicioParam.length() == 10) {
//                        // formato solo fecha (YYYY-MM-DD)
//                        fechaInicio = LocalDate.parse(fechaInicioParam, formatterDate).atStartOfDay();
//                    } else {
//                        // formato datetime-local
//                        fechaInicio = LocalDateTime.parse(fechaInicioParam, formatterDateTime);
//                    }
//                }
//
//                // ------- FECHA FIN -------
//                if (fechaFinParam != null && !fechaFinParam.isEmpty()) {
//
//                    if (fechaFinParam.length() == 10) {
//                        fechaFin = LocalDate.parse(fechaFinParam, formatterDate).atTime(23, 59, 59);
//                    } else {
//                        fechaFin = LocalDateTime.parse(fechaFinParam, formatterDateTime);
//                    }
//                }
//
//                // ------- DÍA COMPLETO ------
//                if ("true".equals(diaCompleto)) {
//                    if (fechaInicio != null) fechaInicio = fechaInicio.withHour(0).withMinute(0).withSecond(0);
//                    if (fechaFin != null) fechaFin = fechaFin.withHour(23).withMinute(59).withSecond(59);
//                }
//
//            } catch (Exception e) {
//                System.out.println("Error al parsear fechas: " + e.getMessage());
//            }
//
//            final LocalDateTime fi = fechaInicio;
//            final LocalDateTime ff = fechaFin;
//
//            // ------- APLICAR FILTRO -------
//            lista.removeIf(u -> {
//                LocalDateTime f = u.getFechaRegistro();
//                if (f == null) return true; // si no hay fecha, quitarlo
//
//                if (fi != null && f.isBefore(fi)) return true;
//                if (ff != null && f.isAfter(ff)) return true;
//
//                return false;
//            });
//        }

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
    public void eliminarUsuario(Long id) {
        repository.deleteById(id);
    }
}
