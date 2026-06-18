package com.althabeauty.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Servico servico;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private String status = "PENDENTE";
}
