package com.megspa.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tipos_masaje")
@Getter
@Setter
@NoArgsConstructor
public class TipoMasaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Min(1)
    @Column(nullable = false)
    private Integer duracionMinutos;

    @DecimalMin("0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private boolean activo = true;
}