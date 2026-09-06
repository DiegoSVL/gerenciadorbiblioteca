package com.example.gerenciadorbiblioteca.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "livros")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Livro {

    @Id
    private String id;

    private String titulo;

    private String autor;

    @Indexed(unique = true)
    private String isbn;

    private Integer anoPublicacao;

    private GeneroEnum genero;

    private Boolean disponivel;

    @CreatedDate
    private LocalDateTime dataInclusao;

    @LastModifiedDate
    private LocalDateTime dataAtualizacao;
}