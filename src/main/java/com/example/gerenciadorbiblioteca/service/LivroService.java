package com.example.gerenciadorbiblioteca.service;


import com.example.gerenciadorbiblioteca.dto.LivroRequestDTO;
import com.example.gerenciadorbiblioteca.dto.LivroResponseDTO;
import com.example.gerenciadorbiblioteca.exception.NegocioException;
import com.example.gerenciadorbiblioteca.model.GeneroEnum;
import com.example.gerenciadorbiblioteca.model.Livro;
import com.example.gerenciadorbiblioteca.repository.LivroRepository;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class LivroService {

    private final LivroRepository repository;
    private final ModelMapper modelMapper;

    public LivroResponseDTO criar(LivroRequestDTO dto) {
        if (repository.existsByIsbn(dto.isbn())) {
            throw new NegocioException("ISBN_JA_EXISTE", "Já existe um livro cadastrado com esse ISBN.");
        }

        Livro livro = modelMapper.map(dto, Livro.class);
        Livro salvo = repository.save(livro);

        return modelMapper.map(salvo, LivroResponseDTO.class);
    }

    public LivroResponseDTO buscarPorId(String id) {
        Livro livro = encontrarLivroPorId(id);
        return modelMapper.map(livro, LivroResponseDTO.class);
    }

    public Page<LivroResponseDTO> listar(GeneroEnum genero, Pageable pageable) {
        Page<Livro> livros;
        if (genero != null) {
            livros = repository.findByGenero(genero, pageable);
        } else {
            livros = repository.findAll(pageable);
        }
        return livros.map(livro -> modelMapper.map(livro, LivroResponseDTO.class));
    }

    public LivroResponseDTO atualizar(String id, LivroRequestDTO dto) {
        Livro livroExistente = encontrarLivroPorId(id);

        repository.findByIsbn(dto.isbn()).ifPresent(l -> {
                    if (!l.getId().equals(id)) {
                        throw new NegocioException("ISBN_DUPLICADO", "Livro com ISBN " + dto.isbn() + " já cadastrado.");
                    }
                }
        );

        modelMapper.map(dto, livroExistente);
        livroExistente.setDataAtualizacao(LocalDateTime.now());

        return modelMapper.map(repository.save(livroExistente), LivroResponseDTO.class);
    }

    public void excluir(String id) {
        if (!repository.existsById(id)) {
            throw new NegocioException("LIVRO_NAO_ENCONTRADO", "Livro com id '" + id + "' não encontrado.");
        }
        repository.deleteById(id);
    }

    private @NonNull Livro encontrarLivroPorId(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NegocioException("LIVRO_NAO_ENCONTRADO", "Livro com id '" + id + "' não encontrado."));
    }
}