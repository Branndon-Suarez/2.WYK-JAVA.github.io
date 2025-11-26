package com.proyecto_wyk.proyecto_wyk.controller;

import com.proyecto_wyk.proyecto_wyk.entity.Rol;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import com.proyecto_wyk.proyecto_wyk.service.impl.RolService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

// IMPORTS para generar PDF con openhtmltopdf (moderno)
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

    public RolController(RolService service) {
        this.service = service;
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

    /* Forma tradicional
    @PostMapping("/guardar")
    public String guardarRol(@ModelAttribute Rol rol) {
        service.guardarRol(rol);
        return "redirect:/roles/home";
    }*/

    // Forma para devolver JSON y hacer AJAX
    @PostMapping("/guardar")
    /* '@ResponseBody'
        - ¿Qué es?
         Anotación para indicar que el valor de retorno de un metodo debe ser serializado/convertido directamente y enviado como el cuerpo de la respuesta HTTP,
         NO como una vista (que por defecto es de la carpeta template).
        - Jackson
         Biblioteca Java para realizar la serialización y deserialización de objetos, convirtiendo JSON a objeto y viceversa.
        - Serialización
         Es el proceso de convertir un objeto a un formato que se pueda almacenar o transmitir fácilmente.
        - Cuerpo de respuesta HTTP
         Es el contenido de la comunicación entre el cliente y el servidor que puede ser en formato JSON, HTML, etc.
        - Es decir, permite en este caso devolver JSON que es a su vez el cuerpo de la respuesta HTTP.*/
    @ResponseBody
    /* Se usa Map<String, Object> usa:
        - String: Porque la clave de un JSON siempre tiene la clave en String.
        - Object: Porque el valor que queremos almacenar para los registros puede ser de más de un tipo de dato.*/
    public Map<String, Object> guardarRol(
            // '@RequestParam' anotación para extraer los datos URL de una solicitud http (GET/POST) y vincularlos directamente a los argumentos de un método contorlador java
            @RequestParam String rol,
            @RequestParam Rol.Clasificacion clasificacion
    ) {
        Rol nuevo = new Rol();
        nuevo.setRol(rol);
        nuevo.setClasificacion(clasificacion);
        nuevo.setEstadoRol(true);

        // Map convertido a JSON con Jackson
        if (service.existeRol(rol)) {
            return Map.of("success", false, "message", "El rol ya existe.");
        }

        service.guardarRol(nuevo);

        // Map convertido a JSON con Jackson
        return Map.of("success", true, "message", "Rol creado correctamente.");
    }

    @GetMapping("/formAct/{id}")
    public String mostrarFormAct(@PathVariable Integer id, Model model) {
        Rol rol = service.buscarPorId(id);
        model.addAttribute("formActRol", rol);
        return "rol/formActualizar";
    }

    /*Forma tradicional
    @PostMapping("/actualizar")
    public String actualizarRol(@ModelAttribute Rol rol) {
        service.guardarRol(rol);
        return "redirect:/roles/home";
    }*/
    @PostMapping("/actualizar")
    @ResponseBody
    public Map<String, Object> actualizarRol(
            @RequestParam Integer idRol,
            @RequestParam String rol,
            @RequestParam Rol.Clasificacion clasificacion,
            @RequestParam boolean estadoRol
    ) {
        // Buscar si el rol existe
        Rol actual = service.buscarPorId(idRol);

        if (actual == null) {
            return Map.of(
                    "success", false,
                    "message", "El rol no existe."
            );
        }

        // Validar si el nuevo nombre ya existe (y no es el mismo rol)
        if (service.existeRol(rol) && !actual.getRol().equalsIgnoreCase(rol)) {
            return Map.of(
                    "success", false,
                    "message", "Ya existe un rol con ese nombre."
            );
        }

        // Actualizar valores
        actual.setRol(rol);
        actual.setClasificacion(clasificacion);
        actual.setEstadoRol(estadoRol);

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

    /* Forma Tradicional
    @GetMapping("/eliminar/{id}")
    public String eliminarRol(@PathVariable Integer id) {
        service.eliminarRol(id);
        return "redirect:/roles/home";
    }*/
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
