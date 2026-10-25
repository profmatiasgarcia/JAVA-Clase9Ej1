package com.megspa.repository;

import com.megspa.model.EspecialidadMasajista;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EspecialidadMasajistaRepository extends JpaRepository<EspecialidadMasajista, Long> {

    List<EspecialidadMasajista>
            findByMasajista_Id(Long masajistaId);

    List<EspecialidadMasajista>
            findByTipoMasaje_Id(Long tipoMasajeId);

    Optional<EspecialidadMasajista>
            findByMasajista_IdAndTipoMasaje_Id(
                    Long masajistaId,
                    Long tipoMasajeId);

    boolean existsByMasajista_IdAndTipoMasaje_Id(
            Long masajistaId,
            Long tipoMasajeId);

    List<EspecialidadMasajista> findAllByOrderByMasajista_NombreAsc();

    void deleteByMasajista_IdAndTipoMasaje_Id(
            Long masajistaId,
            Long tipoMasajeId);
}
