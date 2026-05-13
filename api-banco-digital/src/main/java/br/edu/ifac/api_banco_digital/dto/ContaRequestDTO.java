package br.edu.ifac.api_banco_digital.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ContaRequestDTO {

    @NotBlank(message = "Nome do titular é obrigatório")
    private String nomeTitular;

    @NotBlank(message = "Número da conta é obrigatório")
    private String numeroConta;

    @DecimalMin(value = "0.00", message = "Saldo inicial não pode ser negativo")
    private BigDecimal saldo = BigDecimal.ZERO;
}
