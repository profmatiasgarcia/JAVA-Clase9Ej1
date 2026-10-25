package com.megspa.service;

import com.megspa.model.Reserva;
import com.megspa.model.enums.EstadoReserva;
import com.megspa.repository.ReservaRepository;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private DisponibilidadService disponibilidadService;

    @InjectMocks
    private ReservaService reservaService;

    private Reserva reserva;

    @BeforeEach
    void preparar() {
        reserva = new Reserva();
        reserva.setEstado(EstadoReserva.PENDIENTE);

        when(reservaRepository.findById(1L))
                .thenReturn(Optional.of(reserva));
    }

    @Test
    void debeCancelarReservaPendiente() {

        reservaService.cancelarReserva(1L, 1L);

        assertEquals(
                EstadoReserva.CANCELADA,
                reserva.getEstado()
        );

        verify(reservaRepository).save(reserva);
    }

    @Test
    void noDebeCancelarReservaRealizada() {

        reserva.setEstado(EstadoReserva.REALIZADA);

        assertThrows(
                IllegalArgumentException.class,
                () -> reservaService.cancelarReserva(1L, 1L)
        );

        verify(reservaRepository, never()).save(any());
    }

    @Test
    void debeInformarSiLaReservaNoExiste() {

        when(reservaRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> reservaService.cancelarReserva(99L, 1L)
        );

        verify(reservaRepository, never()).save(any());
    }
}