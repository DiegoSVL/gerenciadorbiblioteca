package com.example.gerenciadorbiblioteca.dto;

import com.example.gerenciadorbiblioteca.model.GeneroEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LivroResponseDTO {
    private String id;
    private String titulo;
    private String autor;
    private String isbn;
    private Integer anoPublicacao;
    private GeneroEnum genero;
    private Boolean disponivel;
    private LocalDateTime dataInclusao;
    private LocalDateTime dataAtualizacao;
}