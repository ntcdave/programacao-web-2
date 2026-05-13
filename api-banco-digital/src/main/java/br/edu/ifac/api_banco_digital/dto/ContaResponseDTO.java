package br.edu.ifac.api_banco_digital.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContaResponseDTO {
    private Long id;
    private String nomeTitular;
    private String numeroConta;
    private BigDecimal saldo;
}
