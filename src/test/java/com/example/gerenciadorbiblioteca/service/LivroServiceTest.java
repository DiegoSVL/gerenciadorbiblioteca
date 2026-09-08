package com.example.gerenciadorbiblioteca.service;

import com.example.gerenciadorbiblioteca.dto.LivroRequestDTO;
import com.example.gerenciadorbiblioteca.dto.LivroResponseDTO;
import com.example.gerenciadorbiblioteca.exception.NegocioException;
import com.example.gerenciadorbiblioteca.model.GeneroEnum;
import com.example.gerenciadorbiblioteca.model.Livro;
import com.example.gerenciadorbiblioteca.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @Mock
    private LivroRepository repository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private LivroService livroService;

    private LivroRequestDTO livroRequestDTO;
    private Livro livro;
    private LivroResponseDTO livroResponseDTO;

    @BeforeEach
    void setUp() {
        livroRequestDTO = new LivroRequestDTO(
                "Titulo Teste",
                "Autor Teste",
                "1234567890",
                2023,
                GeneroEnum.FICCAO_CIENTIFICA,
                true
        );

        livro = new Livro(
                "1",
                "Titulo Teste",
                "Autor Teste",
                "1234567890",
                2023,
                GeneroEnum.FICCAO_CIENTIFICA,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        livroResponseDTO = new LivroResponseDTO(
                "1",
                "Titulo Teste",
                "Autor Teste",
                "1234567890",
                2023,
                GeneroEnum.FICCAO_CIENTIFICA,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Deve criar um livro com sucesso")
    void criarLivro_sucesso() {
        when(repository.existsByIsbn(anyString())).thenReturn(false);
        when(modelMapper.map(livroRequestDTO, Livro.class)).thenReturn(livro);
        when(repository.save(any(Livro.class))).thenReturn(livro);
        when(modelMapper.map(livro, LivroResponseDTO.class)).thenReturn(livroResponseDTO);

        LivroResponseDTO result = livroService.criar(livroRequestDTO);

        assertNotNull(result);
        assertEquals(livroResponseDTO.getId(), result.getId());
        verify(repository, times(1)).existsByIsbn(anyString());
        verify(repository, times(1)).save(any(Livro.class));
    }

    @Test
    @DisplayName("Deve lançar NegocioException ao tentar criar livro com ISBN já existente")
    void criarLivro_isbnExistente_lancaExcecao() {
        when(repository.existsByIsbn(anyString())).thenReturn(true);

        NegocioException exception = assertThrows(NegocioException.class, () ->
                livroService.criar(livroRequestDTO));

        assertEquals("ISBN_JA_EXISTE", exception.getCodigo());
        assertEquals("Já existe um livro cadastrado com esse ISBN.", exception.getMessage());
        verify(repository, times(1)).existsByIsbn(anyString());
        verify(repository, never()).save(any(Livro.class));
    }

    @Test
    @DisplayName("Deve buscar um livro por ID com sucesso")
    void buscarPorId_sucesso() {
        when(repository.findById(anyString())).thenReturn(Optional.of(livro));
        when(modelMapper.map(livro, LivroResponseDTO.class)).thenReturn(livroResponseDTO);

        LivroResponseDTO result = livroService.buscarPorId("1");

        assertNotNull(result);
        assertEquals(livroResponseDTO.getId(), result.getId());
        verify(repository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("Deve lançar NegocioException ao buscar livro por ID não encontrado")
    void buscarPorId_naoEncontrado_lancaExcecao() {
        when(repository.findById(anyString())).thenReturn(Optional.empty());

        NegocioException exception = assertThrows(NegocioException.class, () ->
                livroService.buscarPorId("1"));

        assertEquals("LIVRO_NAO_ENCONTRADO", exception.getCodigo());
        assertEquals("Livro com id '1' não encontrado.", exception.getMessage());
        verify(repository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("Deve listar livros com sucesso sem filtro de gênero")
    void listarLivros_semFiltro_sucesso() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Livro> livroPage = new PageImpl<>(Collections.singletonList(livro), pageable, 1);

        when(repository.findAll(pageable)).thenReturn(livroPage);
        when(modelMapper.map(any(Livro.class), eq(LivroResponseDTO.class))).thenReturn(livroResponseDTO);

        Page<LivroResponseDTO> result = livroService.listar(null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(livroResponseDTO.getId(), result.getContent().get(0).getId());
        verify(repository, times(1)).findAll(pageable);
        verify(repository, never()).findByGenero(any(GeneroEnum.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve listar livros com sucesso com filtro de gênero")
    void listarLivros_comFiltro_sucesso() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Livro> livroPage = new PageImpl<>(Collections.singletonList(livro), pageable, 1);

        when(repository.findByGenero(any(GeneroEnum.class), any(Pageable.class))).thenReturn(livroPage);
        when(modelMapper.map(any(Livro.class), eq(LivroResponseDTO.class))).thenReturn(livroResponseDTO);

        Page<LivroResponseDTO> result = livroService.listar(GeneroEnum.FICCAO_CIENTIFICA, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(livroResponseDTO.getId(), result.getContent().get(0).getId());
        verify(repository, times(1)).findByGenero(any(GeneroEnum.class), any(Pageable.class));
        verify(repository, never()).findAll(pageable);
    }

    @Test
    @DisplayName("Deve atualizar um livro com sucesso")
    void atualizarLivro_sucesso() {
        LivroRequestDTO atualizacaoDto = new LivroRequestDTO(
                "Titulo Atualizado",
                "Autor Atualizado",
                "0987654321",
                2024,
                GeneroEnum.ROMANCE,
                false
        );
        Livro livroAtualizado = new Livro(
                "1",
                "Titulo Atualizado",
                "Autor Atualizado",
                "0987654321",
                2024,
                GeneroEnum.ROMANCE,
                false,
                livro.getDataInclusao(),
                LocalDateTime.now()
        );
        LivroResponseDTO livroResponseAtualizado = new LivroResponseDTO(
                "1",
                "Titulo Atualizado",
                "Autor Atualizado",
                "0987654321",
                2024,
                GeneroEnum.ROMANCE,
                false,
                livro.getDataInclusao(),
                LocalDateTime.now()
        );

        when(repository.findById(anyString())).thenReturn(Optional.of(livro));
        when(repository.findByIsbn(anyString())).thenReturn(Optional.empty()); // No other book with this ISBN
        doNothing().when(modelMapper).map(any(LivroRequestDTO.class), any(Livro.class));
        when(repository.save(any(Livro.class))).thenReturn(livroAtualizado);
        when(modelMapper.map(any(Livro.class), eq(LivroResponseDTO.class))).thenReturn(livroResponseAtualizado);

        LivroResponseDTO result = livroService.atualizar("1", atualizacaoDto);

        assertNotNull(result);
        assertEquals(livroResponseAtualizado.getTitulo(), result.getTitulo());
        assertEquals(livroResponseAtualizado.getIsbn(), result.getIsbn());
        verify(repository, times(1)).findById(anyString());
        verify(repository, times(1)).findByIsbn(anyString());
        verify(repository, times(1)).save(any(Livro.class));
    }

    @Test
    @DisplayName("Deve lançar NegocioException ao tentar atualizar livro não encontrado")
    void atualizarLivro_naoEncontrado_lancaExcecao() {
        when(repository.findById(anyString())).thenReturn(Optional.empty());

        NegocioException exception = assertThrows(NegocioException.class, () ->
                livroService.atualizar("1", livroRequestDTO));

        assertEquals("LIVRO_NAO_ENCONTRADO", exception.getCodigo());
        assertEquals("Livro com id '1' não encontrado.", exception.getMessage());
        verify(repository, times(1)).findById(anyString());
        verify(repository, never()).findByIsbn(anyString());
        verify(repository, never()).save(any(Livro.class));
    }

    @Test
    @DisplayName("Deve lançar NegocioException ao tentar atualizar livro com ISBN duplicado por outro livro")
    void atualizarLivro_isbnDuplicadoPorOutroLivro_lancaExcecao() {
        Livro outroLivro = new Livro(
                "2",
                "Outro Titulo",
                "Outro Autor",
                "1234567890", // Same ISBN as livroRequestDTO
                2020,
                GeneroEnum.TERROR,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(repository.findById(anyString())).thenReturn(Optional.of(livro));
        when(repository.findByIsbn(anyString())).thenReturn(Optional.of(outroLivro));

        NegocioException exception = assertThrows(NegocioException.class, () ->
                livroService.atualizar("1", livroRequestDTO));

        assertEquals("ISBN_DUPLICADO", exception.getCodigo());
        assertEquals("Livro com ISBN 1234567890 já cadastrado.", exception.getMessage());
        verify(repository, times(1)).findById(anyString());
        verify(repository, times(1)).findByIsbn(anyString());
        verify(repository, never()).save(any(Livro.class));
    }

    @Test
    @DisplayName("Deve excluir um livro com sucesso")
    void excluirLivro_sucesso() {
        when(repository.existsById(anyString())).thenReturn(true);
        doNothing().when(repository).deleteById(anyString());

        assertDoesNotThrow(() -> livroService.excluir("1"));

        verify(repository, times(1)).existsById(anyString());
        verify(repository, times(1)).deleteById(anyString());
    }

    @Test
    @DisplayName("Deve lançar NegocioException ao tentar excluir livro não encontrado")
    void excluirLivro_naoEncontrado_lancaExcecao() {
        when(repository.existsById(anyString())).thenReturn(false);

        NegocioException exception = assertThrows(NegocioException.class, () ->
                livroService.excluir("1"));

        assertEquals("LIVRO_NAO_ENCONTRADO", exception.getCodigo());
        assertEquals("Livro com id '1' não encontrado.", exception.getMessage());
        verify(repository, times(1)).existsById(anyString());
        verify(repository, never()).deleteById(anyString());
    }
}
