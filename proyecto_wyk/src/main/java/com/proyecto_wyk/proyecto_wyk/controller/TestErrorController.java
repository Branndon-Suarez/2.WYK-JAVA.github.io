package com.proyecto_wyk.proyecto_wyk.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestErrorController {
    /**
     * Endpoint que lanza una RuntimeException para forzar un error 500.
     */
    @GetMapping("/test-500")
    public String dispararError500() {
        // Mensaje de la excepción que aparecerá en los logs del servidor
        throw new RuntimeException("¡ERROR: Excepción interna forzada para probar la página 500!");
    }
}
