package com.megspa.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "especialidades_masajista",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_masajista_tipo_masaje",
        columnNames = {"masajista_id", "tipo_masaje_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
public class EspecialidadMasajista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "masajista_id", nullable = false)
    private Masajista masajista;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_masaje_id", nullable = false)
    private TipoMasaje tipoMasaje;
}