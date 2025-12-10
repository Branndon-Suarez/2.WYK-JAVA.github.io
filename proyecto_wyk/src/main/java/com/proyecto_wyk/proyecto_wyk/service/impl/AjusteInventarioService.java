package com.proyecto_wyk.proyecto_wyk.service.impl;

import com.proyecto_wyk.proyecto_wyk.entity.AjusteInventario;
import com.proyecto_wyk.proyecto_wyk.repository.AjusteInventarioRepository; // Nombre corregido
import com.proyecto_wyk.proyecto_wyk.service.IAjusteInventarioService;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class AjusteInventarioService implements IAjusteInventarioService {

    public final AjusteInventarioRepository repository; // Nombre corregido

    public AjusteInventarioService(AjusteInventarioRepository repository) {
        this.repository = repository;
    }

    // --- MÉTODOS CRUD BÁSICOS ---

    @Override
    public List<AjusteInventario> listarAjustes() {
        return repository.findAll();
    }

    @Override
    public AjusteInventario guardarAjuste(AjusteInventario ajuste) {
        return repository.save(ajuste);
    }

    @Override
    public AjusteInventario buscarPorID(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void eliminarAjuste(Integer id) {
        repository.deleteById(id);
    }

    // --- MÉTODOS DE CONTEO Y REPORTE ---

    @Override
    public long contarAjustesExistentes() {
        return repository.count();
    }

    // Método auxiliar privado para la lógica de conteo
    private long contarPorTipo(AjusteInventario.TipoAjuste tipo) {
        return repository.findAll().stream()
                .filter(a -> a.getTipoAjuste() != null && a.getTipoAjuste().equals(tipo))
                .count();
    }

    @Override
    public long cantAjustesDañados() {
        return contarPorTipo(AjusteInventario.TipoAjuste.DAÑADO);
    }

    @Override
    public long cantAjustesRobo() {
        return contarPorTipo(AjusteInventario.TipoAjuste.ROBO);
    }

    @Override
    public long cantAjustesPerdida() {
        return contarPorTipo(AjusteInventario.TipoAjuste.PERDIDA);
    }

    @Override
    public long cantAjustesCaducados() {
        return contarPorTipo(AjusteInventario.TipoAjuste.CADUCADO);
    }

    @Override
    public long cantAjustesMuestra() {
        return contarPorTipo(AjusteInventario.TipoAjuste.MUESTRA);
    }


    // --- MÉTODO PRINCIPAL DE FILTRADO ---

    @Override
    public List<AjusteInventario> listarAjustesFiltrados(Map<String, String> params) {

        List<AjusteInventario> lista = repository.findAll();

        // --- 1️⃣ FILTRO DE BÚSQUEDA GENERAL ('search') ---
        String search = params.get("search");
        if (search != null && !search.isEmpty()) {
            String s = search.toLowerCase();

            lista.removeIf(a -> {
                String fechaAjusteStr = a.getFechaAjuste() != null ? a.getFechaAjuste().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "";
                String tipoAjusteStr = a.getTipoAjuste() != null ? a.getTipoAjuste().name().toLowerCase() : "";
                String cantidadAjustadaStr = a.getCantidadAjustada() != null ? String.valueOf(a.getCantidadAjustada()) : "";
                String descripcion = a.getDescripcion() != null ? a.getDescripcion().toLowerCase() : "";
                String nombreProducto = (a.getProducto() != null && a.getProducto().getNombreProducto() != null) ? a.getProducto().getNombreProducto().toLowerCase() : "";
                String nombreUsuario = (a.getUsuario() != null && a.getUsuario().getNombre() != null) ? a.getUsuario().getNombre().toLowerCase() : "";

                return !(
                        fechaAjusteStr.contains(s)
                                || tipoAjusteStr.contains(s)
                                || cantidadAjustadaStr.contains(s)
                                || descripcion.contains(s)
                                || nombreProducto.contains(s)
                                || nombreUsuario.contains(s)
                );
            });
        }


        // --- 2️⃣ FILTRO DE TIPO DE AJUSTE (ENUM) ---
        String tipoFiltroStr = params.get("tipoAjuste");

        if (tipoFiltroStr != null && !tipoFiltroStr.equalsIgnoreCase("todos")) {

            try {
                final AjusteInventario.TipoAjuste tipoDeseado = AjusteInventario.TipoAjuste.valueOf(tipoFiltroStr.toUpperCase());

                lista.removeIf(a -> {
                    return a.getTipoAjuste() == null || !a.getTipoAjuste().equals(tipoDeseado);
                });

            } catch (IllegalArgumentException e) {
                System.err.println("Valor de tipo de ajuste inválido para el filtro: " + tipoFiltroStr);
            }
        }

        // --- 3️⃣ FILTROS DINÁMICOS (chips) ---
        params.forEach((clave, valor) -> {

            if (clave.startsWith("filtro_") && valor != null && !valor.isEmpty()) {

                String columna = clave.replace("filtro_", "").toUpperCase();
                String[] filtros = valor.split(",");

                switch (columna) {
                    case "TIPO_AJUSTE":
                        lista.removeIf(a -> a.getTipoAjuste() == null ||
                                !Arrays.stream(filtros)
                                        .anyMatch(f -> f.equalsIgnoreCase(a.getTipoAjuste().name()))
                        );
                        break;

                    case "CANTIDAD":
                        lista.removeIf(a ->
                                !Arrays.asList(filtros).contains(String.valueOf(a.getCantidadAjustada())));
                        break;

                    case "DESCRIPCION":
                        lista.removeIf(a ->
                                !Arrays.stream(filtros)
                                        .anyMatch(f -> a.getDescripcion() != null && f.equalsIgnoreCase(a.getDescripcion())));
                        break;

                    case "PRODUCTO":
                        lista.removeIf(a ->
                                !Arrays.stream(filtros)
                                        .anyMatch(f -> a.getProducto() != null && a.getProducto().getNombreProducto() != null && f.equalsIgnoreCase(a.getProducto().getNombreProducto())));
                        break;

                    case "USUARIO":
                        lista.removeIf(a ->
                                !Arrays.stream(filtros)
                                        .anyMatch(f -> a.getUsuario() != null && a.getUsuario().getNombre() != null && f.equalsIgnoreCase(a.getUsuario().getNombre())));
                        break;
                }
            }
        });

        return lista;
    }
}