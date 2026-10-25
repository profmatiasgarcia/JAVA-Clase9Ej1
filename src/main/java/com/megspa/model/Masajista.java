package com.megspa.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "masajistas")
@Getter
@Setter
@NoArgsConstructor
public class Masajista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 30)
    private String telefono;

    @Column(nullable = false)
    private boolean activo = true;
}