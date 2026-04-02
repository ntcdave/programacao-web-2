package br.edu.ifac.tarefas_api.controller;

import br.edu.ifac.tarefas_api.dto.TarefaRequestDTO;
import br.edu.ifac.tarefas_api.model.Tarefa;
import br.edu.ifac.tarefas_api.repository.TarefaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {

    @Autowired
    private TarefaRepository repository;

    @PostMapping
    public ResponseEntity<Tarefa> criarTarefa(@Valid @RequestBody TarefaRequestDTO dto) {

        Tarefa novaTarefa = new Tarefa();
        novaTarefa.setTitulo(dto.getTitulo());
        novaTarefa.setDescricao(dto.getDescricao());
        novaTarefa.setDataVencimento(dto.getDataVencimento());
        novaTarefa.setConcluida(false);

        Tarefa tarefaSalva = repository.save(novaTarefa);

        return ResponseEntity.status(HttpStatus.CREATED).body(tarefaSalva);
    }
}