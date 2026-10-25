package com.megspa.controller;

import com.megspa.model.EspecialidadMasajista;
import com.megspa.model.Masajista;
import com.megspa.model.TipoMasaje;
import com.megspa.service.MasajeService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/especialidades")
public class AdminEspecialidadController {

    private final MasajeService masajeService;

    public AdminEspecialidadController(MasajeService masajeService) {
        this.masajeService = masajeService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute(
                "especialidades",
                masajeService.listarTodasLasEspecialidades());
        model.addAttribute(
                "masajistas",
                masajeService.listarMasajistasActivos());
        model.addAttribute(
                "tratamientos",
                masajeService.listarTratamientosActivos());

        return "admin/especialidades";
    }

    @PostMapping("/asignar")
    public String asignar(
            @RequestParam Long masajistaId,
            @RequestParam Long tipoMasajeId,
            RedirectAttributes redirectAttributes) {

        try {
            masajeService.asignarEspecialidad(
                    masajistaId, tipoMasajeId);
            redirectAttributes.addFlashAttribute(
                    "mensaje", "Especialidad asignada correctamente.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute(
                    "error", ex.getMessage());
        }

        return "redirect:/admin/especialidades";
    }

    @PostMapping("/quitar")
    public String quitar(
            @RequestParam Long masajistaId,
            @RequestParam Long tipoMasajeId,
            RedirectAttributes redirectAttributes) {

        try {
            masajeService.quitarEspecialidad(
                    masajistaId, tipoMasajeId);
            redirectAttributes.addFlashAttribute(
                    "mensaje", "Especialidad quitada correctamente.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute(
                    "error", ex.getMessage());
        }

        return "redirect:/admin/especialidades";
    }
}
