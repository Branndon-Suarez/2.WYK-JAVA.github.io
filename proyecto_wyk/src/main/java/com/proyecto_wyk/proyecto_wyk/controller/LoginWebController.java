package com.proyecto_wyk.proyecto_wyk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginWebController {
    @GetMapping("/login")
    public String showLoginPage(
            Model model,
            @RequestParam(value = "error", required = false) String error, // <-- Captura ?error o ?error=disabled
            @RequestParam(value = "logout", required = false) String logout) { // <-- Captura ?logout

        if (error != null) {
            model.addAttribute("loginError", error); // Pasa 'disabled' o 'true' al modelo
        }

        if (logout != null) {
            model.addAttribute("logoutSuccess", true); // Pasa true al modelo
        }

        return "login/login";
    }
}
