package com.example.gerenciadorbiblioteca.service;


import com.example.gerenciadorbiblioteca.dto.LivroRequestDTO;
import com.example.gerenciadorbiblioteca.dto.LivroResponseDTO;
import com.example.gerenciadorbiblioteca.model.GeneroEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LivroService {

    public LivroResponseDTO criar(LivroRequestDTO dto) {
        return new LivroResponseDTO("teste","teste","teste","teste",2026, GeneroEnum.BIOGRAFIA,true, LocalDateTime.now(), LocalDateTime.now());
    }

    public LivroResponseDTO buscarPorId(String id) {
        return new LivroResponseDTO("teste","teste","teste","teste",2026, GeneroEnum.BIOGRAFIA,true, LocalDateTime.now(), LocalDateTime.now());
    }

    public Page<LivroResponseDTO> listar(GeneroEnum genero, Pageable pageable) {
        return new PageImpl<>(List.of(
                new LivroResponseDTO("teste","teste","teste","teste",2026, GeneroEnum.BIOGRAFIA,true, LocalDateTime.now(), LocalDateTime.now()),
                new LivroResponseDTO("teste","teste","teste","teste",2026, GeneroEnum.BIOGRAFIA,true, LocalDateTime.now(), LocalDateTime.now())
        ), pageable, 0);
    }

    public LivroResponseDTO atualizar(String id, LivroRequestDTO dto) {
        return new LivroResponseDTO("teste","teste","teste","teste",2026, GeneroEnum.BIOGRAFIA,true, LocalDateTime.now(), LocalDateTime.now());
    }

    public void excluir(String id) {

    }
}