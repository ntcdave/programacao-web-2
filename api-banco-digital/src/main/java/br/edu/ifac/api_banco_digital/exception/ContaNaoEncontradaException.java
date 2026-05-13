package br.edu.ifac.api_banco_digital.exception;

public class ContaNaoEncontradaException extends RuntimeException {
    public ContaNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
