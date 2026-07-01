package br.edu.ifac.n2.apivulnerabilidades.service;

import br.edu.ifac.n2.apivulnerabilidades.model.Aplicacao;
import br.edu.ifac.n2.apivulnerabilidades.repository.AplicacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AplicacaoService {

    private final AplicacaoRepository repository;

    public AplicacaoService(AplicacaoRepository repository) {
        this.repository = repository;
    }

    public List<Aplicacao> listarTodas() {
        return repository.findAll();
    }

    public Optional<Aplicacao> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Aplicacao salvar(Aplicacao aplicacao) {
        return repository.save(aplicacao);
    }
}
