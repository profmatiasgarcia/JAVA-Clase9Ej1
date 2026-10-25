package com.megspa.service;

import com.megspa.model.Disponibilidad;
import com.megspa.model.Masajista;
import com.megspa.model.enums.DiaSemana;
import com.megspa.repository.DisponibilidadRepository;
import com.megspa.repository.MasajistaRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DisponibilidadService {

    private final DisponibilidadRepository disponibilidadRepository;
    private final MasajistaRepository masajistaRepository;

    public DisponibilidadService(
            DisponibilidadRepository disponibilidadRepository,
            MasajistaRepository masajistaRepository) {
        this.disponibilidadRepository = disponibilidadRepository;
        this.masajistaRepository = masajistaRepository;
    }

    @Transactional(readOnly = true)
    public List<Disponibilidad> listarPorMasajista(Long masajistaId) {
        return disponibilidadRepository
                .findByMasajista_IdAndVigenteTrueOrderByDiaSemanaAscHoraDesdeAsc(
                        masajistaId);
    }

    @Transactional(readOnly = true)
    public List<Disponibilidad> listarTodas() {
        return disponibilidadRepository
                .findAllByOrderByMasajista_NombreAscDiaSemanaAscHoraDesdeAsc();
    }

    @Transactional(readOnly = true)
    public Disponibilidad buscarPorId(Long id) {
        return disponibilidadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                "No existe la disponibilidad indicada."));
    }

    @Transactional
    public Disponibilidad guardar(
            Long id,
            Long masajistaId,
            DiaSemana diaSemana,
            LocalTime horaDesde,
            LocalTime horaHasta) {

        if (masajistaId == null) {
            throw new IllegalArgumentException(
                    "Debés seleccionar un masajista.");
        }

        if (diaSemana == null) {
            throw new IllegalArgumentException(
                    "Debés seleccionar un día.");
        }

        if (horaDesde == null || horaHasta == null) {
            throw new IllegalArgumentException(
                    "Debés indicar la hora de inicio y de fin.");
        }

        if (!horaDesde.isBefore(horaHasta)) {
            throw new IllegalArgumentException(
                    "La hora de fin debe ser posterior a la de inicio.");
        }

        Masajista masajista = masajistaRepository.findById(masajistaId)
                .orElseThrow(() -> new IllegalArgumentException(
                "No existe el masajista seleccionado."));

        if (!masajista.isActivo()) {
            throw new IllegalStateException(
                    "No se pueden configurar horarios para un masajista inactivo.");
        }

        Disponibilidad disponibilidad;

        if (id == null) {
            disponibilidad = new Disponibilidad();
        } else {
            disponibilidad = buscarPorId(id);

            if (!disponibilidad.isVigente()) {
                throw new IllegalStateException(
                        "No se puede editar una disponibilidad desactivada. Reactivala primero.");
            }
        }

        long superposiciones = disponibilidadRepository.contarSuperposiciones(
                masajistaId,
                diaSemana,
                horaDesde,
                horaHasta,
                id);

        if (superposiciones > 0) {
            throw new IllegalArgumentException(
                    "El horario se superpone con otra franja vigente del masajista.");
        }

        disponibilidad.setMasajista(masajista);
        disponibilidad.setDiaSemana(diaSemana);
        disponibilidad.setHoraDesde(horaDesde);
        disponibilidad.setHoraHasta(horaHasta);
        disponibilidad.setVigente(true);

        return disponibilidadRepository.save(disponibilidad);
    }

    @Transactional
    public void cambiarVigencia(Long id, boolean vigente) {
        Disponibilidad disponibilidad = buscarPorId(id);

        if (disponibilidad.isVigente() == vigente) {
            return;
        }

        if (vigente) {
            if (!disponibilidad.getMasajista().isActivo()) {
                throw new IllegalStateException(
                        "No se puede reactivar el horario de un masajista inactivo.");
            }

            long superposiciones = disponibilidadRepository.contarSuperposiciones(
                    disponibilidad.getMasajista().getId(),
                    disponibilidad.getDiaSemana(),
                    disponibilidad.getHoraDesde(),
                    disponibilidad.getHoraHasta(),
                    id);

            if (superposiciones > 0) {
                throw new IllegalStateException(
                        "No se puede reactivar: el horario se superpone con otra franja vigente.");
            }
        }

        disponibilidad.setVigente(vigente);
        disponibilidadRepository.save(disponibilidad);
    }

    @Transactional(readOnly = true)
    public boolean estaDisponible(
            Long masajistaId,
            LocalDateTime inicio,
            LocalDateTime fin) {

        if (masajistaId == null || inicio == null || fin == null
                || !inicio.isBefore(fin)) {
            return false;
        }

        if (!inicio.toLocalDate().equals(fin.toLocalDate())) {
            return false;
        }

        if (!inicio.isAfter(LocalDateTime.now())) {
            return false;
        }

        DiaSemana dia = convertirDia(inicio.getDayOfWeek());

        List<Disponibilidad> franjas = disponibilidadRepository
                .findByMasajista_IdAndDiaSemanaAndVigenteTrueOrderByHoraDesdeAsc(
                        masajistaId, dia);

        LocalTime horaInicio = inicio.toLocalTime();
        LocalTime horaFin = fin.toLocalTime();

        return franjas.stream().anyMatch(franja
                -> !horaInicio.isBefore(franja.getHoraDesde())
                && !horaFin.isAfter(franja.getHoraHasta()));
    }

    private DiaSemana convertirDia(java.time.DayOfWeek dia) {
        return switch (dia) {
            case MONDAY -> DiaSemana.LUNES;
            case TUESDAY -> DiaSemana.MARTES;
            case WEDNESDAY -> DiaSemana.MIERCOLES;
            case THURSDAY -> DiaSemana.JUEVES;
            case FRIDAY -> DiaSemana.VIERNES;
            case SATURDAY -> DiaSemana.SABADO;
            case SUNDAY -> DiaSemana.DOMINGO;
        };
    }
}
