package com.megspa.service;

import com.megspa.model.Cliente;
import com.megspa.model.Masajista;
import com.megspa.model.Reserva;
import com.megspa.model.TipoMasaje;
import com.megspa.model.enums.EstadoReserva;
import com.megspa.repository.ClienteRepository;
import com.megspa.repository.EspecialidadMasajistaRepository;
import com.megspa.repository.MasajistaRepository;
import com.megspa.repository.ReservaRepository;
import com.megspa.repository.TipoMasajeRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ClienteRepository clienteRepository;
    private final MasajistaRepository masajistaRepository;
    private final TipoMasajeRepository tipoMasajeRepository;
    private final EspecialidadMasajistaRepository especialidadRepository;
    private final DisponibilidadService disponibilidadService;

    public ReservaService(
            ReservaRepository reservaRepository,
            ClienteRepository clienteRepository,
            MasajistaRepository masajistaRepository,
            TipoMasajeRepository tipoMasajeRepository,
            EspecialidadMasajistaRepository especialidadRepository,
            DisponibilidadService disponibilidadService) {

        this.reservaRepository = reservaRepository;
        this.clienteRepository = clienteRepository;
        this.masajistaRepository = masajistaRepository;
        this.tipoMasajeRepository = tipoMasajeRepository;
        this.especialidadRepository = especialidadRepository;
        this.disponibilidadService = disponibilidadService;
    }

    @Transactional
    public Reserva crearReserva(
            Long clienteId,
            Long masajistaId,
            Long tipoMasajeId,
            LocalDateTime inicio) {

        if (inicio == null || !inicio.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "La fecha y hora deben ser futuras");
        }

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException(
                "No existe el cliente"));

        Masajista masajista = masajistaRepository
                .findById(masajistaId)
                .orElseThrow(() -> new IllegalArgumentException(
                "No existe el masajista"));

        TipoMasaje masaje = tipoMasajeRepository
                .findById(tipoMasajeId)
                .orElseThrow(() -> new IllegalArgumentException(
                "No existe el tipo de masaje"));

        if (!cliente.isActivo()
                || !masajista.isActivo()
                || !masaje.isActivo()) {
            throw new IllegalStateException(
                    "El cliente, masajista y tratamiento deben estar activos");
        }

        boolean ofreceTratamiento = especialidadRepository
                .existsByMasajista_IdAndTipoMasaje_Id(
                        masajistaId, tipoMasajeId);

        if (!ofreceTratamiento) {
            throw new IllegalArgumentException(
                    "El masajista no ofrece este tratamiento");
        }

        LocalDateTime fin = inicio.plusMinutes(
                masaje.getDuracionMinutos());

        if (!disponibilidadService.estaDisponible(
                masajistaId, inicio, fin)) {
            throw new IllegalArgumentException(
                    "El turno está fuera del horario de atención");
        }

        boolean hayConflicto = reservaRepository.existeSuperposicion(
                masajistaId,
                inicio,
                fin,
                EstadoReserva.CANCELADA,
                EstadoReserva.REALIZADA);

        if (hayConflicto) {
            throw new IllegalStateException(
                    "El masajista ya tiene una reserva en ese horario");
        }

        Reserva reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setMasajista(masajista);
        reserva.setTipoMasaje(masaje);
        reserva.setFechaHoraInicio(inicio);
        reserva.setFechaHoraFin(fin);
        reserva.setPrecioAplicado(masaje.getPrecio());
        reserva.setEstado(EstadoReserva.PENDIENTE);

        return reservaRepository.save(reserva);
    }

    @Transactional(readOnly = true)
    public List<Reserva> listarReservasCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new IllegalArgumentException(
                    "No existe el cliente");
        }

        return reservaRepository
                .findByCliente_IdOrderByFechaHoraInicioDesc(clienteId);
    }

    @Transactional(readOnly = true)
    public List<Reserva> listarTodas() {
        return reservaRepository
                .findAllByOrderByFechaHoraInicioDesc();
    }

    @Transactional
    public Reserva confirmarReserva(Long reservaId) {
        Reserva reserva = buscarReserva(reservaId);

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException(
                    "Solo se pueden confirmar reservas pendientes");
        }

        if (!reserva.getFechaHoraInicio()
                .isAfter(LocalDateTime.now())) {
            throw new IllegalStateException(
                    "No se puede confirmar una reserva cuyo inicio ya pasó");
        }

        reserva.setEstado(EstadoReserva.CONFIRMADA);
        return reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva cancelarReserva(Long reservaId, Long clienteId) {
        Reserva reserva = buscarReserva(reservaId);

        if (!reserva.getCliente().getId().equals(clienteId)) {
            throw new IllegalArgumentException(
                    "La reserva no pertenece a este cliente");
        }

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new IllegalArgumentException(
                    "La reserva ya está cancelada."
            );
        }

        if (reserva.getEstado() != EstadoReserva.PENDIENTE
                && reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new IllegalStateException(
                    "La reserva no se puede cancelar");
        }

        if (!reserva.getFechaHoraInicio()
                .isAfter(LocalDateTime.now())) {
            throw new IllegalStateException(
                    "No se puede cancelar una reserva cuyo inicio ya pasó");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        return reservaRepository.save(reserva);
    }

    @Transactional
    public void cancelarReserva(Long reservaId) {
        Reserva reserva = buscarReserva(reservaId);

        if (reserva.getEstado() != EstadoReserva.PENDIENTE
                && reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new IllegalArgumentException(
                    "Solo se pueden cancelar reservas pendientes "
                    + "o confirmadas."
            );
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva marcarRealizada(Long reservaId) {
        Reserva reserva = buscarReserva(reservaId);

        if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new IllegalStateException(
                    "Solo se pueden marcar como realizadas las reservas confirmadas");
        }

        if (reserva.getFechaHoraFin().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException(
                    "La sesión todavía no ha finalizado");
        }

        reserva.setEstado(EstadoReserva.REALIZADA);
        return reservaRepository.save(reserva);
    }

    private Reserva buscarReserva(Long reservaId) {
        return reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException(
                "No existe la reserva con ID " + reservaId));
    }
}
