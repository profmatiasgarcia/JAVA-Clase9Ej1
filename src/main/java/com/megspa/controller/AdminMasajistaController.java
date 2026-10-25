package com.megspa.controller;

import com.megspa.dto.MasajistaForm;
import com.megspa.model.Masajista;
import com.megspa.service.MasajeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/masajistas")
public class AdminMasajistaController {

    private final MasajeService masajeService;

    public AdminMasajistaController(MasajeService masajeService) {
        this.masajeService = masajeService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute(
                "masajistas",
                masajeService.listarTodosLosMasajistas());

        return "admin/masajistas";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("masajista", new MasajistaForm());
        model.addAttribute("titulo", "Nuevo masajista");

        return "admin/masajista-formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Masajista existente
                    = masajeService.buscarMasajistaPorId(id);

            MasajistaForm form = new MasajistaForm();
            form.setId(existente.getId());
            form.setNombre(existente.getNombre());
            form.setEmail(existente.getEmail());
            form.setTelefono(existente.getTelefono());

            model.addAttribute("masajista", form);
            model.addAttribute("titulo", "Editar masajista");

            return "admin/masajista-formulario";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/masajistas";
        }
    }

    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute("masajista") MasajistaForm form,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            masajeService.guardarMasajista(
                    form.getId(),
                    form.getNombre(),
                    form.getEmail(),
                    form.getTelefono());

            redirectAttributes.addFlashAttribute(
                    "mensaje", "Masajista guardado correctamente.");

            return "redirect:/admin/masajistas";

        } catch (IllegalArgumentException e) {
            model.addAttribute("titulo",
                    form.getId() == null
                    ? "Nuevo masajista"
                    : "Editar masajista");

            model.addAttribute("error", e.getMessage());

            return "admin/masajista-formulario";
        }
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        return cambiarEstado(id, false, redirectAttributes);
    }

    @PostMapping("/{id}/activar")
    public String activar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        return cambiarEstado(id, true, redirectAttributes);
    }

    private String cambiarEstado(
            Long id,
            boolean activo,
            RedirectAttributes redirectAttributes) {

        try {
            masajeService.cambiarEstadoMasajista(id, activo);

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    activo
                            ? "Masajista activado correctamente."
                            : "Masajista desactivado correctamente.");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/masajistas";
    }
}
