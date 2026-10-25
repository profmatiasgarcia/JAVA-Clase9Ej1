package com.megspa.dto;

import com.megspa.model.enums.DiaSemana;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class DisponibilidadForm {

    private Long id;
    private Long masajistaId;
    private DiaSemana diaSemana;
    private LocalTime horaDesde;
    private LocalTime horaHasta;
}