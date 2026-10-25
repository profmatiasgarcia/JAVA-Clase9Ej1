package com.megspa.repository;

import com.megspa.model.Reserva;
import com.megspa.model.enums.EstadoReserva;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByCliente_IdOrderByFechaHoraInicioDesc(
            Long clienteId);

    List<Reserva> findByMasajista_IdOrderByFechaHoraInicioAsc(
            Long masajistaId);

    List<Reserva> findByMasajista_IdAndFechaHoraInicioBetweenOrderByFechaHoraInicioAsc(
            Long masajistaId,
            LocalDateTime desde,
            LocalDateTime hasta);

    List<Reserva> findByEstadoOrderByFechaHoraInicioAsc(EstadoReserva estado);

    List<Reserva> findAllByOrderByFechaHoraInicioDesc();

    @Query("""
        SELECT COUNT(r) > 0
        FROM Reserva r
        WHERE r.masajista.id = :masajistaId
          AND r.estado <> :cancelada
          AND r.estado <> :realizada
          AND r.fechaHoraInicio < :nuevoFin
          AND r.fechaHoraFin > :nuevoInicio
        """)
    boolean existeSuperposicion(
            @Param("masajistaId") Long masajistaId,
            @Param("nuevoInicio") LocalDateTime nuevoInicio,
            @Param("nuevoFin") LocalDateTime nuevoFin,
            @Param("cancelada") EstadoReserva cancelada,
            @Param("realizada") EstadoReserva realizada);
}
