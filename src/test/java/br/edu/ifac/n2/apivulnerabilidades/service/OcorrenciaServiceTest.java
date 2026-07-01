package br.edu.ifac.n2.apivulnerabilidades.service;

import br.edu.ifac.n2.apivulnerabilidades.dto.OcorrenciaRequest;
import br.edu.ifac.n2.apivulnerabilidades.dto.OcorrenciaResponse;
import br.edu.ifac.n2.apivulnerabilidades.model.Aplicacao;
import br.edu.ifac.n2.apivulnerabilidades.model.Ocorrencia;
import br.edu.ifac.n2.apivulnerabilidades.model.StatusOcorrencia;
import br.edu.ifac.n2.apivulnerabilidades.model.Vulnerabilidade;
import br.edu.ifac.n2.apivulnerabilidades.repository.AplicacaoRepository;
import br.edu.ifac.n2.apivulnerabilidades.repository.OcorrenciaRepository;
import br.edu.ifac.n2.apivulnerabilidades.repository.VulnerabilidadeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OcorrenciaServiceTest {

    @Mock
    private OcorrenciaRepository repository;

    @Mock
    private AplicacaoRepository aplicacaoRepository;

    @Mock
    private VulnerabilidadeRepository vulnerabilidadeRepository;

    @InjectMocks
    private OcorrenciaService service;

    @Test
    void listarTodas_DeveRetornarLista() {
        Ocorrencia o = new Ocorrencia();
        o.setId(1L);
        o.setStatus(StatusOcorrencia.ABERTA);

        when(repository.findAll()).thenReturn(List.of(o));

        List<OcorrenciaResponse> resultado = service.listarTodas();

        assertEquals(1, resultado.size());
        assertNotNull(resultado.get(0).getId());
    }

    @Test
    void listarTodas_QuandoVazio_RetornaListaVazia() {
        when(repository.findAll()).thenReturn(List.of());

        List<OcorrenciaResponse> resultado = service.listarTodas();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorId_QuandoExiste_RetornaOptionalComDados() {
        Aplicacao app = new Aplicacao();
        app.setId(1L);
        app.setNome("App Teste");

        Vulnerabilidade vuln = new Vulnerabilidade();
        vuln.setId(1L);
        vuln.setCodigo("CVE-2024-001");

        Ocorrencia o = new Ocorrencia();
        o.setId(1L);
        o.setAplicacao(app);
        o.setVulnerabilidade(vuln);
        o.setDataDescoberta(LocalDate.now());
        o.setStatus(StatusOcorrencia.ABERTA);

        when(repository.findById(1L)).thenReturn(Optional.of(o));

        Optional<OcorrenciaResponse> resultado = service.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        assertEquals("App Teste", resultado.get().getAplicacao().getNome());
    }

    @Test
    void buscarPorId_QuandoNaoExiste_RetornaOptionalVazio() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Optional<OcorrenciaResponse> resultado = service.buscarPorId(99L);

        assertFalse(resultado.isPresent());
    }

    @Test
    void salvar_DeveDefinirStatusAbertaEPersistir() {
        Aplicacao app = new Aplicacao();
        app.setId(1L);
        app.setNome("App");

        Vulnerabilidade vuln = new Vulnerabilidade();
        vuln.setId(1L);
        vuln.setCodigo("CVE-001");

        Ocorrencia salva = new Ocorrencia();
        salva.setId(1L);
        salva.setAplicacao(app);
        salva.setVulnerabilidade(vuln);
        salva.setDataDescoberta(LocalDate.now());
        salva.setStatus(StatusOcorrencia.ABERTA);

        when(aplicacaoRepository.findById(1L)).thenReturn(Optional.of(app));
        when(vulnerabilidadeRepository.findById(1L)).thenReturn(Optional.of(vuln));
        when(repository.save(any(Ocorrencia.class))).thenReturn(salva);

        OcorrenciaRequest request = new OcorrenciaRequest();
        OcorrenciaRequest.EntidadeRef refApp = new OcorrenciaRequest.EntidadeRef();
        refApp.setId(1L);
        request.setAplicacao(refApp);
        OcorrenciaRequest.EntidadeRef refVuln = new OcorrenciaRequest.EntidadeRef();
        refVuln.setId(1L);
        request.setVulnerabilidade(refVuln);
        request.setDataDescoberta(LocalDate.now());

        OcorrenciaResponse resultado = service.salvar(request);

        assertEquals(StatusOcorrencia.ABERTA, resultado.getStatus());
        assertEquals(1L, resultado.getId());
        verify(repository, times(1)).save(any(Ocorrencia.class));
    }

    @Test
    void salvar_QuandoAplicacaoNaoExiste_LancaErro() {
        when(aplicacaoRepository.findById(99L)).thenReturn(Optional.empty());

        OcorrenciaRequest request = new OcorrenciaRequest();
        OcorrenciaRequest.EntidadeRef refApp = new OcorrenciaRequest.EntidadeRef();
        refApp.setId(99L);
        request.setAplicacao(refApp);
        OcorrenciaRequest.EntidadeRef refVuln = new OcorrenciaRequest.EntidadeRef();
        refVuln.setId(1L);
        request.setVulnerabilidade(refVuln);

        assertThrows(IllegalArgumentException.class,
                () -> service.salvar(request));
        verify(repository, never()).save(any());
    }

    @Test
    void salvar_QuandoVulnerabilidadeNaoExiste_LancaErro() {
        Aplicacao app = new Aplicacao();
        app.setId(1L);

        when(aplicacaoRepository.findById(1L)).thenReturn(Optional.of(app));
        when(vulnerabilidadeRepository.findById(99L)).thenReturn(Optional.empty());

        OcorrenciaRequest request = new OcorrenciaRequest();
        OcorrenciaRequest.EntidadeRef refApp = new OcorrenciaRequest.EntidadeRef();
        refApp.setId(1L);
        request.setAplicacao(refApp);
        OcorrenciaRequest.EntidadeRef refVuln = new OcorrenciaRequest.EntidadeRef();
        refVuln.setId(99L);
        request.setVulnerabilidade(refVuln);

        assertThrows(IllegalArgumentException.class,
                () -> service.salvar(request));
        verify(repository, never()).save(any());
    }

    @Test
    void atualizarStatus_AbertaParaEmCorrecao_DeveAtualizar() {
        Aplicacao app = new Aplicacao();
        app.setId(1L);
        app.setNome("App");

        Vulnerabilidade vuln = new Vulnerabilidade();
        vuln.setId(1L);
        vuln.setCodigo("CVE-001");

        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setId(1L);
        ocorrencia.setStatus(StatusOcorrencia.ABERTA);
        ocorrencia.setAplicacao(app);
        ocorrencia.setVulnerabilidade(vuln);

        when(repository.findById(1L)).thenReturn(Optional.of(ocorrencia));
        when(repository.save(any(Ocorrencia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OcorrenciaResponse resultado = service.atualizarStatus(1L, StatusOcorrencia.EM_CORRECAO);

        assertEquals(StatusOcorrencia.EM_CORRECAO, resultado.getStatus());
    }

    @Test
    void atualizarStatus_EmCorrecaoParaResolvida_DeveAtualizar() {
        Aplicacao app = new Aplicacao();
        app.setId(1L);

        Vulnerabilidade vuln = new Vulnerabilidade();
        vuln.setId(1L);

        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setId(1L);
        ocorrencia.setStatus(StatusOcorrencia.EM_CORRECAO);
        ocorrencia.setAplicacao(app);
        ocorrencia.setVulnerabilidade(vuln);

        when(repository.findById(1L)).thenReturn(Optional.of(ocorrencia));
        when(repository.save(any(Ocorrencia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OcorrenciaResponse resultado = service.atualizarStatus(1L, StatusOcorrencia.RESOLVIDA);

        assertEquals(StatusOcorrencia.RESOLVIDA, resultado.getStatus());
    }

    @Test
    void atualizarStatus_ResolvidaParaAberta_DeveReabrir() {
        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setId(1L);
        ocorrencia.setStatus(StatusOcorrencia.RESOLVIDA);

        when(repository.findById(1L)).thenReturn(Optional.of(ocorrencia));
        when(repository.save(any(Ocorrencia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OcorrenciaResponse resultado = service.atualizarStatus(1L, StatusOcorrencia.ABERTA);

        assertEquals(StatusOcorrencia.ABERTA, resultado.getStatus());
    }

    @Test
    void atualizarStatus_ResolvidaParaEmCorrecao_DeveLancarErro() {
        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setId(1L);
        ocorrencia.setStatus(StatusOcorrencia.RESOLVIDA);

        when(repository.findById(1L)).thenReturn(Optional.of(ocorrencia));

        assertThrows(IllegalStateException.class,
                () -> service.atualizarStatus(1L, StatusOcorrencia.EM_CORRECAO));
        verify(repository, never()).save(any());
    }

    @Test
    void atualizarStatus_AbertaParaResolvida_DeveLancarErro() {
        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setId(1L);
        ocorrencia.setStatus(StatusOcorrencia.ABERTA);

        when(repository.findById(1L)).thenReturn(Optional.of(ocorrencia));

        assertThrows(IllegalStateException.class,
                () -> service.atualizarStatus(1L, StatusOcorrencia.RESOLVIDA));
        verify(repository, never()).save(any());
    }

    @Test
    void atualizarStatus_EmCorrecaoParaAberta_DeveLancarErro() {
        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setId(1L);
        ocorrencia.setStatus(StatusOcorrencia.EM_CORRECAO);

        when(repository.findById(1L)).thenReturn(Optional.of(ocorrencia));

        assertThrows(IllegalStateException.class,
                () -> service.atualizarStatus(1L, StatusOcorrencia.ABERTA));
        verify(repository, never()).save(any());
    }

    @Test
    void atualizarStatus_MesmoStatus_DeveLancarErro() {
        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setId(1L);
        ocorrencia.setStatus(StatusOcorrencia.ABERTA);

        when(repository.findById(1L)).thenReturn(Optional.of(ocorrencia));

        assertThrows(IllegalStateException.class,
                () -> service.atualizarStatus(1L, StatusOcorrencia.ABERTA));
        verify(repository, never()).save(any());
    }

    @Test
    void atualizarStatus_QuandoNaoEncontrada_DeveLancarErro() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.atualizarStatus(99L, StatusOcorrencia.EM_CORRECAO));
        verify(repository, never()).save(any());
    }
}
