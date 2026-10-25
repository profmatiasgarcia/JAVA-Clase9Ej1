package com.megspa.controller;

import com.megspa.dto.DisponibilidadForm;
import com.megspa.model.Disponibilidad;
import com.megspa.model.enums.DiaSemana;
import com.megspa.service.DisponibilidadService;
import com.megspa.service.MasajeService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/disponibilidades")
public class AdminDisponibilidadController {

    private final DisponibilidadService disponibilidadService;
    private final MasajeService masajeService;

    public AdminDisponibilidadController(
            DisponibilidadService disponibilidadService,
            MasajeService masajeService) {
        this.disponibilidadService = disponibilidadService;
        this.masajeService = masajeService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("disponibilidades",
                disponibilidadService.listarTodas());
        model.addAttribute("masajistas",
                masajeService.listarTodosLosMasajistas());

        return "admin/disponibilidades";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("form", new DisponibilidadForm());
        prepararFormulario(model);
        return "admin/disponibilidad-formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model,
                         RedirectAttributes redirectAttributes) {
        try {
            Disponibilidad disponibilidad =
                    disponibilidadService.buscarPorId(id);

            if (!disponibilidad.isVigente()) {
                redirectAttributes.addFlashAttribute("error",
                        "Reactivá la disponibilidad antes de editarla.");
                return "redirect:/admin/disponibilidades";
            }

            DisponibilidadForm form = new DisponibilidadForm();
            form.setId(disponibilidad.getId());
            form.setMasajistaId(disponibilidad.getMasajista().getId());
            form.setDiaSemana(disponibilidad.getDiaSemana());
            form.setHoraDesde(disponibilidad.getHoraDesde());
            form.setHoraHasta(disponibilidad.getHoraHasta());

            model.addAttribute("form", form);
            prepararFormulario(model);
            return "admin/disponibilidad-formulario";

        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/disponibilidades";
        }
    }

    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute("form") DisponibilidadForm form,
            RedirectAttributes redirectAttributes) {
        try {
            disponibilidadService.guardar(
                    form.getId(),
                    form.getMasajistaId(),
                    form.getDiaSemana(),
                    form.getHoraDesde(),
                    form.getHoraHasta());

            redirectAttributes.addFlashAttribute("mensaje",
                    "Disponibilidad guardada correctamente.");

        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/admin/disponibilidades";
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        return cambiarVigencia(id, false, redirectAttributes);
    }

    @PostMapping("/{id}/activar")
    public String activar(@PathVariable Long id,
                          RedirectAttributes redirectAttributes) {
        return cambiarVigencia(id, true, redirectAttributes);
    }

    private String cambiarVigencia(
            Long id,
            boolean vigente,
            RedirectAttributes redirectAttributes) {
        try {
            disponibilidadService.cambiarVigencia(id, vigente);
            redirectAttributes.addFlashAttribute("mensaje",
                    vigente
                    ? "Disponibilidad reactivada correctamente."
                    : "Disponibilidad desactivada correctamente.");

        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/admin/disponibilidades";
    }

    private void prepararFormulario(Model model) {
        model.addAttribute("masajistas",
                masajeService.listarMasajistasActivos());
        model.addAttribute("diasSemana",
                List.of(DiaSemana.values()));
    }
}