package br.edu.ifac.n2.apivulnerabilidades.service;

import br.edu.ifac.n2.apivulnerabilidades.dto.VulnerabilidadeRequest;
import br.edu.ifac.n2.apivulnerabilidades.dto.VulnerabilidadeResponse;
import br.edu.ifac.n2.apivulnerabilidades.model.Vulnerabilidade;
import br.edu.ifac.n2.apivulnerabilidades.repository.VulnerabilidadeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VulnerabilidadeService {

    private final VulnerabilidadeRepository repository;

    public VulnerabilidadeService(VulnerabilidadeRepository repository) {
        this.repository = repository;
    }

    public List<VulnerabilidadeResponse> listarTodas() {
        List<VulnerabilidadeResponse> list = new ArrayList<>();
        for (Vulnerabilidade vulnerabilidade : repository.findAll()) {
            VulnerabilidadeResponse vulnerabilidadeResponse = VulnerabilidadeResponse.fromEntity(vulnerabilidade);
            list.add(vulnerabilidadeResponse);
        }
        return list;
    }

    public Optional<VulnerabilidadeResponse> buscarPorId(Long id) {
        return repository.findById(id)
                .map(VulnerabilidadeResponse::fromEntity);
    }

    public VulnerabilidadeResponse salvar(VulnerabilidadeRequest request) {
        Vulnerabilidade entidade = new Vulnerabilidade();
        entidade.setCodigo(request.getCodigo());
        entidade.setDescricao(request.getDescricao());
        entidade.setGravidade(request.getGravidade());
        return VulnerabilidadeResponse.fromEntity(repository.save(entidade));
    }
}
