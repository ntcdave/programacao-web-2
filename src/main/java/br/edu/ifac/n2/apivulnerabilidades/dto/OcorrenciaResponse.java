package br.edu.ifac.n2.apivulnerabilidades.dto;

import br.edu.ifac.n2.apivulnerabilidades.model.Ocorrencia;
import br.edu.ifac.n2.apivulnerabilidades.model.StatusOcorrencia;
import java.time.LocalDate;

public class OcorrenciaResponse {

    private Long id;
    private AplicacaoResumo aplicacao;
    private VulnerabilidadeResumo vulnerabilidade;
    private LocalDate dataDescoberta;
    private StatusOcorrencia status;

    public static OcorrenciaResponse fromEntity(Ocorrencia o) {
        OcorrenciaResponse r = new OcorrenciaResponse();
        r.id = o.getId();
        if (o.getAplicacao() != null) {
            r.aplicacao = new AplicacaoResumo(o.getAplicacao().getId(), o.getAplicacao().getNome());
        }
        if (o.getVulnerabilidade() != null) {
            r.vulnerabilidade = new VulnerabilidadeResumo(o.getVulnerabilidade().getId(), o.getVulnerabilidade().getCodigo());
        }
        r.dataDescoberta = o.getDataDescoberta();
        r.status = o.getStatus();
        return r;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AplicacaoResumo getAplicacao() {
        return aplicacao;
    }

    public void setAplicacao(AplicacaoResumo aplicacao) {
        this.aplicacao = aplicacao;
    }

    public VulnerabilidadeResumo getVulnerabilidade() {
        return vulnerabilidade;
    }

    public void setVulnerabilidade(VulnerabilidadeResumo vulnerabilidade) {
        this.vulnerabilidade = vulnerabilidade;
    }

    public LocalDate getDataDescoberta() {
        return dataDescoberta;
    }

    public void setDataDescoberta(LocalDate dataDescoberta) {
        this.dataDescoberta = dataDescoberta;
    }

    public StatusOcorrencia getStatus() {
        return status;
    }

    public void setStatus(StatusOcorrencia status) {
        this.status = status;
    }

    public static class AplicacaoResumo {
        private Long id;
        private String nome;

        public AplicacaoResumo() {}

        public AplicacaoResumo(Long id, String nome) {
            this.id = id;
            this.nome = nome;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
    }

    public static class VulnerabilidadeResumo {
        private Long id;
        private String codigo;

        public VulnerabilidadeResumo() {}

        public VulnerabilidadeResumo(Long id, String codigo) {
            this.id = id;
            this.codigo = codigo;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getCodigo() { return codigo; }
        public void setCodigo(String codigo) { this.codigo = codigo; }
    }
}
