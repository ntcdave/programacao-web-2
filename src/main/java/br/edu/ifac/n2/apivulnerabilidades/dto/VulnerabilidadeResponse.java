package br.edu.ifac.n2.apivulnerabilidades.dto;

import br.edu.ifac.n2.apivulnerabilidades.model.Vulnerabilidade;

public class VulnerabilidadeResponse {

    private Long id;
    private String codigo;
    private String descricao;
    private String gravidade;

    public VulnerabilidadeResponse() {}

    public VulnerabilidadeResponse(Long id, String codigo, String descricao, String gravidade) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.gravidade = gravidade;
    }

    public static VulnerabilidadeResponse fromEntity(Vulnerabilidade v) {
        return new VulnerabilidadeResponse(v.getId(), v.getCodigo(), v.getDescricao(), v.getGravidade());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getGravidade() { return gravidade; }
    public void setGravidade(String gravidade) { this.gravidade = gravidade; }
}
