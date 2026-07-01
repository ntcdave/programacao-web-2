package br.edu.ifac.n2.apivulnerabilidades.service;

import br.edu.ifac.n2.apivulnerabilidades.dto.VulnerabilidadeRequest;
import br.edu.ifac.n2.apivulnerabilidades.dto.VulnerabilidadeResponse;
import br.edu.ifac.n2.apivulnerabilidades.model.Vulnerabilidade;
import br.edu.ifac.n2.apivulnerabilidades.repository.VulnerabilidadeRepository;
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
class VulnerabilidadeServiceTest {

    @Mock
    private VulnerabilidadeRepository repository;

    @InjectMocks
    private VulnerabilidadeService service;

    @Test
    void listarTodas_DeveRetornarLista() {
        when(repository.findAll()).thenReturn(List.of(
                new Vulnerabilidade(1L, "CVE-001", "ALTA"),
                new Vulnerabilidade(2L, "CVE-002", "MEDIA")
        ));

        List<VulnerabilidadeResponse> resultado = service.listarTodas();

        assertEquals(2, resultado.size());
        assertEquals("CVE-001", resultado.get(0).getCodigo());
    }

    @Test
    void listarTodas_QuandoVazio_RetornaListaVazia() {
        when(repository.findAll()).thenReturn(List.of());

        List<VulnerabilidadeResponse> resultado = service.listarTodas();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorId_QuandoExiste_RetornaOptionalComDados() {
        when(repository.findById(1L)).thenReturn(Optional.of(
                new Vulnerabilidade(1L, "CVE-001", "ALTA")
        ));

        Optional<VulnerabilidadeResponse> resultado = service.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("CVE-001", resultado.get().getCodigo());
    }

    @Test
    void buscarPorId_QuandoNaoExiste_RetornaOptionalVazio() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Optional<VulnerabilidadeResponse> resultado = service.buscarPorId(99L);

        assertFalse(resultado.isPresent());
    }

    @Test
    void salvar_DevePersistirERetornarResponse() {
        VulnerabilidadeRequest request = new VulnerabilidadeRequest();
        request.setCodigo("CVE-003");
        request.setDescricao("XSS");
        request.setGravidade("ALTA");

        Vulnerabilidade entidade = new Vulnerabilidade();
        entidade.setId(3L);
        entidade.setCodigo("CVE-003");
        entidade.setDescricao("XSS");
        entidade.setGravidade("ALTA");

        when(repository.save(any(Vulnerabilidade.class))).thenReturn(entidade);

        VulnerabilidadeResponse resultado = service.salvar(request);

        assertEquals("CVE-003", resultado.getCodigo());
        assertEquals("XSS", resultado.getDescricao());
        assertEquals("ALTA", resultado.getGravidade());
        assertEquals(3L, resultado.getId());
        verify(repository, times(1)).save(any(Vulnerabilidade.class));
    }

    @Test
    void salvar_DeveMapearCamposCorretamente() {
        VulnerabilidadeRequest request = new VulnerabilidadeRequest();
        request.setCodigo("CVE-004");
        request.setDescricao("CSRF");
        request.setGravidade("MEDIA");

        when(repository.save(any(Vulnerabilidade.class))).thenAnswer(invocation -> {
            Vulnerabilidade v = invocation.getArgument(0);
            v.setId(4L);
            return v;
        });

        VulnerabilidadeResponse resultado = service.salvar(request);

        assertEquals("CVE-004", resultado.getCodigo());
        assertEquals("CSRF", resultado.getDescricao());
        assertEquals("MEDIA", resultado.getGravidade());
    }
}
