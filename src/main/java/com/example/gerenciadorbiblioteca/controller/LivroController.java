package com.example.gerenciadorbiblioteca.controller;

import com.example.gerenciadorbiblioteca.dto.ListarLivroResponseDTO;
import com.example.gerenciadorbiblioteca.dto.LivroRequestDTO;
import com.example.gerenciadorbiblioteca.dto.LivroResponseDTO;
import com.example.gerenciadorbiblioteca.dto.MetaDTO;
import com.example.gerenciadorbiblioteca.model.GeneroEnum;
import com.example.gerenciadorbiblioteca.service.LivroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/livros")
@RequiredArgsConstructor
public class LivroController {

    private final LivroService service;

    @PostMapping
    @Operation(summary = "Criar um novo livro", description = "Cadastra um novo livro no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Livro criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<LivroResponseDTO> criar(@RequestBody @Valid LivroRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar livro por ID", description = "Retorna os detalhes de um livro específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livro encontrado"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    })
    public ResponseEntity<LivroResponseDTO> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    @Operation(summary = "Listar livros", description = "Retorna uma lista de livros, podendo filtrar por gênero")
    @ApiResponse(responseCode = "200", description = "Lista de livros retornada com sucesso")
    public ResponseEntity<ListarLivroResponseDTO> listar(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(required = false) GeneroEnum genero) {
        Pageable pageable = PageRequest.of(pagina, tamanho);
        Page<LivroResponseDTO> responseDTOPage = service.listar(genero, pageable);
        List<LivroResponseDTO> items = responseDTOPage.getContent();
        MetaDTO meta = new MetaDTO(
                responseDTOPage.getPageable().getPageNumber(),
                responseDTOPage.getPageable().getPageSize(),
                responseDTOPage.getNumberOfElements(),
                responseDTOPage.getTotalPages());

        return ResponseEntity.ok(new ListarLivroResponseDTO(items, meta));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar livro", description = "Atualiza as informações de um livro existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livro atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<LivroResponseDTO> atualizar(@PathVariable String id, @RequestBody @Valid LivroRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Excluir livro", description = "Remove um livro do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Livro excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    })
    public void excluir(@PathVariable String id) {
        service.excluir(id);
    }
}