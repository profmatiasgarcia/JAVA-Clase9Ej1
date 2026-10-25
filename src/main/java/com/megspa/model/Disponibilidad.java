package com.megspa.model;

import com.megspa.model.enums.DiaSemana;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "disponibilidades")
@Getter
@Setter
@NoArgsConstructor
public class Disponibilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 15)
    private DiaSemana diaSemana;

    @NotNull
    @Column(name = "hora_desde", nullable = false)
    private LocalTime horaDesde;

    private LocalTime horaHasta;

    @Column(nullable = false)
    private boolean vigente = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "masajista_id", nullable = false)
    private Masajista masajista;
}   