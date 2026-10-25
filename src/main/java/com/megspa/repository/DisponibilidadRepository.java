package com.megspa.repository;

import com.megspa.model.Disponibilidad;
import com.megspa.model.enums.DiaSemana;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {

    List<Disponibilidad> findByMasajista_IdAndVigenteTrueOrderByDiaSemanaAscHoraDesdeAsc(
            Long masajistaId);

    List<Disponibilidad> findByMasajista_IdAndDiaSemanaAndVigenteTrueOrderByHoraDesdeAsc(
            Long masajistaId,
            DiaSemana diaSemana);

    List<Disponibilidad>
            findAllByOrderByMasajista_NombreAscDiaSemanaAscHoraDesdeAsc();

    @Query("""
        SELECT COUNT(d)
        FROM Disponibilidad d
        WHERE d.masajista.id = :masajistaId
          AND d.diaSemana = :diaSemana
          AND d.vigente = true
          AND (:id IS NULL OR d.id <> :id)
          AND d.horaDesde < :horaHasta
          AND d.horaHasta > :horaDesde
        """)
    long contarSuperposiciones(
            @Param("masajistaId") Long masajistaId,
            @Param("diaSemana") DiaSemana diaSemana,
            @Param("horaDesde") LocalTime horaDesde,
            @Param("horaHasta") LocalTime horaHasta,
            @Param("id") Long id);
}
