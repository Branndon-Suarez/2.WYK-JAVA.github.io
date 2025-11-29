package com.proyecto_wyk.proyecto_wyk.controller;

import com.proyecto_wyk.proyecto_wyk.dto.rol.RolCreateDTO;
import com.proyecto_wyk.proyecto_wyk.dto.rol.RolUpdateDTO;
import com.proyecto_wyk.proyecto_wyk.entity.Rol;
import jakarta.validation.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import com.proyecto_wyk.proyecto_wyk.service.impl.RolService;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

    // Esto es para listar los roles en otras interfaces en forma de tabla modal en formularios de crear y actualizar
    @GetMapping("/listarRolesModal")
    @ResponseBody
    public Map<String, Object> listarRolesModal() {
        try {
            List<Rol> listarRol = service.listarRol();

            return Map.of(
                    "success", true,
                    "data", listarRol
            );
        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "Error al listar los roles para el modal: " + e.getMessage()
            );
        }
    }

    @GetMapping("/formGuardar")
    public String mostrarFormGuardar(Model model) {
        model.addAttribute("formGuardarRol", new Rol());
        return "rol/formGuardar";
    }

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
    // '@RequestParam' anotación para extraer los datos URL de una solicitud http (GET/POST) y vincularlos directamente a los argumentos de un método contorlador java
    public Map<String, Object> guardarRol(
            @Valid @RequestBody RolCreateDTO dto,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            String mensaje = result.getFieldError().getDefaultMessage();
            return Map.of(
                    "success", false,
                    "message", mensaje
            );
        }

        if (service.existeRol(dto.getRol())) {
            return Map.of(
                    "success", false,
                    "message", "El rol ya existe.");
        }

        Rol nuevoRol = new Rol();
        nuevoRol.setRol(dto.getRol());
        nuevoRol.setClasificacion(dto.getClasificacion());
        nuevoRol.setEstadoRol(true);

        service.guardarRol(nuevoRol);

        return Map.of(
                "success", true,
                "message", "Rol creado correctamente.");
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
            @Valid @RequestBody RolUpdateDTO dto,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            String mensaje = result.getFieldError().getDefaultMessage();
            return Map.of(
                    "success", false,
                    "message", mensaje
            );
        }

        Rol actualRol = service.buscarPorId(dto.getIdRol());

        if (actualRol == null) {
            return Map.of(
                    "success", false,
                    "message", "El rol no existe."
            );
        }

        if (service.existeRol(dto.getRol()) && !actualRol.getRol().equalsIgnoreCase(dto.getRol())) {
            return Map.of(
                    "success", false,
                    "message", "Ya existe un rol con ese nombre."
            );
        }

        actualRol.setRol(dto.getRol());
        actualRol.setClasificacion(dto.getClasificacion());
        actualRol.setEstadoRol(dto.getEstadoRol());

        service.guardarRol(actualRol);

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
