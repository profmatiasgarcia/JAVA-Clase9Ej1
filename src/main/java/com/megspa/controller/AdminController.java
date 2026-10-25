package com.megspa.controller;

import com.megspa.model.Reserva;
import com.megspa.service.ReservaService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ReservaService reservaService;

    public AdminController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public String panel(Model model) {
        List<Reserva> reservas = reservaService.listarTodas();

        model.addAttribute("reservas", reservas);

        return "admin/panel";
    }

    @PostMapping("/reservas/{id}/confirmar")
    public String confirmar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        return ejecutarAccion(
                id, "confirmada",
                () -> reservaService.confirmarReserva(id),
                redirectAttributes
        );
    }

    @PostMapping("/reservas/{id}/realizada")
    public String realizada(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        return ejecutarAccion(
                id, "marcada como realizada",
                () -> reservaService.marcarRealizada(id),
                redirectAttributes
        );
    }

    @PostMapping("/reservas/{id}/cancelar")
    public String cancelar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        return ejecutarAccion(
                id, "cancelada",
                () -> reservaService.cancelarReserva(id),
                redirectAttributes
        );
    }

    private String ejecutarAccion(
            Long id,
            String accion,
            Runnable operacion,
            RedirectAttributes redirectAttributes) {
        try {
            operacion.run();
            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Reserva #" + id + " " + accion + " correctamente."
            );
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute(
                    "error", ex.getMessage()
            );
        }

        return "redirect:/admin";
    }
}
