package com.megspa.controller;

import com.megspa.service.MasajeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/masajes")
public class MasajeController {

    private final MasajeService masajeService;

    public MasajeController(MasajeService masajeService) {
        this.masajeService = masajeService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute(
                "masajes",
                masajeService.listarTratamientosActivos()
        );

        return "masajes";
    }
}