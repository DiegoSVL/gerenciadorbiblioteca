package com.example.gerenciadorbiblioteca.controller;

import com.example.gerenciadorbiblioteca.dto.LivroRequestDTO;
import com.example.gerenciadorbiblioteca.dto.LivroResponseDTO;
import com.example.gerenciadorbiblioteca.model.GeneroEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/livros")
@RequiredArgsConstructor
public class LivroController {

    @PostMapping
    @Operation(summary = "Criar um novo livro", description = "Cadastra um novo livro no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Livro criado com sucesso")
    })
    public ResponseEntity<LivroResponseDTO> criar(@RequestBody @Valid LivroRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(null);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar livro por ID", description = "Retorna os detalhes de um livro específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livro encontrado"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    })
    public ResponseEntity<LivroResponseDTO> buscarPorId(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(null);
    }

    @GetMapping
    @Operation(summary = "Listar livros", description = "Retorna uma lista de livros, podendo filtrar por gênero")
    @ApiResponse(responseCode = "200", description = "Lista de livros retornada com sucesso")
    public ResponseEntity<Page<LivroResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(required = false) GeneroEnum genero) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(null);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar livro", description = "Atualiza as informações de um livro existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livro atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado"),
    })
    public ResponseEntity<LivroResponseDTO> atualizar(@PathVariable String id, @RequestBody @Valid LivroRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(null);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable String id) {

    }
}