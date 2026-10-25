package com.megspa.model;

import com.megspa.model.enums.Rol;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank
    @Column(name = "password_hash", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol = Rol.CLIENTE;

    @Column(nullable = false)
    private boolean activo = true;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", unique = true)
    private Cliente cliente;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "masajista_id", unique = true)
    private Masajista masajista;
}