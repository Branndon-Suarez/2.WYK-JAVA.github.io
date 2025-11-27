package com.proyecto_wyk.proyecto_wyk.controller;

import com.proyecto_wyk.proyecto_wyk.entity.Rol;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import com.proyecto_wyk.proyecto_wyk.service.impl.RolService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Map;
import java.util.Set;
import java.util.List;

import org.xhtmlrenderer.pdf.ITextRenderer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Controller
@RequestMapping("/roles")
public class RolController {

    public final RolService service;

    // ✔ Validador para activar las @Pattern, @NotBlank del modelo
    private final Validator validator;

    public RolController(RolService service) {
        this.service = service;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("listaRoles", service.listarRol());
        model.addAttribute("rolesExistentes", service.cantRolesExistentes());
        model.addAttribute("rolesActivos", service.cantRolesActivos());
        model.addAttribute("rolesInactivos", service.cantRolesInactivos());
        return "rol/dashboardRol";
    }

    @GetMapping("/formGuardar")
    public String mostrarFormGuardar(Model model) {
        model.addAttribute("formGuardarRol", new Rol());
        return "rol/formGuardar";
    }

    @PostMapping("/guardar")
    @ResponseBody
    public Map<String, Object> guardarRol(
            @RequestParam String rol,
            @RequestParam Rol.Clasificacion clasificacion
    ) {

        // Construimos un objeto para evaluar validaciones
        Rol nuevo = new Rol();
        nuevo.setRol(rol);
        nuevo.setClasificacion(clasificacion);
        nuevo.setEstadoRol(true);

        // ✔ Ejecutar validación automática usando las anotaciones de Rol.java
        Set<ConstraintViolation<Rol>> errores = validator.validate(nuevo);

        if (!errores.isEmpty()) {
            // Retorna el primer mensaje de error de la entidad
            String mensaje = errores.iterator().next().getMessage();

            return Map.of(
                    "success", false,
                    "message", mensaje
            );
        }

        if (service.existeRol(rol)) {
            return Map.of("success", false, "message", "El rol ya existe.");
        }

        service.guardarRol(nuevo);

        return Map.of("success", true, "message", "Rol creado correctamente.");
    }

    @GetMapping("/formAct/{id}")
    public String mostrarFormAct(@PathVariable Integer id, Model model) {
        Rol rol = service.buscarPorId(id);
        model.addAttribute("formActRol", rol);
        return "rol/formActualizar";
    }

    @PostMapping("/actualizar")
    @ResponseBody
    public Map<String, Object> actualizarRol(
            @RequestParam Integer idRol,
            @RequestParam String rol,
            @RequestParam Rol.Clasificacion clasificacion,
            @RequestParam boolean estadoRol
    ) {

        Rol actual = service.buscarPorId(idRol);

        if (actual == null) {
            return Map.of(
                    "success", false,
                    "message", "El rol no existe."
            );
        }

        // Aplicar cambios para validar
        actual.setRol(rol);
        actual.setClasificacion(clasificacion);
        actual.setEstadoRol(estadoRol);

        // ✔ Ejecutar validación automática
        Set<ConstraintViolation<Rol>> errores = validator.validate(actual);

        if (!errores.isEmpty()) {
            String mensaje = errores.iterator().next().getMessage();
            return Map.of("success", false, "message", mensaje);
        }

        if (service.existeRol(rol) && !actual.getRol().equalsIgnoreCase(rol)) {
            return Map.of(
                    "success", false,
                    "message", "Ya existe un rol con ese nombre."
            );
        }

        service.guardarRol(actual);

        return Map.of(
                "success", true,
                "message", "Rol actualizado correctamente."
        );
    }

    @PostMapping("/updateState")
    @ResponseBody
    public Map<String, Object> updateState(@RequestBody Map<String, Object> body) {
        try {
            Integer id = Integer.parseInt(body.get("id").toString());
            Integer estado = Integer.parseInt(body.get("estado").toString());

            boolean nuevoEstado = (estado == 1);

            Rol rol = service.buscarPorId(id);

            if (rol == null) {
                return Map.of(
                        "success", false,
                        "message", "El rol no existe."
                );
            }

            rol.setEstadoRol(nuevoEstado);
            service.guardarRol(rol);

            return Map.of(
                    "success", true,
                    "message", "Estado actualizado correctamente.",
                    "estadoNuevo", nuevoEstado
            );

        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "Error al actualizar el estado: " + e.getMessage()
            );
        }
    }

    @PostMapping("/delete")
    @ResponseBody
    public Map<String, Object> eliminarRol(@RequestParam Integer id) {
        try {
            service.eliminarRol(id);

            return Map.of(
                    "success", true,
                    "message", "Rol eliminado correctamente."
            );

        } catch (DataIntegrityViolationException e) {
            return Map.of(
                    "success", false,
                    "code", "FK_CONSTRAINT",
                    "message", "No es posible eliminar este rol porque está siendo usado por otros registros."
            );

        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "Error al eliminar el rol: " + e.getMessage()
            );
        }
    }

    @GetMapping("/generateReportPDF")
    public ResponseEntity<InputStreamResource> generarPDF(@RequestParam Map<String, String> params) throws Exception {

        List<Rol> listaRoles = service.listarRolesFiltrados(params);

        StringBuilder html = new StringBuilder();
        html.append("""
        <html>
        <head>
        <style>
            table { width:100%; border-collapse: collapse; }
            th, td { border:1px solid #ccc; padding:8px; }
            th { background-color:#f2f2f2; }
        </style>
        </head>
        <body>
        <h2>Reporte de Roles</h2>
        <table>
            <thead>
                <tr><th>Rol</th><th>Clasificación</th><th>Estado</th></tr>
            </thead>
            <tbody>
    """);

        for (Rol r : listaRoles) {
            html.append("<tr>")
                    .append("<td>").append(r.getRol()).append("</td>")
                    .append("<td>").append(r.getClasificacion()).append("</td>")
                    .append("<td>").append(r.getEstadoRol() ? "Activo" : "Inactivo").append("</td>")
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
        headers.add("Content-Disposition", "inline; filename=roles.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdfStream));
    }
}
