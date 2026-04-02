package br.edu.ifac.tarefasapi.controller;

import br.edu.ifac.tarefasapi.dto.TarefaRequestDTO;
import br.edu.ifac.tarefasapi.model.Tarefa;
import br.edu.ifac.tarefasapi.repository.TarefaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // Indica que é uma API REST
@RequestMapping("/api/tarefas") // O endereço do endpoint
public class TarefaController {

    @Autowired
    private TarefaRepository repository;

    // O método POST para criar a tarefa
    @PostMapping
    public ResponseEntity<Tarefa> criarTarefa(@Valid @RequestBody TarefaRequestDTO dto) {

        // 1. Transforma o DTO (que veio do JSON) no Model (que vai pro banco)
        Tarefa novaTarefa = new Tarefa();
        novaTarefa.setTitulo(dto.getTitulo());
        novaTarefa.setDescricao(dto.getDescricao());
        novaTarefa.setDataVencimento(dto.getDataVencimento());
        novaTarefa.setConcluida(false); // Toda tarefa nasce não concluída

        // 2. Salva no banco de dados
        Tarefa tarefaSalva = repository.save(novaTarefa);

        // 3. Retorna o status 201 (Created) e os dados salvos
        return ResponseEntity.status(HttpStatus.CREATED).body(tarefaSalva);
    }
}