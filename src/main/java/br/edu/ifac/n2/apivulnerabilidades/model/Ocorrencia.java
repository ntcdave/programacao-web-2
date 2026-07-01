package br.edu.ifac.n2.apivulnerabilidades.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "ocorrencia")
public class Ocorrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "aplicacao_id")
    private Aplicacao aplicacao;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "vulnerabilidade_id")
    private Vulnerabilidade vulnerabilidade;

    @NotNull
    @Column(nullable = false)
    private LocalDate dataDescoberta;

    @Enumerated(EnumType.STRING)
    private StatusOcorrencia status;

    public Ocorrencia() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Aplicacao getAplicacao() { return aplicacao; }
    public void setAplicacao(Aplicacao aplicacao) { this.aplicacao = aplicacao; }
    public Vulnerabilidade getVulnerabilidade() { return vulnerabilidade; }
    public void setVulnerabilidade(Vulnerabilidade vulnerabilidade) { this.vulnerabilidade = vulnerabilidade; }
    public LocalDate getDataDescoberta() { return dataDescoberta; }
    public void setDataDescoberta(LocalDate dataDescoberta) { this.dataDescoberta = dataDescoberta; }
    public StatusOcorrencia getStatus() { return status; }
    public void setStatus(StatusOcorrencia status) { this.status = status; }
}