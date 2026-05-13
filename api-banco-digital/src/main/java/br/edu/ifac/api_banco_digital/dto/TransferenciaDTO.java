package br.edu.ifac.api_banco_digital.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferenciaDTO {

    @NotBlank(message = "Número da conta de origem é obrigatório")
    private String numeroContaOrigem;

    @NotBlank(message = "Número da conta de destino é obrigatório")
    private String numeroContaDestino;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;
}
