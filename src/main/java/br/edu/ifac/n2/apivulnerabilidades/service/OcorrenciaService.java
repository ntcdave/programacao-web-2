package br.edu.ifac.n2.apivulnerabilidades.service;

import br.edu.ifac.n2.apivulnerabilidades.dto.OcorrenciaRequest;
import br.edu.ifac.n2.apivulnerabilidades.dto.OcorrenciaResponse;
import br.edu.ifac.n2.apivulnerabilidades.model.Aplicacao;
import br.edu.ifac.n2.apivulnerabilidades.model.Ocorrencia;
import br.edu.ifac.n2.apivulnerabilidades.model.StatusOcorrencia;
import br.edu.ifac.n2.apivulnerabilidades.model.Vulnerabilidade;
import br.edu.ifac.n2.apivulnerabilidades.repository.AplicacaoRepository;
import br.edu.ifac.n2.apivulnerabilidades.repository.OcorrenciaRepository;
import br.edu.ifac.n2.apivulnerabilidades.repository.VulnerabilidadeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OcorrenciaService {

    private final OcorrenciaRepository repository;
    private final AplicacaoRepository aplicacaoRepository;
    private final VulnerabilidadeRepository vulnerabilidadeRepository;

    public OcorrenciaService(OcorrenciaRepository repository,
                             AplicacaoRepository aplicacaoRepository,
                             VulnerabilidadeRepository vulnerabilidadeRepository) {
        this.repository = repository;
        this.aplicacaoRepository = aplicacaoRepository;
        this.vulnerabilidadeRepository = vulnerabilidadeRepository;
    }

    public List<OcorrenciaResponse> listarTodas() {
        return repository.findAll()
                .stream()
                .map(OcorrenciaResponse::fromEntity)
                .toList();
    }

    public Optional<OcorrenciaResponse> buscarPorId(Long id) {
        return repository.findById(id)
                .map(OcorrenciaResponse::fromEntity);
    }

    public OcorrenciaResponse salvar(OcorrenciaRequest request) {
        Aplicacao aplicacao = aplicacaoRepository.findById(request.getAplicacaoId())
                .orElseThrow(() -> new IllegalArgumentException("Aplicação não encontrada: " + request.getAplicacaoId()));
        Vulnerabilidade vulnerabilidade = vulnerabilidadeRepository.findById(request.getVulnerabilidadeId())
                .orElseThrow(() -> new IllegalArgumentException("Vulnerabilidade não encontrada: " + request.getVulnerabilidadeId()));

        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setAplicacao(aplicacao);
        ocorrencia.setVulnerabilidade(vulnerabilidade);
        ocorrencia.setDataDescoberta(request.getDataDescoberta());
        ocorrencia.setStatus(StatusOcorrencia.ABERTA);

        return OcorrenciaResponse.fromEntity(repository.save(ocorrencia));
    }

    public OcorrenciaResponse atualizarStatus(Long id, StatusOcorrencia novoStatus) {
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
        return OcorrenciaResponse.fromEntity(repository.save(ocorrencia));
    }
}
