package br.edu.ifac.n2.apivulnerabilidades.service;

import br.edu.ifac.n2.apivulnerabilidades.model.Ocorrencia;
import br.edu.ifac.n2.apivulnerabilidades.model.StatusOcorrencia;
import br.edu.ifac.n2.apivulnerabilidades.repository.OcorrenciaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OcorrenciaService {

    private final OcorrenciaRepository repository;

    public OcorrenciaService(OcorrenciaRepository repository) {
        this.repository = repository;
    }

    public List<Ocorrencia> listarTodas() {
        return repository.findAll();
    }

    public Optional<Ocorrencia> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Ocorrencia salvar(Ocorrencia ocorrencia) {
        ocorrencia.setStatus(StatusOcorrencia.ABERTA);
        return repository.save(ocorrencia);
    }

    public Ocorrencia atualizarStatus(Long id, StatusOcorrencia novoStatus) {
        Ocorrencia ocorrencia = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ocorrência não encontrada: " + id));

        StatusOcorrencia atual = ocorrencia.getStatus();

        if (atual == StatusOcorrencia.RESOLVIDA && novoStatus != StatusOcorrencia.ABERTA) {
            throw new IllegalStateException("Ocorrência resolvida só pode ser reaberta (status ABERTA)");
        }
        if (atual == StatusOcorrencia.ABERTA && novoStatus == StatusOcorrencia.RESOLVIDA) {
            throw new IllegalStateException("Ocorrência não pode ir de ABERTA para RESOLVIDA sem passar por EM_CORRECAO");
        }
        if (atual == StatusOcorrencia.EM_CORRECAO && novoStatus == StatusOcorrencia.ABERTA) {
            throw new IllegalStateException("Ocorrência em correção não pode voltar para ABERTA");
        }
        if (atual == novoStatus) {
            throw new IllegalStateException("Ocorrência já está no status " + novoStatus);
        }

        ocorrencia.setStatus(novoStatus);
        return repository.save(ocorrencia);
    }
}
