package br.edu.ifac.api_banco_digital.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "conta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nomeTitular;

    @Column(unique = true, nullable = false)
    private String numeroConta;

    @Column(nullable = false)
    private BigDecimal saldo;
}
