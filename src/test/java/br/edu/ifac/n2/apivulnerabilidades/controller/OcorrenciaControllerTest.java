package br.edu.ifac.n2.apivulnerabilidades.controller;

import br.edu.ifac.n2.apivulnerabilidades.model.Aplicacao;
import br.edu.ifac.n2.apivulnerabilidades.model.Ocorrencia;
import br.edu.ifac.n2.apivulnerabilidades.model.StatusOcorrencia;
import br.edu.ifac.n2.apivulnerabilidades.model.Vulnerabilidade;
import br.edu.ifac.n2.apivulnerabilidades.service.OcorrenciaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OcorrenciaController.class)
class OcorrenciaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OcorrenciaService service;

    @Test
    void listar_DeveRetornar200ComLista() throws Exception {
        Ocorrencia o = new Ocorrencia();
        o.setId(1L);
        o.setStatus(StatusOcorrencia.ABERTA);

        when(service.listarTodas()).thenReturn(List.of(o));

        mockMvc.perform(get("/api/ocorrencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void listar_QuandoVazio_Retorna200ComListaVazia() throws Exception {
        when(service.listarTodas()).thenReturn(List.of());

        mockMvc.perform(get("/api/ocorrencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void buscarPorId_QuandoExiste_Retorna200() throws Exception {
        Ocorrencia o = new Ocorrencia();
        o.setId(1L);
        o.setStatus(StatusOcorrencia.ABERTA);

        when(service.buscarPorId(1L)).thenReturn(Optional.of(o));

        mockMvc.perform(get("/api/ocorrencias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void buscarPorId_QuandoNaoExiste_Retorna404() throws Exception {
        when(service.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/ocorrencias/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void criar_ComDadosValidos_Retorna200() throws Exception {
        Aplicacao app = new Aplicacao();
        app.setId(1L);
        Vulnerabilidade vuln = new Vulnerabilidade();
        vuln.setId(1L);

        Ocorrencia request = new Ocorrencia();
        request.setAplicacao(app);
        request.setVulnerabilidade(vuln);
        request.setDataDescoberta(LocalDate.now());
        request.setStatus(StatusOcorrencia.ABERTA);

        Ocorrencia salva = new Ocorrencia();
        salva.setId(1L);
        salva.setStatus(StatusOcorrencia.ABERTA);

        when(service.salvar(any())).thenReturn(salva);

        mockMvc.perform(post("/api/ocorrencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void atualizarStatus_TransicaoValida_Retorna200() throws Exception {
        Ocorrencia atualizada = new Ocorrencia();
        atualizada.setId(1L);
        atualizada.setStatus(StatusOcorrencia.EM_CORRECAO);

        when(service.atualizarStatus(1L, StatusOcorrencia.EM_CORRECAO)).thenReturn(atualizada);

        mockMvc.perform(patch("/api/ocorrencias/1/status")
                        .param("status", "EM_CORRECAO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_CORRECAO"));
    }

    @Test
    void atualizarStatus_QuandoNaoEncontrada_Retorna404() throws Exception {
        when(service.atualizarStatus(99L, StatusOcorrencia.EM_CORRECAO))
                .thenThrow(new IllegalArgumentException("Ocorrência não encontrada: 99"));

        mockMvc.perform(patch("/api/ocorrencias/99/status")
                        .param("status", "EM_CORRECAO"))
                .andExpect(status().isNotFound());
    }

    @Test
    void atualizarStatus_TransicaoInvalida_Retorna400() throws Exception {
        when(service.atualizarStatus(1L, StatusOcorrencia.RESOLVIDA))
                .thenThrow(new IllegalStateException("Ocorrência não pode ir de ABERTA para RESOLVIDA sem passar por EM_CORRECAO"));

        mockMvc.perform(patch("/api/ocorrencias/1/status")
                        .param("status", "RESOLVIDA"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void atualizarStatus_StatusInvalido_Retorna400() throws Exception {
        mockMvc.perform(patch("/api/ocorrencias/1/status")
                        .param("status", "INEXISTENTE"))
                .andExpect(status().isBadRequest());
    }
}
