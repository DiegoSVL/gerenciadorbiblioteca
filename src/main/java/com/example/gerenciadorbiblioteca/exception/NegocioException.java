package com.example.gerenciadorbiblioteca.exception;

import lombok.Getter;

@Getter
public class NegocioException extends RuntimeException {

    private final String codigo;
    private final String mensagem;

    public NegocioException(String codigo, String mensagem) {
        super(mensagem);
        this.codigo = codigo;
        this.mensagem = mensagem;
    }
}
