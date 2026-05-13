package br.edu.ifac.api_banco_digital.controller;

import br.edu.ifac.api_banco_digital.dto.*;
import br.edu.ifac.api_banco_digital.service.ContaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/contas")
public class ContaController {

    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    // POST /api/contas → 201
    @PostMapping
    public ResponseEntity<ContaResponseDTO> criarConta(@Valid @RequestBody ContaRequestDTO dto) {
        ContaResponseDTO resposta = contaService.criarConta(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    // GET /api/contas/numero/{numeroConta} → 200 / 404
    @GetMapping("/numero/{numeroConta}")
    public ResponseEntity<ContaResponseDTO> buscarPorNumero(@PathVariable String numeroConta) {
        return ResponseEntity.ok(contaService.buscarPorNumero(numeroConta));
    }

    // GET /api/contas/{id} → 200 / 404
    @GetMapping("/{id}")
    public ResponseEntity<ContaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(contaService.buscarPorId(id));
    }

    // POST /api/contas/{numeroConta}/deposito → 200 / 400 / 404
    @PostMapping("/{numeroConta}/deposito")
    public ResponseEntity<ContaResponseDTO> depositar(
            @PathVariable String numeroConta,
            @Valid @RequestBody OperacaoDTO dto) {
        return ResponseEntity.ok(contaService.depositar(numeroConta, dto));
    }

    // POST /api/contas/{numeroConta}/saque → 200 / 400 / 404 / 422
    @PostMapping("/{numeroConta}/saque")
    public ResponseEntity<ContaResponseDTO> sacar(
            @PathVariable String numeroConta,
            @Valid @RequestBody OperacaoDTO dto) {
        return ResponseEntity.ok(contaService.sacar(numeroConta, dto));
    }

    // POST /api/contas/transferencia → 200 / 400 / 404 / 422
    @PostMapping("/transferencia")
    public ResponseEntity<Map<String, String>> transferir(@Valid @RequestBody TransferenciaDTO dto) {
        contaService.transferir(dto);
        return ResponseEntity.ok(Map.of("mensagem", "Transferência realizada com sucesso"));
    }
}
