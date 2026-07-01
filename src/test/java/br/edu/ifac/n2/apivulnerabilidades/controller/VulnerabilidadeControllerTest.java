package br.edu.ifac.n2.apivulnerabilidades.controller;

import br.edu.ifac.n2.apivulnerabilidades.dto.VulnerabilidadeRequest;
import br.edu.ifac.n2.apivulnerabilidades.dto.VulnerabilidadeResponse;
import br.edu.ifac.n2.apivulnerabilidades.service.VulnerabilidadeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VulnerabilidadeController.class)
class VulnerabilidadeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VulnerabilidadeService service;

    @Test
    void listar_DeveRetornar200ComLista() throws Exception {
        List<VulnerabilidadeResponse> t = new java.util.ArrayList<>();
        t.add(new VulnerabilidadeResponse(1L, "CVE-001", "SQL Injection", "ALTA"));
        when(service.listarTodas()).thenReturn(t);

        mockMvc.perform(get("/api/vulnerabilidades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("CVE-001"));
    }

    @Test
    void buscarPorId_QuandoExiste_Retorna200() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(
                Optional.of(new VulnerabilidadeResponse(1L, "CVE-001", "SQL Injection", "ALTA"))
        );

        mockMvc.perform(get("/api/vulnerabilidades/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("CVE-001"));
    }

    @Test
    void buscarPorId_QuandoNaoExiste_Retorna404() throws Exception {
        when(service.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/vulnerabilidades/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void criar_ComDadosValidos_Retorna201() throws Exception {
        VulnerabilidadeRequest request = new VulnerabilidadeRequest();
        request.setCodigo("CVE-003");
        request.setDescricao("XSS");
        request.setGravidade("ALTA");

        VulnerabilidadeResponse response = new VulnerabilidadeResponse(3L, "CVE-003", "XSS", "ALTA");

        when(service.salvar(any(VulnerabilidadeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/vulnerabilidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("CVE-003"))
                .andExpect(jsonPath("$.gravidade").value("ALTA"));
    }

    @Test
    void criar_ComDadosInvalidos_Retorna400() throws Exception {
        VulnerabilidadeRequest request = new VulnerabilidadeRequest();

        mockMvc.perform(post("/api/vulnerabilidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listar_QuandoVazio_Retorna200ComListaVazia() throws Exception {
        when(service.listarTodas()).thenReturn(List.of());

        mockMvc.perform(get("/api/vulnerabilidades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
