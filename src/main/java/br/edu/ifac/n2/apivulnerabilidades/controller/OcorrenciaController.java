package br.edu.ifac.n2.apivulnerabilidades.controller;

import br.edu.ifac.n2.apivulnerabilidades.dto.OcorrenciaRequest;
import br.edu.ifac.n2.apivulnerabilidades.dto.OcorrenciaResponse;
import br.edu.ifac.n2.apivulnerabilidades.model.StatusOcorrencia;
import br.edu.ifac.n2.apivulnerabilidades.service.OcorrenciaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ocorrencias")
public class OcorrenciaController {

    private final OcorrenciaService service;

    public OcorrenciaController(OcorrenciaService service) {
        this.service = service;
    }

    @GetMapping
    public List<OcorrenciaResponse> listar() {
        return service.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OcorrenciaResponse> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public OcorrenciaResponse criar(@Valid @RequestBody OcorrenciaRequest request) {
        return service.salvar(request);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OcorrenciaResponse> atualizarStatus(
            @PathVariable Long id,
            @RequestParam StatusOcorrencia status) {
        try {
            OcorrenciaResponse atualizada = service.atualizarStatus(id, status);
            return ResponseEntity.ok(atualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
