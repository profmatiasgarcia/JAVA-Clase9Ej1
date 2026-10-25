package com.megspa.service;

import com.megspa.model.Cliente;
import com.megspa.model.Usuario;
import com.megspa.model.enums.Rol;
import com.megspa.repository.ClienteRepository;
import com.megspa.repository.UsuarioRepository;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistroService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistroService(
            UsuarioRepository usuarioRepository,
            ClienteRepository clienteRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario registrarCliente(
            String nombre,
            String email,
            String password,
            String telefono) {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre es obligatorio");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "El email es obligatorio");
        }

        if (password == null || password.length() < 12) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos 12 caracteres");
        }

        String emailNormalizado = email.trim()
                .toLowerCase(Locale.ROOT);

        if (usuarioRepository.existsByEmailIgnoreCase(
                emailNormalizado)
                || clienteRepository.existsByEmail(emailNormalizado)) {
            throw new IllegalArgumentException(
                    "Ya existe una cuenta o cliente con ese email");
        }

        Cliente cliente = new Cliente();
        cliente.setNombre(nombre.trim());
        cliente.setEmail(emailNormalizado);
        cliente.setTelefono(telefono);
        cliente.setActivo(true);

        cliente = clienteRepository.save(cliente);

        Usuario usuario = new Usuario();
        usuario.setEmail(emailNormalizado);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(Rol.CLIENTE);
        usuario.setActivo(true);
        usuario.setCliente(cliente);

        return usuarioRepository.save(usuario);
    }
}
