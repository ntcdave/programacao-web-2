package br.edu.ifac.n2.apivulnerabilidades.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name = "aplicacao")
public class Aplicacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @NotBlank
    @Column(nullable = false)
    private String linguagem;

    private String repositorioUrl;

    @OneToMany(mappedBy = "aplicacao", cascade = CascadeType.ALL)
    private List<Ocorrencia> ocorrencias;

    public Aplicacao() {}

    public Aplicacao(Long id, String nome, String linguagem, String repositorioUrl) {
        this.id = id;
        this.nome = nome;
        this.linguagem = linguagem;
        this.repositorioUrl = repositorioUrl;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getLinguagem() { return linguagem; }
    public void setLinguagem(String linguagem) { this.linguagem = linguagem; }
    public String getRepositorioUrl() { return repositorioUrl; }
    public void setRepositorioUrl(String repositorioUrl) { this.repositorioUrl = repositorioUrl; }
    public List<Ocorrencia> getOcorrencias() { return ocorrencias; }
    public void setOcorrencias(List<Ocorrencia> ocorrencias) { this.ocorrencias = ocorrencias; }
}