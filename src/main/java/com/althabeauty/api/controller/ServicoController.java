package com.althabeauty.api.controller;

import com.althabeauty.api.model.Servico;
import com.althabeauty.api.service.ServicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/servicos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ServicoController {

    private final ServicoService servicoService;

    @GetMapping
    public List<Servico> listar() {
        return servicoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Servico buscar(@PathVariable Long id) {
        return servicoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Servico criar(@Valid @RequestBody Servico servico) {
        return servicoService.salvar(servico);
    }

    @PutMapping("/{id}")
    public Servico atualizar(@PathVariable Long id, @Valid @RequestBody Servico servico) {
        return servicoService.atualizar(id, servico);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        servicoService.deletar(id);
    }
}
