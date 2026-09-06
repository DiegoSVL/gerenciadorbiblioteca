package com.example.gerenciadorbiblioteca.dto;

import com.example.gerenciadorbiblioteca.model.GeneroEnum;
import java.time.LocalDateTime;

public record LivroResponseDTO(String id,
                               String titulo,
                               String autor,
                               String isbn,
                               Integer anoPublicacao,
                               GeneroEnum genero,
                               Boolean disponivel,
                               LocalDateTime dataInclusao,
                               LocalDateTime dataAtualizacao) {
}
