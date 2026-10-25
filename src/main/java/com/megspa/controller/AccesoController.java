package com.megspa.controller;

import com.megspa.service.RegistroService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AccesoController {

    private final RegistroService registroService;

    public AccesoController(RegistroService registroService) {
        this.registroService = registroService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) String telefono,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            registroService.registrarCliente(
                    nombre, email, password, telefono);

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Cuenta creada. Ya podés iniciar sesión.");

            return "redirect:/login?registrado";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "registro";
        }
    }
}