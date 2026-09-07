package com.example.gerenciadorbiblioteca.controller;

import com.example.gerenciadorbiblioteca.dto.LivroRequestDTO;
import com.example.gerenciadorbiblioteca.model.GeneroEnum;
import com.example.gerenciadorbiblioteca.repository.LivroRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class LivroControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"));

    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:latest"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379).toString());
    }

    @BeforeEach
    void setUp() {
        livroRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        livroRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /livros - Deve criar um novo livro com sucesso")
    void criarLivro_sucesso() throws Exception {
        LivroRequestDTO requestDTO = new LivroRequestDTO(
                "O Senhor dos Anéis",
                "J.R.R. Tolkien",
                "978-85-333-0227-3",
                1954,
                GeneroEnum.FANTASIA,
                true
        );

        mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.titulo").value("O Senhor dos Anéis"))
                .andExpect(jsonPath("$.autor").value("J.R.R. Tolkien"))
                .andExpect(jsonPath("$.isbn").value("978-85-333-0227-3"));
    }

    @Test
    @DisplayName("POST /livros - Deve retornar 400 ao tentar criar livro com ISBN duplicado")
    void criarLivro_isbnDuplicado_retornaBadRequest() throws Exception {
        LivroRequestDTO requestDTO = new LivroRequestDTO(
                "O Senhor dos Anéis",
                "J.R.R. Tolkien",
                "978-85-333-0227-3",
                1954,
                GeneroEnum.FANTASIA,
                true
        );

        // First creation
        mockMvc.perform(post("/livros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)));

        // Second creation with same ISBN
        mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("ISBN_JA_EXISTE"));
    }

    @Test
    @DisplayName("GET /livros/{id} - Deve buscar um livro por ID com sucesso")
    void buscarLivroPorId_sucesso() throws Exception {
        LivroRequestDTO requestDTO = new LivroRequestDTO(
                "A Culpa é das Estrelas",
                "John Green",
                "978-85-8057-346-6",
                2012,
                GeneroEnum.ROMANCE,
                null
        );

        String responseString = mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(responseString).get("id").asText();

        mockMvc.perform(get("/livros/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.titulo").value("A Culpa é das Estrelas"));
    }

    @Test
    @DisplayName("GET /livros/{id} - Deve retornar 404 ao buscar livro por ID não encontrado")
    void buscarLivroPorId_naoEncontrado_retornaNotFound() throws Exception {
        mockMvc.perform(get("/livros/{id}", "nonExistentId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("LIVRO_NAO_ENCONTRADO"));
    }

    @Test
    @DisplayName("PUT /livros/{id} - Deve atualizar um livro com sucesso")
    void atualizarLivro_sucesso() throws Exception {
        LivroRequestDTO createDTO = new LivroRequestDTO(
                "Original Title",
                "Original Author",
                "111-11-111-1111-1",
                2000,
                GeneroEnum.HISTORIA,
                true
        );

        String responseString = mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(responseString).get("id").asText();

        LivroRequestDTO updateDTO = new LivroRequestDTO(
                "Updated Title",
                "Updated Author",
                "222-22-222-2222-2",
                2010,
                GeneroEnum.BIOGRAFIA,
                false
        );

        mockMvc.perform(put("/livros/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.titulo").value("Updated Title"))
                .andExpect(jsonPath("$.autor").value("Updated Author"))
                .andExpect(jsonPath("$.isbn").value("222-22-222-2222-2"));
    }

    @Test
    @DisplayName("DELETE /livros/{id} - Deve excluir um livro com sucesso")
    void excluirLivro_sucesso() throws Exception {
        LivroRequestDTO createDTO = new LivroRequestDTO(
                "Livro para Excluir",
                "Autor Exclusão",
                "333-33-333-3333-3",
                2005,
                GeneroEnum.TERROR,
                true
        );

        String responseString = mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(responseString).get("id").asText();

        mockMvc.perform(delete("/livros/{id}", id))
                .andExpect(status().isNoContent());

        // Verify it's actually deleted
        mockMvc.perform(get("/livros/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /livros/{id} - Deve retornar 404 ao tentar excluir livro não encontrado")
    void excluirLivro_naoEncontrado_retornaNotFound() throws Exception {
        mockMvc.perform(delete("/livros/{id}", "nonExistentId"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("LIVRO_NAO_ENCONTRADO"));
    }

    @Test
    @DisplayName("GET /livros - Deve listar livros com paginação")
    void listarLivros_comPaginacao() throws Exception {
        // Create multiple books
        for (int i = 0; i < 5; i++) {
            LivroRequestDTO requestDTO = new LivroRequestDTO(
                    "Livro " + i,
                    "Autor " + i,
                    "999-99-999-999" + i + "-9",
                    2020,
                    GeneroEnum.FICCAO_CIENTIFICA,
                    true
            );
            mockMvc.perform(post("/livros")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)));
        }

        mockMvc.perform(get("/livros?pagina=0&tamanho=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.meta.page").value(0))
                .andExpect(jsonPath("$.meta.limit").value(2))
                .andExpect(jsonPath("$.meta.totalPages").value(3)); // 5 books / 2 per page = 3 pages
    }

    @Test
    @DisplayName("GET /livros - Deve listar livros filtrados por gênero")
    void listarLivros_filtradoPorGenero() throws Exception {
        // Create books with different genres
        LivroRequestDTO ficcao = new LivroRequestDTO("Ficcao 1", "Autor F", "111-11-111-1111-1", 2000, GeneroEnum.FICCAO_CIENTIFICA, true);
        LivroRequestDTO romance = new LivroRequestDTO("Romance 1", "Autor R", "222-22-222-2222-2", 2001, GeneroEnum.ROMANCE, true);
        LivroRequestDTO ficcao2 = new LivroRequestDTO("Ficcao 2", "Autor F2", "333-33-333-3333-3", 2002, GeneroEnum.FICCAO_CIENTIFICA, true);

        mockMvc.perform(post("/livros").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(ficcao)));
        mockMvc.perform(post("/livros").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(romance)));
        mockMvc.perform(post("/livros").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(ficcao2)));

        mockMvc.perform(get("/livros?genero=FICCAO_CIENTIFICA")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].titulo").value("Ficcao 1"))
                .andExpect(jsonPath("$.items[1].titulo").value("Ficcao 2"));

        mockMvc.perform(get("/livros?genero=ROMANCE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].titulo").value("Romance 1"));
    }
}
