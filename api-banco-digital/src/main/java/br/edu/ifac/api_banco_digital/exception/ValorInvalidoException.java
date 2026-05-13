package br.edu.ifac.api_banco_digital.exception;

public class ValorInvalidoException extends RuntimeException {
    public ValorInvalidoException(String mensagem) {
        super(mensagem);
    }
}
