package br.edu.ifac.api_banco_digital.service;

import br.edu.ifac.api_banco_digital.dto.*;
import br.edu.ifac.api_banco_digital.exception.ContaNaoEncontradaException;
import br.edu.ifac.api_banco_digital.exception.SaldoInsuficienteException;
import br.edu.ifac.api_banco_digital.exception.ValorInvalidoException;
import br.edu.ifac.api_banco_digital.model.Conta;
import br.edu.ifac.api_banco_digital.repository.ContaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class ContaService {

    private final ContaRepository contaRepository;

    public ContaService(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    // ── Criar Conta ──────────────────────────────────────────────────────────────

    public ContaResponseDTO criarConta(ContaRequestDTO dto) {
        if (dto.getNomeTitular() == null || dto.getNomeTitular().isBlank()) {
            throw new ValorInvalidoException("Nome do titular é obrigatório e não pode ser vazio");
        }
        if (dto.getSaldo() == null) {
            dto.setSaldo(BigDecimal.ZERO);
        }
        if (dto.getSaldo().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValorInvalidoException("Saldo inicial não pode ser negativo");
        }

        Conta conta = new Conta();
        conta.setNomeTitular(dto.getNomeTitular());
        conta.setNumeroConta(dto.getNumeroConta());
        conta.setSaldo(dto.getSaldo());

        Conta salva = contaRepository.save(conta);
        return toResponse(salva);
    }

    // ── Consultar por Número ──────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public ContaResponseDTO buscarPorNumero(String numeroConta) {
        Conta conta = contaRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta não encontrada: " + numeroConta));
        return toResponse(conta);
    }

    // ── Consultar por ID ──────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public ContaResponseDTO buscarPorId(Long id) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta não encontrada com id: " + id));
        return toResponse(conta);
    }

    // ── Depósito ──────────────────────────────────────────────────────────────────

    public ContaResponseDTO depositar(String numeroConta, OperacaoDTO dto) {
        if (dto.getValor() == null || dto.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValorInvalidoException("Valor do depósito deve ser maior que zero");
        }
        Conta conta = contaRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta não encontrada: " + numeroConta));

        conta.setSaldo(conta.getSaldo().add(dto.getValor()));
        return toResponse(contaRepository.save(conta));
    }

    // ── Saque ─────────────────────────────────────────────────────────────────────

    public ContaResponseDTO sacar(String numeroConta, OperacaoDTO dto) {
        if (dto.getValor() == null || dto.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValorInvalidoException("Valor do saque deve ser maior que zero");
        }
        Conta conta = contaRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta não encontrada: " + numeroConta));

        if (conta.getSaldo().compareTo(dto.getValor()) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente para realizar o saque");
        }

        conta.setSaldo(conta.getSaldo().subtract(dto.getValor()));
        return toResponse(contaRepository.save(conta));
    }

    // ── Transferência ─────────────────────────────────────────────────────────────

    public void transferir(TransferenciaDTO dto) {
        Conta contaOrigem = contaRepository.findByNumeroConta(dto.getNumeroContaOrigem())
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta de origem não encontrada: " + dto.getNumeroContaOrigem()));

        Conta contaDestino = contaRepository.findByNumeroConta(dto.getNumeroContaDestino())
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta de destino não encontrada: " + dto.getNumeroContaDestino()));

        if (dto.getValor() == null || dto.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValorInvalidoException("Valor da transferência deve ser maior que zero");
        }

        if (contaOrigem.getSaldo().compareTo(dto.getValor()) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente na conta de origem para realizar a transferência");
        }

        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(dto.getValor()));
        contaDestino.setSaldo(contaDestino.getSaldo().add(dto.getValor()));

        contaRepository.save(contaOrigem);
        contaRepository.save(contaDestino);
        // Qualquer exceção nos saves reverte toda a transação pelo @Transactional da classe
    }

    // ── Helper ────────────────────────────────────────────────────────────────────

    private ContaResponseDTO toResponse(Conta conta) {
        return new ContaResponseDTO(
                conta.getId(),
                conta.getNomeTitular(),
                conta.getNumeroConta(),
                conta.getSaldo()
        );
    }
}
