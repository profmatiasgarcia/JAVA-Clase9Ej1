package com.megspa.controller;

import com.megspa.model.Usuario;
import com.megspa.repository.UsuarioRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import com.megspa.model.Reserva;
import com.megspa.service.MasajeService;
import com.megspa.service.ReservaService;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private final UsuarioRepository usuarioRepository;

    private final ReservaService reservaService;
    private final MasajeService masajeService;

    public ReservaController(
            ReservaService reservaService,
            MasajeService masajeService,
            UsuarioRepository usuarioRepository
    ) {
        this.reservaService = reservaService;
        this.masajeService = masajeService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        cargarFormulario(model);
        return "reservas/nueva";
    }

    @PostMapping
    public String crear(
            @AuthenticationPrincipal UserDetails usuarioAutenticado,
            @RequestParam Long masajistaId,
            @RequestParam Long tipoMasajeId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHoraInicio,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Usuario usuario = usuarioRepository
                    .findByEmailIgnoreCase(
                            usuarioAutenticado.getUsername())
                    .orElseThrow(() -> new IllegalStateException(
                    "No se encontró la cuenta autenticada"));

            if (usuario.getCliente() == null) {
                throw new IllegalStateException(
                        "La cuenta no tiene un perfil de cliente");
            }

            Long clienteId = usuario.getCliente().getId();

            Reserva reserva = reservaService.crearReserva(
                    clienteId,
                    masajistaId,
                    tipoMasajeId,
                    fechaHoraInicio
            );

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Reserva creada correctamente. Estado: "
                    + reserva.getEstado());

            return "redirect:/reservas/mias";

        } catch (IllegalArgumentException
                | IllegalStateException e) {

            model.addAttribute("error", e.getMessage());
            cargarFormulario(model);
            return "reservas/nueva";
        }
    }

    @GetMapping("/mias")
    public String misReservas(
            @AuthenticationPrincipal UserDetails usuarioAutenticado,
            Model model) {

        Usuario usuario = usuarioRepository
                .findByEmailIgnoreCase(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new IllegalStateException(
                "No se encontró la cuenta autenticada"));

        if (usuario.getCliente() == null) {
            throw new IllegalStateException(
                    "La cuenta no tiene un perfil de cliente");
        }

        model.addAttribute(
                "reservas",
                reservaService.listarReservasCliente(
                        usuario.getCliente().getId()));

        return "reservas/mis-reservas";
    }

    private void cargarFormulario(Model model) {
        model.addAttribute(
                "masajes",
                masajeService.listarTratamientosActivos()
        );

        model.addAttribute(
                "masajistas",
                masajeService.listarMasajistasActivos()
        );
    }
}
