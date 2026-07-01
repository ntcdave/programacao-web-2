package br.edu.ifac.n2.apivulnerabilidades.dto;

import jakarta.validation.constraints.NotBlank;

public class VulnerabilidadeRequest {

    @NotBlank
    private String codigo;

    @NotBlank
    private String descricao;

    @NotBlank
    private String gravidade;

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getGravidade() { return gravidade; }
    public void setGravidade(String gravidade) { this.gravidade = gravidade; }
}
