package com.example.gerenciadorbiblioteca.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ErrorResponse error = new ErrorResponse(
                "ERRO_VALIDACAO",
                mensagem,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<ErrorResponse> handleNegocioException(NegocioException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if ("LIVRO_NAO_ENCONTRADO".equals(ex.getCodigo())) {
            status = HttpStatus.NOT_FOUND;
        }

        ErrorResponse error = new ErrorResponse(
                ex.getCodigo(),
                ex.getMensagem(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(status).body(error);
    }

}