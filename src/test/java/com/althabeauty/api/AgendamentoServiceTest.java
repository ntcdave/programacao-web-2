package com.althabeauty.api;

import com.althabeauty.api.model.Agendamento;
import com.althabeauty.api.model.Cliente;
import com.althabeauty.api.model.Servico;
import com.althabeauty.api.repository.AgendamentoRepository;
import com.althabeauty.api.repository.ClienteRepository;
import com.althabeauty.api.repository.ServicoRepository;
import com.althabeauty.api.service.AgendamentoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ServicoRepository servicoRepository;

    @InjectMocks
    private AgendamentoService agendamentoService;

    @Test
    void deveLancarExcecao_QuandoClienteNaoEncontrado() {
        Cliente cliente = new Cliente();
        cliente.setId(99L);

        Servico servico = new Servico();
        servico.setId(1L);

        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setServico(servico);
        agendamento.setDataHora(LocalDateTime.now());

        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> agendamentoService.salvar(agendamento));
        assertEquals("Cliente não encontrado: 99", ex.getMessage());

        verify(servicoRepository, never()).findById(any());
        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    void deveSalvarAgendamento_QuandoClienteEServicoExistem() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Servico servico = new Servico();
        servico.setId(1L);
        servico.setNome("Corte de Cabelo");
        servico.setPreco(BigDecimal.valueOf(50));
        servico.setDuracaoMinutos(30);

        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setServico(servico);
        agendamento.setDataHora(LocalDateTime.now());

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(servicoRepository.findById(1L)).thenReturn(Optional.of(servico));

        Agendamento agendamentoSalvo = new Agendamento();
        agendamentoSalvo.setId(1L);
        agendamentoSalvo.setCliente(cliente);
        agendamentoSalvo.setServico(servico);
        agendamentoSalvo.setDataHora(agendamento.getDataHora());
        agendamentoSalvo.setStatus("PENDENTE");

        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamentoSalvo);

        Agendamento resultado = agendamentoService.salvar(agendamento);

        assertNotNull(resultado);
        assertEquals("PENDENTE", resultado.getStatus());
        assertEquals(1L, resultado.getId());

        verify(clienteRepository).findById(1L);
        verify(servicoRepository).findById(1L);
        verify(agendamentoRepository).save(any(Agendamento.class));
    }
}
