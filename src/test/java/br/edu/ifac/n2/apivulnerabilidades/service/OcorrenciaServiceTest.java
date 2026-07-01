package br.edu.ifac.n2.apivulnerabilidades.service;

import br.edu.ifac.n2.apivulnerabilidades.model.Ocorrencia;
import br.edu.ifac.n2.apivulnerabilidades.model.StatusOcorrencia;
import br.edu.ifac.n2.apivulnerabilidades.repository.OcorrenciaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OcorrenciaServiceTest {

    @Mock
    private OcorrenciaRepository repository;

    @InjectMocks
    private OcorrenciaService service;

    @Test
    void listarTodas_DeveRetornarLista() {
        when(repository.findAll()).thenReturn(List.of(new Ocorrencia(), new Ocorrencia()));

        List<Ocorrencia> resultado = service.listarTodas();

        assertEquals(2, resultado.size());
    }

    @Test
    void listarTodas_QuandoVazio_RetornaListaVazia() {
        when(repository.findAll()).thenReturn(List.of());

        List<Ocorrencia> resultado = service.listarTodas();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorId_QuandoExiste_RetornaOptionalComDados() {
        Ocorrencia o = new Ocorrencia();
        o.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(o));

        Optional<Ocorrencia> resultado = service.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
    }

    @Test
    void buscarPorId_QuandoNaoExiste_RetornaOptionalVazio() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Optional<Ocorrencia> resultado = service.buscarPorId(99L);

        assertFalse(resultado.isPresent());
    }

    @Test
    void salvar_DeveDefinirStatusAbertaEPersistir() {
        Ocorrencia ocorrencia = new Ocorrencia();
        Ocorrencia salva = new Ocorrencia();
        salva.setId(1L);
        salva.setStatus(StatusOcorrencia.ABERTA);

        when(repository.save(any(Ocorrencia.class))).thenReturn(salva);

        Ocorrencia resultado = service.salvar(ocorrencia);

        assertEquals(StatusOcorrencia.ABERTA, resultado.getStatus());
        verify(repository, times(1)).save(any(Ocorrencia.class));
    }

    @Test
    void atualizarStatus_AbertaParaEmCorrecao_DeveAtualizar() {
        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setId(1L);
        ocorrencia.setStatus(StatusOcorrencia.ABERTA);

        when(repository.findById(1L)).thenReturn(Optional.of(ocorrencia));
        when(repository.save(any(Ocorrencia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ocorrencia resultado = service.atualizarStatus(1L, StatusOcorrencia.EM_CORRECAO);

        assertEquals(StatusOcorrencia.EM_CORRECAO, resultado.getStatus());
    }

    @Test
    void atualizarStatus_EmCorrecaoParaResolvida_DeveAtualizar() {
        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setId(1L);
        ocorrencia.setStatus(StatusOcorrencia.EM_CORRECAO);

        when(repository.findById(1L)).thenReturn(Optional.of(ocorrencia));
        when(repository.save(any(Ocorrencia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ocorrencia resultado = service.atualizarStatus(1L, StatusOcorrencia.RESOLVIDA);

        assertEquals(StatusOcorrencia.RESOLVIDA, resultado.getStatus());
    }

    @Test
    void atualizarStatus_ResolvidaParaAberta_DeveReabrir() {
        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setId(1L);
        ocorrencia.setStatus(StatusOcorrencia.RESOLVIDA);

        when(repository.findById(1L)).thenReturn(Optional.of(ocorrencia));
        when(repository.save(any(Ocorrencia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ocorrencia resultado = service.atualizarStatus(1L, StatusOcorrencia.ABERTA);

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
