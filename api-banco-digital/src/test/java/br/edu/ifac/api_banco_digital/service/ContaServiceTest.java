package br.edu.ifac.api_banco_digital.service;

import br.edu.ifac.api_banco_digital.dto.*;
import br.edu.ifac.api_banco_digital.exception.ContaNaoEncontradaException;
import br.edu.ifac.api_banco_digital.exception.SaldoInsuficienteException;
import br.edu.ifac.api_banco_digital.exception.ValorInvalidoException;
import br.edu.ifac.api_banco_digital.model.Conta;
import br.edu.ifac.api_banco_digital.repository.ContaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @InjectMocks
    private ContaService contaService;

    private Conta contaA;
    private Conta contaB;

    @BeforeEach
    void setup() {
        contaA = new Conta(1L, "João Silva", "001", new BigDecimal("200.00"));
        contaB = new Conta(2L, "Maria Souza", "002", new BigDecimal("50.00"));
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // criarConta
    // ─────────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Criar conta com dados válidos deve retornar DTO")
    void criarConta_dadosValidos_retornaDTO() {
        ContaRequestDTO dto = new ContaRequestDTO();
        dto.setNomeTitular("João Silva");
        dto.setNumeroConta("001");
        dto.setSaldo(new BigDecimal("100.00"));

        when(contaRepository.save(any(Conta.class))).thenReturn(contaA);

        ContaResponseDTO resposta = contaService.criarConta(dto);

        assertThat(resposta).isNotNull();
        assertThat(resposta.getNomeTitular()).isEqualTo("João Silva");
        verify(contaRepository, times(1)).save(any(Conta.class));
    }

    @Test
    @DisplayName("Criar conta com nome vazio deve lançar ValorInvalidoException")
    void criarConta_nomeVazio_lancaExcecao() {
        ContaRequestDTO dto = new ContaRequestDTO();
        dto.setNomeTitular("   ");
        dto.setNumeroConta("001");
        dto.setSaldo(BigDecimal.ZERO);

        assertThatThrownBy(() -> contaService.criarConta(dto))
                .isInstanceOf(ValorInvalidoException.class);
    }

    @Test
    @DisplayName("Criar conta com saldo negativo deve lançar ValorInvalidoException")
    void criarConta_saldoNegativo_lancaExcecao() {
        ContaRequestDTO dto = new ContaRequestDTO();
        dto.setNomeTitular("João Silva");
        dto.setNumeroConta("001");
        dto.setSaldo(new BigDecimal("-10.00"));

        assertThatThrownBy(() -> contaService.criarConta(dto))
                .isInstanceOf(ValorInvalidoException.class);
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // depositar
    // ─────────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Depositar valor positivo deve aumentar o saldo corretamente")
    void depositar_valorPositivo_aumentaSaldo() {
        OperacaoDTO dto = new OperacaoDTO();
        dto.setValor(new BigDecimal("50.00"));

        when(contaRepository.findByNumeroConta("001")).thenReturn(Optional.of(contaA));
        when(contaRepository.save(any(Conta.class))).thenAnswer(inv -> inv.getArgument(0));

        ContaResponseDTO resposta = contaService.depositar("001", dto);

        assertThat(resposta.getSaldo()).isEqualByComparingTo(new BigDecimal("250.00"));
    }

    @Test
    @DisplayName("Depositar valor <= 0 deve lançar ValorInvalidoException")
    void depositar_valorZero_lancaExcecao() {
        OperacaoDTO dto = new OperacaoDTO();
        dto.setValor(BigDecimal.ZERO);

        assertThatThrownBy(() -> contaService.depositar("001", dto))
                .isInstanceOf(ValorInvalidoException.class);
    }

    @Test
    @DisplayName("Depositar em conta inexistente deve lançar ContaNaoEncontradaException")
    void depositar_contaInexistente_lancaExcecao() {
        OperacaoDTO dto = new OperacaoDTO();
        dto.setValor(new BigDecimal("50.00"));

        when(contaRepository.findByNumeroConta("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contaService.depositar("999", dto))
                .isInstanceOf(ContaNaoEncontradaException.class);
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // sacar
    // ─────────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Sacar valor válido deve diminuir o saldo corretamente")
    void sacar_valorValido_diminuiSaldo() {
        OperacaoDTO dto = new OperacaoDTO();
        dto.setValor(new BigDecimal("50.00"));

        when(contaRepository.findByNumeroConta("001")).thenReturn(Optional.of(contaA));
        when(contaRepository.save(any(Conta.class))).thenAnswer(inv -> inv.getArgument(0));

        ContaResponseDTO resposta = contaService.sacar("001", dto);

        assertThat(resposta.getSaldo()).isEqualByComparingTo(new BigDecimal("150.00"));
    }

    @Test
    @DisplayName("Sacar com saldo insuficiente deve lançar SaldoInsuficienteException")
    void sacar_saldoInsuficiente_lancaExcecao() {
        OperacaoDTO dto = new OperacaoDTO();
        dto.setValor(new BigDecimal("500.00"));

        when(contaRepository.findByNumeroConta("001")).thenReturn(Optional.of(contaA));

        assertThatThrownBy(() -> contaService.sacar("001", dto))
                .isInstanceOf(SaldoInsuficienteException.class);
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // transferir
    // ─────────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Transferência válida deve debitar origem e creditar destino")
    void transferir_valida_debitaOrigemCreditaDestino() {
        TransferenciaDTO dto = new TransferenciaDTO();
        dto.setNumeroContaOrigem("001");
        dto.setNumeroContaDestino("002");
        dto.setValor(new BigDecimal("100.00"));

        when(contaRepository.findByNumeroConta("001")).thenReturn(Optional.of(contaA));
        when(contaRepository.findByNumeroConta("002")).thenReturn(Optional.of(contaB));
        when(contaRepository.save(any(Conta.class))).thenAnswer(inv -> inv.getArgument(0));

        contaService.transferir(dto);

        assertThat(contaA.getSaldo()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(contaB.getSaldo()).isEqualByComparingTo(new BigDecimal("150.00"));
        verify(contaRepository, times(2)).save(any(Conta.class));
    }

    @Test
    @DisplayName("Transferência com saldo insuficiente deve lançar SaldoInsuficienteException")
    void transferir_saldoInsuficiente_lancaExcecao() {
        TransferenciaDTO dto = new TransferenciaDTO();
        dto.setNumeroContaOrigem("001");
        dto.setNumeroContaDestino("002");
        dto.setValor(new BigDecimal("999.00"));

        when(contaRepository.findByNumeroConta("001")).thenReturn(Optional.of(contaA));
        when(contaRepository.findByNumeroConta("002")).thenReturn(Optional.of(contaB));

        assertThatThrownBy(() -> contaService.transferir(dto))
                .isInstanceOf(SaldoInsuficienteException.class);

        // Nenhum saldo deve ter sido alterado
        assertThat(contaA.getSaldo()).isEqualByComparingTo(new BigDecimal("200.00"));
        assertThat(contaB.getSaldo()).isEqualByComparingTo(new BigDecimal("50.00"));
        verify(contaRepository, never()).save(any(Conta.class));
    }

    @Test
    @DisplayName("Transferência com conta origem inexistente deve lançar ContaNaoEncontradaException")
    void transferir_contaOrigemInexistente_lancaExcecao() {
        TransferenciaDTO dto = new TransferenciaDTO();
        dto.setNumeroContaOrigem("999");
        dto.setNumeroContaDestino("002");
        dto.setValor(new BigDecimal("10.00"));

        when(contaRepository.findByNumeroConta("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contaService.transferir(dto))
                .isInstanceOf(ContaNaoEncontradaException.class);
    }

    @Test
    @DisplayName("Transferência com conta destino inexistente deve lançar ContaNaoEncontradaException")
    void transferir_contaDestinoInexistente_lancaExcecao() {
        TransferenciaDTO dto = new TransferenciaDTO();
        dto.setNumeroContaOrigem("001");
        dto.setNumeroContaDestino("999");
        dto.setValor(new BigDecimal("10.00"));

        when(contaRepository.findByNumeroConta("001")).thenReturn(Optional.of(contaA));
        when(contaRepository.findByNumeroConta("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contaService.transferir(dto))
                .isInstanceOf(ContaNaoEncontradaException.class);
    }
}
