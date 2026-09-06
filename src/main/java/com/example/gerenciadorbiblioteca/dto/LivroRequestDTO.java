package com.example.gerenciadorbiblioteca.dto;

import com.example.gerenciadorbiblioteca.model.GeneroEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Year;

public record LivroRequestDTO(
        @NotBlank(message = "O título não pode ser vazio")
        String titulo,

        @NotBlank(message = "O autor não pode ser vazio")
        String autor,

        @NotBlank(message = "O ISBN não pode ser vazio")
        String isbn,

        @NotNull(message = "O ano de publicação não pode ser nulo")
        @Min(value = 1001, message = "O ano de publicação deve ser maior que 1000")
        Integer anoPublicacao,

        @NotNull(message = "O gênero não pode ser nulo")
        GeneroEnum genero,

        Boolean disponivel
) {
    public LivroRequestDTO {
        if (anoPublicacao != null && anoPublicacao > Year.now().getValue()) {
            throw new IllegalArgumentException("O ano de publicação não pode ser maior que o ano atual");
        }
        if (disponivel == null) {
            disponivel = true;
        }
    }
}
