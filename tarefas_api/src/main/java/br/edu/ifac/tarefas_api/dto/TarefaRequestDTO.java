package br.edu.ifac.tarefas_api.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data // Anotação do Lombok para gerar Getters e Setters automaticamente
public class TarefaRequestDTO {

    @NotBlank(message = "O título da tarefa é obrigatório.")
    private String titulo;

    private String descricao;

    @NotNull(message = "A data de vencimento é obrigatória.")
    @FutureOrPresent(message = "A data da tarefa não pode estar no passado.")
    private LocalDate dataVencimento;
    // O Spring converte automaticamente a string "2026-04-05" do JSON para LocalDate
}