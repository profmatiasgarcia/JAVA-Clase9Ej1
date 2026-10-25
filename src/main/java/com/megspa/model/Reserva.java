package com.megspa.model;

import com.megspa.model.enums.EstadoReserva;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reservas",
       indexes = {
           @Index(name = "idx_reserva_masajista_fecha",
                  columnList = "masajista_id, fecha_hora_inicio")
       })
@Getter
@Setter
@NoArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaHoraInicio;

    @NotNull
    @Column(name = "fecha_hora_fin", nullable = false)
    private LocalDateTime fechaHoraFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReserva estado = EstadoReserva.PENDIENTE;

    @NotNull
    @Column(name = "precio_aplicado",
            nullable = false, precision = 10, scale = 2)
    private BigDecimal precioAplicado;

    @Column(length = 500)
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "masajista_id", nullable = false)
    private Masajista masajista;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_masaje_id", nullable = false)
    private TipoMasaje tipoMasaje;
}