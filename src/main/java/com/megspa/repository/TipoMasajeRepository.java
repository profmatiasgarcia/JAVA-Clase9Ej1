package com.megspa.repository;

import com.megspa.model.TipoMasaje;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoMasajeRepository extends JpaRepository<TipoMasaje, Long> {

    Optional<TipoMasaje> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    List<TipoMasaje> findByActivoTrueOrderByNombreAsc();
    
    List<TipoMasaje> findAllByOrderByNombreAsc();
}