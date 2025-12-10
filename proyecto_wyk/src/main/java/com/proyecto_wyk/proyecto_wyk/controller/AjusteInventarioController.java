package com.proyecto_wyk.proyecto_wyk.controller;

import com.proyecto_wyk.proyecto_wyk.dto.AjusteInventario.AjusteInventarioCreateDTO;
import com.proyecto_wyk.proyecto_wyk.entity.AjusteInventario;
import com.proyecto_wyk.proyecto_wyk.entity.Producto;
import com.proyecto_wyk.proyecto_wyk.entity.Usuario;
import com.proyecto_wyk.proyecto_wyk.security.CustomUserDetails;
import com.proyecto_wyk.proyecto_wyk.service.IAjusteInventarioService;
import com.proyecto_wyk.proyecto_wyk.service.IUsuarioService;
import com.proyecto_wyk.proyecto_wyk.service.impl.ProductoService; // Usamos la clase concreta ProductoService
import jakarta.validation.Valid;

import org.springframework.core.io.InputStreamResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/ajustesInventario")
public class AjusteInventarioController {

    private final IAjusteInventarioService ajusteService;
    private final IUsuarioService usuarioService;
    private final ProductoService productoService; // Inyección de ProductoService

    public AjusteInventarioController(
            IAjusteInventarioService ajusteService,
            IUsuarioService usuarioService,
            ProductoService productoService
    ) {
        this.ajusteService = ajusteService;
        this.usuarioService = usuarioService;
        this.productoService = productoService;
    }

    // --- 1. LISTADO Y DASHBOARD ---

    @GetMapping
    public String listar(Model model, @RequestParam Map<String, String> params) {

        // Cargar datos filtrados o todos
        model.addAttribute("listaAjustes", ajusteService.listarAjustesFiltrados(params));

        // Cargar métricas de conteo (adaptado de IAjusteInventarioService)
        model.addAttribute("ajustesExistentes", ajusteService.contarAjustesExistentes());
        model.addAttribute("ajustesDanados", ajusteService.cantAjustesDanados());
        model.addAttribute("ajustesRobo", ajusteService.cantAjustesRobo());
        model.addAttribute("ajustesPerdida", ajusteService.cantAjustesPerdida());
        model.addAttribute("ajustesCaducados", ajusteService.cantAjustesCaducados());
        model.addAttribute("ajustesMuestra", ajusteService.cantAjustesMuestra());

        model.addAttribute("listaUsuarios", usuarioService.listarUsuario());

        return "ajusteInventario/dashboardAjuste"; // Asume la ruta de la vista
    }

    // --- 2. FORMULARIO DE GUARDADO ---

    @GetMapping("/formGuardar")
    public String mostrarFormGuardar(Model model) {
        model.addAttribute("formGuardarAjuste", new AjusteInventarioCreateDTO());
        model.addAttribute("listaUsuarios", usuarioService.listarUsuario());
        model.addAttribute("listaProductos", productoService.listarProducto()); // Lista de productos para el selector

        return "ajuste/formGuardar"; // Asume la ruta de la vista
    }

    // --- 3. GUARDADO (CREATE) ---

    @PostMapping("/guardar")
    @ResponseBody
    public Map<String, Object> guardarAjuste(
            @Valid @RequestBody AjusteInventarioCreateDTO dto,
            BindingResult result
    ) {
        // Validación de DTO
        if (result.hasErrors()) {
            String mensaje = result.getFieldErrors().stream()
                    .filter(e -> e.getCode().equals("NotBlank"))
                    .findFirst()
                    .orElse(result.getFieldError())
                    .getDefaultMessage();
            return Map.of(
                    "success", false,
                    "message", mensaje
            );
        }

        // --- BÚSQUEDA Y VALIDACIÓN DE CLAVES FORÁNEAS ---

        // 1. Conversión de String a Enum
        AjusteInventario.TipoAjuste tipoAjusteEnum;
        try {
            tipoAjusteEnum = AjusteInventario.TipoAjuste.valueOf(dto.getTipoAjuste().toUpperCase());
        } catch (IllegalArgumentException e) {
            return Map.of(
                    "success", false,
                    "message", "Tipo de ajuste no válido. Por favor, selecciona un valor de la lista."
            );
        }

        // 2. Buscar Producto (usando findById de ProductoService)
        Producto productoAfectado = productoService.findById(dto.getIdProducto());
        if (productoAfectado == null) {
            return Map.of(
                    "success", false,
                    "message", "El producto al que se aplica el ajuste no existe.");
        }

        // 3. Buscar Usuario (usando buscarPorID de IUsuarioService)
        Usuario usuarioRegistra = usuarioService.buscarPorID(Long.valueOf(dto.getIdUsuario()));
        if (usuarioRegistra == null) {
            return Map.of(
                    "success", false,
                    "message", "El usuario que registra el ajuste no existe.");
        }

        // --- 4. MAPEO Y GUARDADO ---
        AjusteInventario nuevoAjuste = new AjusteInventario();
        nuevoAjuste.setFechaAjuste(dto.getFechaAjuste() != null ? dto.getFechaAjuste() : LocalDateTime.now());
        nuevoAjuste.setTipoAjuste(tipoAjusteEnum);
        nuevoAjuste.setCantidadAjustada(dto.getCantidadAjustada());
        nuevoAjuste.setDescripcion(dto.getDescripcion());

        nuevoAjuste.setProducto(productoAfectado);
        nuevoAjuste.setUsuario(usuarioRegistra);

        ajusteService.guardarAjuste(nuevoAjuste);

        return Map.of(
                "success", true,
                "message", "Ajuste de inventario registrado correctamente."
        );
    }

    // NOTA: Se omite el /formAct/{id} y /actualizar por falta de AjusteInventarioUpdateDTO

    // --- 4. ELIMINACIÓN ---

    @PostMapping("/delete")
    @ResponseBody
    public Map<String, Object> eliminarAjuste(@RequestParam Integer id) { // ID es Integer
        try {
            ajusteService.eliminarAjuste(id);

            return Map.of(
                    "success", true,
                    "message", "Ajuste de inventario eliminado correctamente."
            );

        } catch (DataIntegrityViolationException e) {
            return Map.of(
                    "success", false,
                    "code", "FK_CONSTRAINT",
                    "message", "No es posible eliminar este ajuste porque está relacionado con otros registros."
            );

        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "Error al eliminar el ajuste: " + e.getMessage()
            );
        }
    }

    // --- 5. GENERACIÓN DE PDF ---

    @GetMapping("/generateReportPDF")
    public ResponseEntity<InputStreamResource> generarPDF(@RequestParam Map<String, String> params) throws Exception {

        List<AjusteInventario> listaAjustes = ajusteService.listarAjustesFiltrados(params);

        StringBuilder html = new StringBuilder();
        html.append("""
                    <html>
                    <head>
                    <style>
                        @page {
                            size: A4 landscape;
                            margin: 20mm;
                        }
                        table { width:100%; border-collapse: collapse; }
                        th, td { border:1px solid #ccc; padding:8px; }
                        th { background-color:#f2f2f2; }
                    </style>
                    </head>
                    <body>
                    <h2>Reporte de Ajustes de Inventario</h2>
                    <table>
                        <thead>
                            <tr>
                                <th>ID Ajuste</th>
                                <th>Fecha Ajuste</th>
                                <th>Tipo Ajuste</th>
                                <th>Cantidad Ajustada</th>
                                <th>Producto</th>
                                <th>Usuario Registro</th>
                                <th>Descripción</th>
                            </tr>
                        </thead>
                        <tbody>
                """);

        for (AjusteInventario a : listaAjustes) {
            html.append("<tr>")
                    .append("<td>").append(a.getIdAjuste()).append("</td>")
                    .append("<td>").append(a.getFechaAjuste()).append("</td>")
                    .append("<td>").append(a.getTipoAjuste()).append("</td>")
                    .append("<td>").append(a.getCantidadAjustada()).append("</td>")
                    .append("<td>").append(a.getProducto() != null ? a.getProducto().getNombreProducto() : "N/A").append("</td>")
                    .append("<td>").append(a.getUsuario() != null ? a.getUsuario().getNombre() : "N/A").append("</td>")
                    .append("<td>").append(a.getDescripcion() != null ? a.getDescripcion() : "").append("</td>")
                    .append("</tr>");
        }

        html.append("""
                    </tbody>
                    </table>
                    </body>
                    </html>
                """);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html.toString());
        renderer.layout();
        renderer.createPDF(os);

        ByteArrayInputStream pdfStream = new ByteArrayInputStream(os.toByteArray());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=ajustes_inventario.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdfStream));
    }
}