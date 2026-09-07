# API Gerenciador de Biblioteca

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg?style=flat-square&logo=openjdk)](https://www.oracle.com/java/)
[![Spring Boot 3](https://img.shields.io/badge/Spring_Boot-3.x-brightgreen.svg?style=flat-square&logo=spring)](https://spring.io/projects/spring-boot)
[![Redis](https://img.shields.io/badge/Redis-Cache-red.svg?style=flat-square&logo=redis)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-Containers-blue.svg?style=flat-square&logo=docker)](https://www.docker.com/)

Uma API RESTful backend desenvolvida em **Java 21** e **Spring Boot 3** para gerenciamento de acervo bibliográfico. A aplicação conta com suporte a **cache via Redis**, suporte a **containers via Docker**, **testes de integração com Testcontainers** e padronização de respostas de erro e DTOs.

---

## Sumário

- [Características Principais](#características-principais)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Arquitetura do Código](#arquitetura-do-código)
- [Configuração e Execução](#configuração-e-execução)
- [Documentação da API](#documentação-da-api)
- [Tratamento de Exceções](#tratamento-de-exceções)

---

## Características Principais

- **CRUD de Acervo Bibliográfico:** Operações completas de cadastro, consulta, edição e exclusão de livros.
- **Cache de Alta Performance:** Cacheamento de consultas frequentes utilizando Redis para otimizar tempo de resposta e reduzir carga no banco de dados.
- **Mapeamento Flexível (DTOs):** Uso do `ModelMapper` para desacoplar entidades de banco das respostas da API (`ListarLivroResponseDTO`, `MetaDTO`).
- **Tratamento Global de Erros:** Estrutura unificada com `@ControllerAdvice` (`RestExceptionHandler`) e exceções de negócio customizadas (`NegocioException`).
- **Ambiente Isolado com Docker:** Containerização da aplicação e de suas dependências (Banco de dados e Redis).
- **Testes de Integração Confiáveis:** Execução de suíte de testes end-to-end com **Testcontainers**, simulando um ambiente de produção real.

---

## Tecnologias Utilizadas

| Tecnologia                  | Finalidade |
|-----------------------------|---|
| **Java 21**                 | Linguagem de programação principal |
| **Spring Boot 3.x**         | Framework para construção da aplicação |
| **Spring Data JPA**         | Persistência de dados e abstração de repositórios |
| **Redis**                   | Armazenamento em cache em memória |
| **ModelMapper**             | Conversão de entidades de domínio para DTOs |
| **Docker & Docker Compose** | Gerenciamento e orquestração de containers |
| **JUnit 5 & Mockito**       | Testes unitários e mocks |
| **Testcontainers**          | Testes de integração com containers reais |

---

## Arquitetura do Código

O projeto adota uma arquitetura em camadas bem definida, facilitando a manutenção e a escalabilidade:

```text
src/main/java/com/example/gerenciadorbiblioteca/
├── config/          # Bean Configurations (RedisConfig, ModelMapperConfig)
├── controller/      # Controladores REST e exposição de endpoints (LivroController)
├── dto/             # Data Transfer Objects (Requests, Responses, MetaDTO)
├── exception/       # Exceções customizadas e RestExceptionHandler
├── model/           # Entidades JPA e Enums de domínio (Livro, GeneroEnum)
├── repository/      # Interfaces de acesso a dados (LivroRepository)
└── service/         # Regras de negócio e integração com Cache (LivroService)
```

---

## Configuração e Execução

### 1. Pré-requisitos
- **Java JDK 21** ou superior instalado
- **Docker** e **Docker Compose** ativos na máquina
- **Maven** (opcional, o projeto possui `mvnw`)

### 2. Subindo a Infraestrutura com Docker

Acesse o diretório raiz do projeto e inicie os containers do banco de dados e do Redis:

```bash
docker-compose up -d
```

### 3. Executando a Aplicação

Rode a aplicação Spring Boot utilizando o wrapper do Maven:

**Linux / macOS:**
```bash
./mvnw spring-boot:run
```

**Windows (PowerShell/CMD):**
```cmd
mvnw.cmd spring-boot:run
```

Por padrão, a aplicação estará acessível em: `http://localhost:8080`

---

## Documentação da API

### Endpoints de Livros (`/api/livros`)

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/livros` | Lista todos os livros cadastrados (com paginação e cache) |
| `GET` | `/api/livros/{id}` | Busca um livro específico pelo seu ID |
| `POST` | `/api/livros` | Cadastra um novo livro no sistema |
| `PUT` | `/api/livros/{id}` | Atualiza as informações de um livro existente |
| `DELETE` | `/api/livros/{id}` | Remove um livro do sistema e invalida o cache |

---

### Exemplo de Requisição (POST `/api/livros`)

**Header:** `Content-Type: application/json`

```json
{
  "titulo": "Dom Casmurro",
  "autor": "Machado de Assis",
  "genero": "ROMANCE",
  "anoPublicacao": 1899
}
```

### Exemplo de Resposta (GET `/api/livros`)

```json
{
  "data": [
    {
      "id": 1,
      "titulo": "Dom Casmurro",
      "autor": "Machado de Assis",
      "genero": "ROMANCE",
      "anoPublicacao": 1899
    }
  ],
  "meta": {
    "paginaAtual": 0,
    "totalPaginas": 1,
    "totalElementos": 1,
    "tamanhoPagina": 10
  }
}
```

---

## Tratamento de Exceções

A API utiliza o interceptor global `RestExceptionHandler` para retornar respostas padronizadas em caso de falhas ou regras de negócio violadas:

```json
{
  "timestamp": "2026-07-27T14:10:00Z",
  "status": 400,
  "erro": "Regra de Negócio",
  "mensagem": "Já existe um livro cadastrado com o mesmo título e autor.",
  "path": "/api/livros"
}
```
