package br.edu.ifac.n2.apivulnerabilidades.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class OcorrenciaRequest {

    @NotNull
    @Valid
    private EntidadeRef aplicacao;

    @NotNull
    @Valid
    private EntidadeRef vulnerabilidade;

    @NotNull
    private LocalDate dataDescoberta;

    public EntidadeRef getAplicacao() {
        return aplicacao;
    }

    public void setAplicacao(EntidadeRef aplicacao) {
        this.aplicacao = aplicacao;
    }

    public EntidadeRef getVulnerabilidade() {
        return vulnerabilidade;
    }

    public void setVulnerabilidade(EntidadeRef vulnerabilidade) {
        this.vulnerabilidade = vulnerabilidade;
    }

    public LocalDate getDataDescoberta() {
        return dataDescoberta;
    }

    public void setDataDescoberta(LocalDate dataDescoberta) {
        this.dataDescoberta = dataDescoberta;
    }

    public Long getAplicacaoId() {
        return aplicacao != null ? aplicacao.getId() : null;
    }

    public Long getVulnerabilidadeId() {
        return vulnerabilidade != null ? vulnerabilidade.getId() : null;
    }

    public static class EntidadeRef {

        @NotNull
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}
