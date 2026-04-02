package br.edu.ifac.tarefasapi.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity // Avisa o Spring que isso vai virar uma tabela no banco
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremento (1, 2, 3...)
    private Long id;

    private String titulo;

    private String descricao;

    private LocalDate dataVencimento;

    private boolean concluida;
}