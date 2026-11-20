
package com.fatec.beassi.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.fatec.beassi.dto.ClienteCadastroDTO;
import com.fatec.beassi.dto.ClienteAtualizacaoDTO;
import com.fatec.beassi.dto.ClienteResponseDTO;

import com.fatec.beassi.entities.ClienteEntity;

import com.fatec.beassi.repositories.ClienteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    // LISTAR TODOS OS CLIENTES
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarTodos() {

        return clienteRepository.findAll()
            .stream()
            .map(this::converterParaDTO)
            .toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorId(int id) {
        ClienteEntity cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado."));
        return converterParaDTO(cliente);
    }

    // CADASTRAR CLIENTE
    @Transactional
    public ClienteResponseDTO incluir(ClienteCadastroDTO dto) {

        String email = usuarioService.normalizarEmail(dto.getEmail());

        usuarioService.validarEmailDisponivel(email, null);

        String cpf = normalizarCpf(dto.getCpf());

        validarCpfDisponivel(cpf, null);

        ClienteEntity cliente = new ClienteEntity();

        cliente.setEmail(email);
        cliente.setSenha(passwordEncoder.encode(dto.getSenha()));
        cliente.setNome(dto.getNome().trim());
        cliente.setCpf(cpf);
        cliente.setTelefone(dto.getTelefone().trim());
        cliente.setStatus(true);

        ClienteEntity salvo = clienteRepository.save(cliente);

        return converterParaDTO(salvo);
    }

    // EDITAR CLIENTE
    @Transactional
    public ClienteResponseDTO editar(int id, ClienteAtualizacaoDTO dto) {

        ClienteEntity cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Cliente não encontrado."
            ));

        if (!Boolean.TRUE.equals(cliente.getStatus())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Não é possível editar um cliente inativo."
            );
        }

        String email = usuarioService.normalizarEmail(dto.getEmail());

        usuarioService.validarEmailDisponivel(email, id);

        String cpf = normalizarCpf(dto.getCpf());

        validarCpfDisponivel(cpf, id);

        cliente.setEmail(email);
        cliente.setNome(dto.getNome().trim());
        cliente.setCpf(cpf);
        cliente.setTelefone(dto.getTelefone().trim());

        ClienteEntity atualizado = clienteRepository.save(cliente);

        return converterParaDTO(atualizado);
    }


    // INATIVAR CLIENTE
    @Transactional
    public void inativar(Integer id) {

        ClienteEntity cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Cliente não encontrado."
            ));

        if (!Boolean.TRUE.equals(cliente.getStatus())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Esta conta já está inativa."
            );
        }

        cliente.setStatus(false);
        clienteRepository.save(cliente);
    }

    // REATIVAR CLIENTE
    @Transactional
    public void reativar(Integer id) {

        ClienteEntity cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Cliente não encontrado."
            ));

        if (Boolean.TRUE.equals(cliente.getStatus())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Esta conta já está ativa."
            );
        }

        cliente.setStatus(true);
        clienteRepository.save(cliente);
    }


    // NORMALIZAR E VALIDAR CPF
    private String normalizarCpf(String cpf) {

        if (cpf == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "O CPF é obrigatório."
            );
        }

        String numeros = cpf.replaceAll("\\D", "");

        if (numeros.length() != 11 || !cpfValido(numeros)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Informe um CPF válido."
            );
        }

        return numeros.replaceFirst(
            "(\\d{3})(\\d{3})(\\d{3})(\\d{2})",
            "$1.$2.$3-$4"
        );
    }

    // VALIDAR DÍGITOS VERIFICADORES DO CPF
    private boolean cpfValido(String cpf) {

        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        int soma = 0;

        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }

        int primeiroDigito = (soma * 10) % 11;

        if (primeiroDigito == 10) {
            primeiroDigito = 0;
        }

        if (primeiroDigito !=
                Character.getNumericValue(cpf.charAt(9))) {
            return false;
        }

        soma = 0;

        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }

        int segundoDigito = (soma * 10) % 11;

        if (segundoDigito == 10) {
            segundoDigito = 0;
        }

        return segundoDigito ==
            Character.getNumericValue(cpf.charAt(10));
    }

    // VERIFICAR CPF DUPLICADO
    private void validarCpfDisponivel(String cpf, Integer idIgnorado) {

        clienteRepository.buscarPorCpf(cpf)
            .ifPresent(cliente -> {

                if (idIgnorado == null ||
                        cliente.getId_usuario() != idIgnorado) {

                    throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Já existe um cliente cadastrado com esse CPF."
                    );
                }
            });
    }

    // CONVERTER ENTITY PARA RESPONSE DTO
    private ClienteResponseDTO converterParaDTO(ClienteEntity cliente) {

        return new ClienteResponseDTO(
            cliente.getId_usuario(),
            cliente.getEmail(),
            cliente.getNome(),
            cliente.getCpf(),
            cliente.getTelefone(),
            cliente.getStatus()
        );
    }
}
