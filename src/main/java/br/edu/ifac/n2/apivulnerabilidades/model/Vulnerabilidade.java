package br.edu.ifac.n2.apivulnerabilidades.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "vulnerabilidade")
public class Vulnerabilidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String codigo;

    @NotBlank
    @Column(nullable = false)
    private String descricao;

    @NotBlank
    @Column(nullable = false)
    private String gravidade;

    public Vulnerabilidade() {}

    public Vulnerabilidade(Long id, String codigo, String gravidade) {
        this.id = id;
        this.codigo = codigo;
        this.gravidade = gravidade;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getGravidade() { return gravidade; }
    public void setGravidade(String gravidade) { this.gravidade = gravidade; }
}