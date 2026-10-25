package com.megspa.repository;

import com.megspa.model.Masajista;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MasajistaRepository extends JpaRepository<Masajista, Long> {

    Optional<Masajista> findByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);

    List<Masajista> findByActivoTrueOrderByNombreAsc();

    List<Masajista> findAllByOrderByNombreAsc();
}
