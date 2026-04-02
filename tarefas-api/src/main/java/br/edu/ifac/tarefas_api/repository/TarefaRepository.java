package br.edu.ifac.tarefasapi.repository;

import br.edu.ifac.tarefasapi.model.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    // Só de estender o JpaRepository, você já ganha o método .save() de graça!
}