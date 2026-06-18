package com.althabeauty.api.service;

import com.althabeauty.api.model.Servico;
import com.althabeauty.api.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository servicoRepository;

    public List<Servico> listarTodos() {
        return servicoRepository.findAll();
    }

    public Servico buscarPorId(Long id) {
        return servicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado: " + id));
    }

    public Servico salvar(Servico servico) {
        return servicoRepository.save(servico);
    }

    public Servico atualizar(Long id, Servico servico) {
        buscarPorId(id);
        servico.setId(id);
        return servicoRepository.save(servico);
    }

    public void deletar(Long id) {
        servicoRepository.deleteById(id);
    }
}
