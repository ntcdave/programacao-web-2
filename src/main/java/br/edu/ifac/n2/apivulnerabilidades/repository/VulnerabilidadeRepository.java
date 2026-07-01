package br.edu.ifac.n2.apivulnerabilidades.repository;

import br.edu.ifac.n2.apivulnerabilidades.model.Vulnerabilidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VulnerabilidadeRepository extends JpaRepository<Vulnerabilidade, Long> {
}