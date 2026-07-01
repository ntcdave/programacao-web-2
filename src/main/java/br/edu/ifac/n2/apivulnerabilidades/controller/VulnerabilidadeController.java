package br.edu.ifac.n2.apivulnerabilidades.controller;

import br.edu.ifac.n2.apivulnerabilidades.dto.VulnerabilidadeRequest;
import br.edu.ifac.n2.apivulnerabilidades.dto.VulnerabilidadeResponse;
import br.edu.ifac.n2.apivulnerabilidades.service.VulnerabilidadeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vulnerabilidades")
public class VulnerabilidadeController {

    private final VulnerabilidadeService service;

    public VulnerabilidadeController(VulnerabilidadeService service) {
        this.service = service;
    }

    @GetMapping
    public List<VulnerabilidadeResponse> listar() {
        return service.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VulnerabilidadeResponse> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public VulnerabilidadeResponse criar(@Valid @RequestBody VulnerabilidadeRequest request) {
        return service.salvar(request);
    }
}
