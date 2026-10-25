package com.megspa.service;

import com.megspa.model.Cliente;
import com.megspa.repository.ClienteRepository;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public Cliente registrar(Cliente cliente) {
        cliente.setId(null);

        if (cliente.getNombre() == null
                || cliente.getNombre().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre es obligatorio");
        }

        if (cliente.getEmail() == null
                || cliente.getEmail().isBlank()) {
            throw new IllegalArgumentException(
                    "El email es obligatorio");
        }

        String email = cliente.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (clienteRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Ya existe un cliente con ese email");
        }

        cliente.setNombre(cliente.getNombre().trim());
        cliente.setEmail(email);
        cliente.setActivo(true);

        return clienteRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe el cliente con ID " + id));
    }
}