package br.edu.ifac.n2.apivulnerabilidades.repository;

import br.edu.ifac.n2.apivulnerabilidades.model.Aplicacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AplicacaoRepository extends JpaRepository<Aplicacao, Long> {
}