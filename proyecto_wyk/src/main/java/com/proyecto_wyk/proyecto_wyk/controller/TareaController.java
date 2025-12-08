package com.proyecto_wyk.proyecto_wyk.controller;

import com.proyecto_wyk.proyecto_wyk.dto.Tarea.TareaCreateDTO;
import com.proyecto_wyk.proyecto_wyk.dto.Tarea.TareaUpdateDTO;
import com.proyecto_wyk.proyecto_wyk.dto.usuario.UsuarioCreateDTO;
import com.proyecto_wyk.proyecto_wyk.dto.usuario.UsuarioUpdateDTO;
import com.proyecto_wyk.proyecto_wyk.entity.Rol;
import com.proyecto_wyk.proyecto_wyk.entity.Tarea;
import com.proyecto_wyk.proyecto_wyk.entity.Usuario;
import com.proyecto_wyk.proyecto_wyk.security.CustomUserDetails;
import com.proyecto_wyk.proyecto_wyk.service.impl.TareaService;
import com.proyecto_wyk.proyecto_wyk.service.impl.UsuarioService;
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
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/tareas")
public class TareaController {
    private final TareaService tareaService;
    private final UsuarioService usuarioService;

    public TareaController(TareaService tareaService, UsuarioService usuarioService) {
        this.tareaService = tareaService;
        this.usuarioService = usuarioService;
    }


    @GetMapping
    public String listar(Model model) {
        model.addAttribute("listaTareas", tareaService.listarTarea());
        model.addAttribute("tareasExistentes", tareaService.cantTareasExistentes());
        model.addAttribute("tareasPendientes", tareaService.cantTareasPendientes());
        model.addAttribute("tareasCompletadas", tareaService.cantTareasCompletada());
        model.addAttribute("tareasCanceladas", tareaService.cantTareasCancelada());

        return "tarea/dashboardTarea";
    }

    @GetMapping("/formGuardar")
    public String mostrarFormGuardar(Model model) {
        model.addAttribute("formGuardarTarea", new TareaCreateDTO());
        model.addAttribute("listaUsuraio", usuarioService.listarUsuario());

        return "tarea/formGuardar";
    }

    @PostMapping("/guardar")
    @ResponseBody
    public Map<String, Object> guardarTarea(
            @Valid @RequestBody TareaCreateDTO dto,
            BindingResult result
    ) {
        // --- 1. OBTENER EL USUARIO CREADOR LOGUEADO ---

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioCreador = null;

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            usuarioCreador = userDetails.getUsuario();
        }

        if (usuarioCreador == null) {
            // Esto significa que el usuario no está correctamente autenticado.
            return Map.of(
                    "success", false,
                    "message", "Error de autenticación. El usuario creador no pudo ser identificado."
            );
        }

        if (result.hasErrors()) {
            String mensaje = result.getFieldErrors().stream()
                    .filter(e -> e.getCode().equals("NotBlank")) // 1. Busca si existe un error de @NotBlank para darle prioridad
                    .findFirst()
                    .orElse(result.getFieldError())// 2. Si no es @NotBlank, toma el primer error que encuentre
                    .getDefaultMessage();
            return Map.of(
                    "success", false,
                    "message", mensaje
            );
        }

        if (tareaService.existeTarea(dto.getTarea())) {
            return Map.of(
                    "success", false,
                    "message", "Ya existe esa tarea dentro del sistema."
            );
        }

        // Conversión de String a Enum y try catch para manejar atacante e impedir no enviar datos que no estén en el enum.
        Tarea.Prioridad prioridadEnum;
        try {
            prioridadEnum = Tarea.Prioridad.valueOf(dto.getPrioridad().toUpperCase()); // Asegurar mayúsculas
        } catch (IllegalArgumentException e) {
            return Map.of(
                    "success", false,
                    "message", "Prioridad no válida. Por favor, selecciona un valor de la lista."
            );
        }

        Tarea nuevaTarea = new Tarea();
        nuevaTarea.setTarea(dto.getTarea());
        nuevaTarea.setCategoria(dto.getCategoria());
        nuevaTarea.setDescripcion(dto.getDescripcion());
        nuevaTarea.setTiempoEstimadoHoras(dto.getTiempoEstimadoHoras());
        nuevaTarea.setPrioridad(prioridadEnum);
        nuevaTarea.setEstadoTarea(Tarea.EstadoTarea.PENDIENTE);

        // --- ASIGNAR USUARIO ASIGNADO ---
        Usuario usuarioAsignado = usuarioService.buscarPorID(Long.valueOf(dto.getIdUsuarioAsignado()));
        if (usuarioAsignado == null) {
            return Map.of(
                    "success", false,
                    "message", "El usuario asignado no existe.");
        }
        nuevaTarea.setUsuarioAsignado(usuarioAsignado);

        // --- ASIGNAR USUARIO CREADOR (FK: Usuario Logueado) ---
        nuevaTarea.setUsuarioCreador(usuarioCreador);

        tareaService.guardarTarea(nuevaTarea);

        return Map.of(
                "success", true,
                "message", "Tarea registrada correctamente."
        );
    }
    @GetMapping("/formAct/{id}")
    public String mostrarFormActualizar(@PathVariable Long id, Model model) {

        Tarea tarea = tareaService.buscarPorID(id);

        model.addAttribute("formActTarea", tarea);
        model.addAttribute("listaUsurarios", usuarioService.listarUsuario());

        return "tarea/formActualizar";
    }


    @PostMapping("/actualizar")
    @ResponseBody
    public Map<String, Object> actualizarTarea(
            @Valid @RequestBody TareaUpdateDTO dto,
            BindingResult result
    ) {
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

        Tarea actualTarea = tareaService.buscarPorID(dto.getIdTarea());

        if (actualTarea == null) {
            return Map.of(
                    "success", false,
                    "message", "La tarea no existe."
            );
        }

        // validar documento duplicado
        if (!actualTarea.getTarea().equalsIgnoreCase(dto.getTarea())) { // SOLO si el nombre cambió...

            // ...verifica si el nuevo nombre ya existe en el sistema
            if (tareaService.existeTarea(dto.getTarea())) {
                return Map.of(
                        "success", false,
                        "message", "Ya existe esa tarea dentro del sistema."
                );
            }
        }
// Si el nombre no cambió, o si cambió pero no existe, el flujo de actualización continúa.


        actualTarea.setTarea(dto.getTarea());
        actualTarea.setCategoria(dto.getCategoria());
        actualTarea.setDescripcion(dto.getDescripcion());
        actualTarea.setTiempoEstimadoHoras(dto.getTiempoEstimadoHoras());
        actualTarea.setPrioridad(dto.getPrioridad());
        actualTarea.setEstadoTarea(dto.getEstadoTarea());

        // --- ASIGNAR USUARIO CORRECTAMENTE ---
        Usuario usuario = usuarioService.buscarPorID(Long.valueOf(dto.getIdUsuarioAsignado()));
        if (usuario == null) {
            return Map.of(
                    "success", false,
                    "message", "El usuario seleccionado no existe.");
        }
        actualTarea.setUsuarioAsignado(usuario);

        tareaService.guardarTarea(actualTarea);

        return Map.of(
                "success", true,
                "message", "Tarea actualizado correctamente."
        );
    }

    @PostMapping("/delete")
    @ResponseBody
    public Map<String, Object> eliminarTarea(@RequestParam Long id) {
        try {
            tareaService.eliminarTarea(id);

            return Map.of(
                    "success", true,
                    "message", "Tarea eliminado correctamente."
            );

        } catch (DataIntegrityViolationException e) {
            return Map.of(
                    "success", false,
                    "code", "FK_CONSTRAINT",
                    "message", "No es posible eliminar esta tarea porque está relacionda con otros registros."
            );

        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "Error al eliminar la tarea: " + e.getMessage()
            );
        }
    }

    @GetMapping("/generateReportPDF")
    public ResponseEntity<InputStreamResource> generarPDF(@RequestParam Map<String, String> params) throws Exception {

        List<Tarea> listaTarea = tareaService.listarTareasFiltradas(params);

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
                    <h2>Reporte de Tareas</h2>
                    <table>
                        <thead>
                            <tr>
                                <th>Tarea</th>
                                <th>Categoria</th>
                                <th>Descripción</th>
                                <th>Tiempo Estimado Horas</th>
                                <th>Prioridad</th>
                                <th>Estado Tarea</th>
                                <th>Usuario Asignado</th>
                                <th>Usuario Creador</th>
                            </tr>
                        </thead>
                        <tbody>
                """);

        for (Tarea t : listaTarea) {
            html.append("<tr>")
                    .append("<td>").append(t.getTarea()).append("</td>")
                    .append("<td>").append(t.getCategoria()).append("</td>")
                    .append("<td>").append(t.getDescripcion()).append("</td>")
                    .append("<td>").append(t.getTiempoEstimadoHoras()).append("</td>")
                    .append("<td>").append(t.getPrioridad()).append("</td>")
                    .append("<td>").append(t.getEstadoTarea()).append("</td>")
                    .append("<td>").append(t.getUsuarioAsignado().getNombre()).append("</td>")
                    .append("<td>").append(t.getUsuarioCreador().getNombre()).append("</td>")
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
        headers.add("Content-Disposition", "inline; filename=usuarios.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdfStream));
    }


//    @PostMapping("/actualizar")
//    @ResponseBody
//    public Map<String, Object> actualizarTarea(
//            @Valid @RequestBody TareaUpdateDTO dto,
//            BindingResult result
//    ) {
//        if (result.hasErrors()) {
//            String mensaje = result.getFieldErrors().stream()
//                    .filter(e -> e.getCode().equals("NotBlank"))
//                    .findFirst()
//                    .orElse(result.getFieldError())
//                    .getDefaultMessage();
//            return Map.of(
//                    "success", false,
//                    "message", mensaje
//            );
//        }
//
//        Tarea actualTarea = tareaService.buscarPorID(dto.getIdTarea());
//
//        if (actualTarea == null) {
//            return Map.of(
//                    "success", false,
//                    "message", "El usuario no existe."
//            );
//        }
//
//        // validar documento duplicado
//        if (tareaService.existeTarea(dto.getTarea())
//                && !actualTarea.getTarea().equals(dto.getTarea())) {
//
//            return Map.of(
//                    "success", false,
//                    "message", "Ya existe un usuario con ese número de documento."
//            );
//        }
//
//        actualTarea.setIdTarea(Long.valueOf(dto.getIdTarea()));
//        actualTarea.setTarea(dto.getTarea());
//        actualTarea.setCategoria(dto.getCategoria());
//        actualTarea.setDescripcion(dto.getDescripcion());
//        actualTarea.setTiempoEstimadoHoras(dto.getTiempoEstimadoHoras());
//        actualTarea.setPrioridad(dto.getPrioridad());
//        actualTarea.setEstadoTarea(dto.getEstadoTarea());
//
//        // --- ASIGNAR USUARIO CORRECTAMENTE ---
//        Usuario usuario = usuarioService.buscarPorID(Long.valueOf(dto.getIdUsuarioAsignado()));
//        if (usuario == null) {
//            return Map.of(
//                    "success", false,
//                    "message", "El rol seleccionado no existe.");
//        }
//        actualTarea.setNombre(usuario);
//
//        tareaService.guardarTarea(actualTarea);
//
//        return Map.of(
//                "success", true,
//                "message", "Usuario actualizado correctamente."
//        );
//    }

}
