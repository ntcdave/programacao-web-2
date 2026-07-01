package br.edu.ifac.n2.apivulnerabilidades.controller;

import br.edu.ifac.n2.apivulnerabilidades.model.Aplicacao;
import br.edu.ifac.n2.apivulnerabilidades.service.AplicacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aplicacoes")
public class AplicacaoController {

    private final AplicacaoService service;

    public AplicacaoController(AplicacaoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Aplicacao> listar() {
        return service.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aplicacao> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Aplicacao criar(@Valid @RequestBody Aplicacao aplicacao) {
        return service.salvar(aplicacao);
    }
}
