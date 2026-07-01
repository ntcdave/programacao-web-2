package br.edu.ifac.n2.apivulnerabilidades.controller;

import br.edu.ifac.n2.apivulnerabilidades.dto.VulnerabilidadeResponse;
import br.edu.ifac.n2.apivulnerabilidades.service.VulnerabilidadeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VulnerabilidadeController.class)
class VulnerabilidadeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VulnerabilidadeService service;

    @Test
    void listar_DeveRetornar200ComLista() throws Exception {
        when(service.listarTodas()).thenReturn(List.of(
                new VulnerabilidadeResponse(1L, "CVE-001", "SQL Injection", "ALTA")
        ));

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
}
