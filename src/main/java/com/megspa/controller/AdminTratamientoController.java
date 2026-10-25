package com.megspa.controller;

import com.megspa.dto.TratamientoForm;
import com.megspa.model.TipoMasaje;
import com.megspa.service.MasajeService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/tratamientos")
public class AdminTratamientoController {

    private final MasajeService masajeService;

    public AdminTratamientoController(MasajeService masajeService) {
        this.masajeService = masajeService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute(
                "tratamientos",
                masajeService.listarTodosLosTratamientos());

        return "admin/tratamientos";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("tratamiento", new TratamientoForm());
        model.addAttribute("titulo", "Nuevo tratamiento");

        return "admin/tratamiento-formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        TipoMasaje existente =
                masajeService.buscarTratamientoPorId(id);

        TratamientoForm form = new TratamientoForm();
        form.setId(existente.getId());
        form.setNombre(existente.getNombre());
        form.setDescripcion(existente.getDescripcion());
        form.setDuracionMinutos(existente.getDuracionMinutos());
        form.setPrecio(existente.getPrecio());

        model.addAttribute("tratamiento", form);
        model.addAttribute("titulo", "Editar tratamiento");

        return "admin/tratamiento-formulario";
    }

    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute("tratamiento") TratamientoForm form,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            masajeService.guardarTratamiento(
                    form.getId(),
                    form.getNombre(),
                    form.getDescripcion(),
                    form.getDuracionMinutos(),
                    form.getPrecio());

            redirectAttributes.addFlashAttribute(
                    "mensaje", "Tratamiento guardado correctamente.");

            return "redirect:/admin/tratamientos";

        } catch (IllegalArgumentException e) {
            model.addAttribute("titulo",
                    form.getId() == null
                            ? "Nuevo tratamiento"
                            : "Editar tratamiento");

            model.addAttribute("error", e.getMessage());

            return "admin/tratamiento-formulario";
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
            masajeService.cambiarEstadoTratamiento(id, activo);

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    activo
                            ? "Tratamiento activado."
                            : "Tratamiento desactivado.");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());
        }

        return "redirect:/admin/tratamientos";
    }
}