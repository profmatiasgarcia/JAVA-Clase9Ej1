package com.megspa.service;

import com.megspa.model.EspecialidadMasajista;
import com.megspa.model.Masajista;
import com.megspa.model.TipoMasaje;
import com.megspa.repository.EspecialidadMasajistaRepository;
import com.megspa.repository.MasajistaRepository;
import com.megspa.repository.TipoMasajeRepository;
import java.util.List;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MasajeService {

    private final TipoMasajeRepository tipoMasajeRepository;
    private final MasajistaRepository masajistaRepository;
    private final EspecialidadMasajistaRepository especialidadRepository;

    public MasajeService(
            TipoMasajeRepository tipoMasajeRepository,
            MasajistaRepository masajistaRepository,
            EspecialidadMasajistaRepository especialidadRepository) {

        this.tipoMasajeRepository = tipoMasajeRepository;
        this.masajistaRepository = masajistaRepository;
        this.especialidadRepository = especialidadRepository;
    }

    @Transactional(readOnly = true)
    public List<TipoMasaje> listarTratamientosActivos() {
        return tipoMasajeRepository
                .findByActivoTrueOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public List<Masajista> listarMasajistasActivos() {
        return masajistaRepository
                .findByActivoTrueOrderByNombreAsc();
    }

    @Transactional
    public EspecialidadMasajista asignarEspecialidad(
            Long masajistaId, Long tipoMasajeId) {

        Masajista masajista = masajistaRepository
                .findById(masajistaId)
                .orElseThrow(() -> new IllegalArgumentException(
                "No existe el masajista"));

        TipoMasaje masaje = tipoMasajeRepository
                .findById(tipoMasajeId)
                .orElseThrow(() -> new IllegalArgumentException(
                "No existe el tipo de masaje"));

        if (!masajista.isActivo() || !masaje.isActivo()) {
            throw new IllegalStateException(
                    "El masajista y el tratamiento deben estar activos");
        }

        if (especialidadRepository
                .existsByMasajista_IdAndTipoMasaje_Id(
                        masajistaId, tipoMasajeId)) {
            throw new IllegalArgumentException(
                    "La especialidad ya está asignada");
        }

        EspecialidadMasajista especialidad
                = new EspecialidadMasajista();

        especialidad.setMasajista(masajista);
        especialidad.setTipoMasaje(masaje);

        return especialidadRepository.save(especialidad);
    }

    @Transactional(readOnly = true)
    public List<TipoMasaje> listarTodosLosTratamientos() {
        return tipoMasajeRepository.findAllByOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public TipoMasaje buscarTratamientoPorId(Long id) {
        return tipoMasajeRepository.findById(id)
                .orElseThrow(()
                        -> new IllegalArgumentException(
                        "No existe el tratamiento indicado."));
    }

    @Transactional
    public TipoMasaje guardarTratamiento(
            Long id,
            String nombre,
            String descripcion,
            Integer duracionMinutos,
            BigDecimal precio) {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre es obligatorio.");
        }

        nombre = nombre.trim();

        if (nombre.length() > 100) {
            throw new IllegalArgumentException(
                    "El nombre no puede superar los 100 caracteres.");
        }

        if (descripcion != null && descripcion.length() > 500) {
            throw new IllegalArgumentException(
                    "La descripción no puede superar los 500 caracteres.");
        }

        if (duracionMinutos == null || duracionMinutos < 1) {
            throw new IllegalArgumentException(
                    "La duración debe ser de al menos un minuto.");
        }

        if (precio == null || precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "El precio no puede ser negativo ni estar vacío.");
        }

        TipoMasaje tratamiento;

        if (id == null) {
            tratamiento = new TipoMasaje();
            tratamiento.setActivo(true);
        } else {
            tratamiento = buscarTratamientoPorId(id);
        }

        var existente = tipoMasajeRepository
                .findByNombreIgnoreCase(nombre);

        if (existente.isPresent()
                && !existente.get().getId().equals(tratamiento.getId())) {
            throw new IllegalArgumentException(
                    "Ya existe un tratamiento con ese nombre.");
        }

        tratamiento.setNombre(nombre);
        tratamiento.setDescripcion(
                descripcion == null || descripcion.isBlank()
                ? null : descripcion.trim());
        tratamiento.setDuracionMinutos(duracionMinutos);
        tratamiento.setPrecio(precio);

        return tipoMasajeRepository.save(tratamiento);
    }

    @Transactional
    public void cambiarEstadoTratamiento(Long id, boolean activo) {
        TipoMasaje tratamiento = buscarTratamientoPorId(id);
        tratamiento.setActivo(activo);
        tipoMasajeRepository.save(tratamiento);
    }

    @Transactional(readOnly = true)
    public List<Masajista> listarTodosLosMasajistas() {
        return masajistaRepository.findAllByOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public Masajista buscarMasajistaPorId(Long id) {
        return masajistaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                "No existe el masajista indicado."));
    }

    @Transactional
    public Masajista guardarMasajista(
            Long id,
            String nombre,
            String email,
            String telefono) {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre es obligatorio.");
        }

        nombre = nombre.trim();

        if (nombre.length() > 100) {
            throw new IllegalArgumentException(
                    "El nombre no puede superar los 100 caracteres.");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "El email es obligatorio.");
        }

        email = email.trim().toLowerCase(Locale.ROOT);

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException(
                    "Ingresá un email válido.");
        }

        if (email.length() > 150) {
            throw new IllegalArgumentException(
                    "El email no puede superar los 150 caracteres.");
        }

        if (telefono != null) {
            telefono = telefono.trim();

            if (telefono.isEmpty()) {
                telefono = null;
            }

            if (telefono != null && telefono.length() > 30) {
                throw new IllegalArgumentException(
                        "El teléfono no puede superar los 30 caracteres.");
            }
        }

        Masajista masajista;

        if (id == null) {
            masajista = new Masajista();
            masajista.setActivo(true);
        } else {
            masajista = buscarMasajistaPorId(id);
        }

        Optional<Masajista> existente
                = masajistaRepository.findByEmailIgnoreCase(email);

        if (existente.isPresent()
                && !java.util.Objects.equals(
                        existente.get().getId(), masajista.getId())) {
            throw new IllegalArgumentException(
                    "Ya existe un masajista registrado con ese email.");
        }

        masajista.setNombre(nombre);
        masajista.setEmail(email);
        masajista.setTelefono(telefono);

        return masajistaRepository.save(masajista);
    }

    @Transactional
    public void cambiarEstadoMasajista(Long id, boolean activo) {
        Masajista masajista = buscarMasajistaPorId(id);
        masajista.setActivo(activo);
        masajistaRepository.save(masajista);
    }

    @Transactional(readOnly = true)
    public List<EspecialidadMasajista> listarTodasLasEspecialidades() {
        return especialidadRepository
                .findAllByOrderByMasajista_NombreAsc();
    }

    @Transactional
    public void quitarEspecialidad(
            Long masajistaId,
            Long tipoMasajeId) {

        EspecialidadMasajista especialidad = especialidadRepository
                .findByMasajista_IdAndTipoMasaje_Id(
                        masajistaId, tipoMasajeId)
                .orElseThrow(() -> new IllegalArgumentException(
                "La especialidad indicada no existe."));

        especialidadRepository.delete(especialidad);
    }

}
