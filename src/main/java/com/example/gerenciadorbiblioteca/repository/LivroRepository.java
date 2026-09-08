package com.example.gerenciadorbiblioteca.repository;

import com.example.gerenciadorbiblioteca.model.GeneroEnum;
import com.example.gerenciadorbiblioteca.model.Livro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LivroRepository  extends MongoRepository<Livro, String> {

    Optional<Livro> findByIsbn(String isbn);

    Page<Livro> findByGenero(GeneroEnum genero, Pageable pageable);

    boolean existsByIsbn(String isbn);
}