package com.althabeauty.api.service;

import com.althabeauty.api.model.Agendamento;
import com.althabeauty.api.repository.AgendamentoRepository;
import com.althabeauty.api.repository.ClienteRepository;
import com.althabeauty.api.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final ClienteRepository clienteRepository;
    private final ServicoRepository servicoRepository;

    public List<Agendamento> listarTodos() {
        return agendamentoRepository.findAll();
    }

    public Agendamento buscarPorId(Long id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado: " + id));
    }

    public Agendamento salvar(Agendamento agendamento) {
        clienteRepository.findById(agendamento.getCliente().getId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + agendamento.getCliente().getId()));
        servicoRepository.findById(agendamento.getServico().getId())
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado: " + agendamento.getServico().getId()));
        agendamento.setStatus("PENDENTE");
        return agendamentoRepository.save(agendamento);
    }

    public Agendamento atualizar(Long id, Agendamento agendamento) {
        buscarPorId(id);
        agendamento.setId(id);
        return agendamentoRepository.save(agendamento);
    }

    public void deletar(Long id) {
        agendamentoRepository.deleteById(id);
    }
}
