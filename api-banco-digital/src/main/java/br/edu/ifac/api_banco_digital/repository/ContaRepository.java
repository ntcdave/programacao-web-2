package br.edu.ifac.api_banco_digital.repository;

import br.edu.ifac.api_banco_digital.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
    Optional<Conta> findByNumeroConta(String numeroConta);
    boolean existsByNumeroConta(String numeroConta);
}
