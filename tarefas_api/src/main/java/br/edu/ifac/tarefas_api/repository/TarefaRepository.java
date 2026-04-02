package br.edu.ifac.tarefas_api.repository;

import br.edu.ifac.tarefas_api.model.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    // Só de estender o JpaRepository, você já ganha o método .save() de graça!
}