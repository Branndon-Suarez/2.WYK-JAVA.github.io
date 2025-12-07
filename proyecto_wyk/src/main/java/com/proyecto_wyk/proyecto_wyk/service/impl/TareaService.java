package com.proyecto_wyk.proyecto_wyk.service.impl;

import com.proyecto_wyk.proyecto_wyk.entity.Tarea;
import com.proyecto_wyk.proyecto_wyk.entity.Usuario;
import com.proyecto_wyk.proyecto_wyk.repository.TareaRepository;
import com.proyecto_wyk.proyecto_wyk.service.ITareaService;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class TareaService implements ITareaService {
    public final TareaRepository repository;
    public TareaService(TareaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Tarea> listarTarea() {
        return repository.findAll();
    }

//    @Override
//    public List<Tarea> listarTareasFiltrados(Map<String, String> params) {
//        péndiente
//    }

    @Override
    public List<Tarea> listarTareasFiltradas(Map<String, String> params) {

        List<Tarea> lista = repository.findAll();


        String search = params.get("search");
        if (search != null && !search.isEmpty()) {
            String s = search.toLowerCase();

            lista.removeIf(t -> {
                String tarea = t.getTarea() != null ? t.getTarea().toLowerCase() : "";
                String categoria = t.getCategoria() != null ? t.getCategoria().toLowerCase() : "";
                String descripcion = t.getDescripcion() == null ? t.getDescripcion().toLowerCase() : "";
                String tiempoEstimadoHorasStr = t.getTiempoEstimadoHoras() != null ? String.valueOf(t.getTiempoEstimadoHoras()) : "";
                String prioridadStr = t.getPrioridad() != null ?  t.getPrioridad().name().toLowerCase() : "";
                String estadoTareaStr = t.getEstadoTarea() != null ? t.getEstadoTarea().name().toLowerCase() : "";
                String usuarioAsignado = (t.getUsuarioAsignado() != null && t.getUsuarioAsignado().getNombre() != null) ? t.getUsuarioAsignado().getNombre().toLowerCase() : "";
                String usuarioCreador = (t.getUsuarioCreador() != null && t.getUsuarioCreador().getNombre() != null) ? t.getUsuarioCreador().getNombre().toLowerCase() : "";

                return !(
                        tarea.toLowerCase().contains(s)
                                || categoria.contains(s)
                                || descripcion.toLowerCase().contains(s)
                                || tiempoEstimadoHorasStr.contains(s)
                                || prioridadStr.contains(s)
                                || estadoTareaStr.contains(s)
                                || usuarioAsignado.contains(s)
                                || usuarioCreador.contains(s)
                );
            });
        }

        // --- 2️⃣ FILTRO DE ESTADO TAREA (ENUM) ---
        String estadoFiltroStr = params.get("estado");

// 1. Verifica que el parámetro 'estado' exista y no sea 'todos'
// Se usa equalsIgnoreCase para que funcione tanto con "todos" como con "TODOS" o "Todos".
        if (estadoFiltroStr != null && !estadoFiltroStr.equalsIgnoreCase("todos")) {

            try {
                // 2. Convierte el String del parámetro a su constante ENUM.
                // Se usa .toUpperCase() para que "pendiente" se convierta a PENDIENTE (la constante ENUM).
                final Tarea.EstadoTarea estadoDeseado = Tarea.EstadoTarea.valueOf(estadoFiltroStr.toUpperCase());

                // 3. Filtra la lista: remueve las tareas cuyo ESTADO_TAREA no coincida con el estado deseado.
                lista.removeIf(t -> {
                    // El metodo .equals() en ENUMs compara las constantes directamente.
                    // TRUE si NO coinciden, lo que provoca que se eliminen.
                    return !t.getEstadoTarea().equals(estadoDeseado);
                });

            } catch (IllegalArgumentException e) {
                // Manejo de error si el usuario envía un valor que no es PENDIENTE, COMPLETADA, o CANCELADA.
                System.err.println("Valor de estado de tarea inválido para el filtro: " + estadoFiltroStr);
                // La lista permanece sin filtrar por estado en este caso.
            }
        }

        // --- 3️⃣ FILTROS DINÁMICOS (chips) ---
        params.forEach((clave, valor) -> {

            if (clave.startsWith("filtro_") && valor != null && !valor.isEmpty()) {

                String columna = clave.replace("filtro_", "").toUpperCase();
                String[] filtros = valor.split(",");

                switch (columna) {
                    case "TAREA":
                        lista.removeIf(t ->
                                !Arrays.stream(filtros)
                                        .anyMatch(f -> f.equalsIgnoreCase(t.getTarea())));
                        break;

                    case "CATEGORIA":
                        lista.removeIf(t ->
                                !Arrays.stream(filtros)
                                        .anyMatch(f -> f.equalsIgnoreCase(t.getCategoria())));
                        break;

                    case "DESCRIPCION":
                        lista.removeIf(t ->
                                !Arrays.stream(filtros)
                                        .anyMatch(f -> f.equalsIgnoreCase(t.getDescripcion())));
                        break;

                    case "TIEMPO_HORAS":
                        lista.removeIf(t ->
                                !Arrays.asList(filtros).contains(String.valueOf(t.getTiempoEstimadoHoras())));
                        break;

                    case "PRIORIDAD":
                        lista.removeIf(t ->
                                !Arrays.stream(filtros)
                                        .anyMatch(f -> f.equalsIgnoreCase(t.getPrioridad().name()))
                        );
                        break;

                    case "USUARIO_ASIGNADO":
                        lista.removeIf(t ->
                                !Arrays.stream(filtros)
                                        .anyMatch(f -> f.equalsIgnoreCase(t.getUsuarioAsignado().getNombre())));
                        break;

                    case "USUARIO_CREADOR":
                        lista.removeIf(t ->
                                !Arrays.stream(filtros)
                                        .anyMatch(f -> f.equalsIgnoreCase(t.getUsuarioCreador().getNombre())));
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
    public long cantTareasExistentes() {
        return repository.count();
    }

    @Override
    public long cantTareasPendientes() {
        return repository.findAll().stream()
                .filter(t -> t.getEstadoTarea().equals(Tarea.EstadoTarea.PENDIENTE))
                .count();
    }

    @Override
    public long cantTareasCompletada() {
        return repository.findAll().stream()
                .filter(t -> t.getEstadoTarea().equals(Tarea.EstadoTarea.COMPLETADA))
                .count();
    }

    @Override
    public long cantTareasCancelada() {
        return repository.findAll().stream()
                .filter(t -> t.getEstadoTarea().equals(Tarea.EstadoTarea.CANCELADA))
                .count();
    }

    @Override
    public Tarea guardarTarea(Tarea tarea) {
        return repository.save(tarea);
    }

    @Override
    public boolean existeTarea(String nombreTarea) {
        return repository.existsByTarea(nombreTarea);
    }

    @Override
    public Tarea buscarPorID(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void eliminarTarea(Long id) {
        repository.deleteById(id);
    }

}
