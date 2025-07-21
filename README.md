# Orivan Travel API

API RESTful para registro, consulta de reservas e pacotes de viagens.

## Tecnologias

- **Java 21**
- **Maven**
- **Spring Boot 3.5.3**
- **Spring Data JPA**
- **Lombok**
- **Mapstruct**
- **SQL Server**
- **Azure**

## Pré-Requisitos

- **JDK 21** instalado
- **IDE (IntelliJ IDEA, Eclipse, etc.)** configurada
- **SQL Server** instalado

## Configuração do Banco de Dados

- Conectar ao banco usando as credenciais no canal do teams "Decola Tech 6 - Grupo 3"
- Inserir o server name
- Alterar a autenticação para SQL Server Authentication
- Inserir o login e senha fornecidos
- Enviar o IP da máquina para o administrador

## Clonando o Repositório

```bash

git clone https://github.com/Orvian-Travel/RESTAPI_Orvian

cd RESTAPI_Orvian

```

## Padrões de Desenvolvimento
O padrão de arquitetura utilizado é a Arquitetura em Camadas, com as seguintes camadas:
- **Controller**: Responsável por receber as requisições HTTP, validar dados de entrada e retornar respostas padronizadas através de DTOs.
- **Service**: Contém a lógica de negócio da aplicação.
- **Repository**: Interage com o banco de dados, utilizando Spring Data JPA.
- **Domain**: Contém as entidades e objetos de valor.
- **Validator**: Contém as regras de validação de dados.
- **Mapper**: Utiliza MapStruct para mapear entidades e DTOs.
- **Config**: Contém as configurações do projeto, como segurança, Swagger, etc.
- **Annotations**: Contém anotações personalizadas utilizadas no projeto.

## Desenvolvimento

### Criação de Enums
1. Adicione em *domain/enums/*
2. Implemente validadores se necessário em *validator/*

~~~Java

public enum Status {
    ATIVO("Ativo"),
    INATIVO("Inativo");
    
    private final String descricao;
    
    Status(String descricao) {
        this.descricao = descricao;
    }
}

~~~

### Tratamento de Exceções
- Exceções de negócio: *service/exception/*
- Tratamento global: *controller/exception/GlobalExceptionHandler.java*

---

### Endpoints do Serviço de Usuário

- **POST `/api/v1/users`**  
  Cria um novo usuário.  
  **Request body:** `CreateUserDTO`  
  **Respostas:**
    - 201 Created: Usuário criado com sucesso (Location no header)
    - 400 Bad Request: Dados inválidos
    - 409 Conflict: Usuário já existe
    - 500 Internal Server Error

- **PUT `/api/v1/users/{id}`**  
  Atualiza um usuário existente pelo ID.  
  **Request body:** `UpdateUserDTO`  
  **Respostas:**
    - 204 No Content: Atualizado com sucesso
    - 400 Bad Request: Dados inválidos
    - 404 Not Found: Usuário não encontrado
    - 409 Conflict: Usuário já existe
    - 500 Internal Server Error

- **GET `/api/v1/users`**  
  Lista todos os usuários cadastrados.  
  **Respostas:**
    - 200 OK: Lista de usuários (`UserSearchResultDTO[]`)
    - 500 Internal Server Error

- **GET `/api/v1/users/{id}`**  
  Busca um usuário pelo ID.  
  **Respostas:**
    - 200 OK: Usuário encontrado (`UserSearchResultDTO`)
    - 404 Not Found: Usuário não encontrado
    - 500 Internal Server Error

- **DELETE `/api/v1/users/{id}`**  
  Remove um usuário pelo ID.  
  **Respostas:**
    - 204 No Content: Usuário removido
    - 404 Not Found: Usuário não encontrado
    - 500 Internal Server Error
### Estrutura dos DTOs do Serviço de Usuário

##### CreateUserDTO
- `name` (String, obrigatório): Nome completo do usuário. Máx. 150 caracteres.
- `email` (String, obrigatório): E-mail do usuário. Máx. 150 caracteres, formato válido.
- `password` (String, obrigatório): Senha (8-20 caracteres, requisitos de segurança).
- `phone` (String, obrigatório): Telefone do usuário. Máx. 15 caracteres, formato válido.
- `document` (String, obrigatório): Documento de identificação (8-14 caracteres, formatos aceitos: CPF, RG, passaporte).
- `birthDate` (LocalDate, obrigatório): Data de nascimento (ISO, ex: 1990-01-01, deve ser maior de idade).

##### UpdateUserDTO
- Todos os campos são opcionais, seguem as mesmas regras de validação do `CreateUserDTO`, com a adição do campo `role`.

##### UserSearchResultDTO
- `id` (UUID): Identificador único do usuário.
- `name` (String): Nome completo.
- `email` (String): E-mail.
- `phone` (String): Telefone.
- `document` (String): Documento.
- `birthDate` (LocalDate): Data de nascimento.

---