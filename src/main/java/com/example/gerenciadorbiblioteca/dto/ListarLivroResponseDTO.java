package com.example.gerenciadorbiblioteca.dto;

import java.util.List;


public record ListarLivroResponseDTO (
    List<LivroResponseDTO> items,
    MetaDTO meta
){
}
