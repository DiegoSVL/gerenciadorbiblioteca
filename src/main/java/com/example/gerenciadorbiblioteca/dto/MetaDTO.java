package com.example.gerenciadorbiblioteca.dto;

public record MetaDTO (
    int page,
    int limit,
    int totalItems,
    int totalPages
){
}
