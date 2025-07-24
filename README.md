# ✈️ Orivan Travel API

API RESTful para **registro, consulta de reservas e pacotes de viagens**

---

## 🚀 Tecnologias Utilizadas

- ![Java](https://img.shields.io/badge/Java-21-blue.svg) **Java 21**
- ![Maven](https://img.shields.io/badge/Maven-3.8-blue) **Maven**
- ![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.3-brightgreen) **Spring Boot 3.5.3**
- **Spring Data JPA**
- **Lombok**
- **MapStruct**
- **SQL Server**
- **Azure**

---

## ⚙  ️Pré-Requisitos

- [JDK 21](https://adoptium.net/) instalado
- IDE Java (ex: *IntelliJ IDEA*, *Eclipse*, etc)
- SQL Server instalado/configurado

---

## 🗄 Configuração do Banco de Dados

1. Verifique as credenciais no canal do Teams **"Decola Tech 6 - Grupo 3"**
2. Configure seu cliente SQL:
  - **Server name**: `<informar conforme canal>`
  - **Autenticação**: SQL Server Authentication
  - **Login e senha**: `<informar conforme canal>`
3. Solicite ao admin para liberar o seu IP

---

## 📦 Clonando o Repositório

```bash

git clone https://github.com/Orvian-Travel/RESTAPI_Orvian

cd RESTAPI_Orvian

```

---

## 📄 Documentação

- Você pode conferir a documentação do projeto na url, ao iniciar a aplicação:

```url
http://localhost:8080/swagger-ui/index.html
```

---

## 🏗️ Padrão Arquitetural

Utilizamos **Arquitetura em Camadas**:

| Camada       | Função                                                                      |
|--------------|-----------------------------------------------------------------------------|
| Controller   | Recebe requisições, valida e retorna respostas (DTOs)                       |
| Service      | Contém as regras de negócio                                                 |
| Repository   | Interage com o banco de dados (Spring Data JPA)                             |
| Domain       | Entidades e objetos de valor                                                |
| Validator    | Regras de validação                                                         |
| Mapper       | Mapeamento entidade ↔ DTO (MapStruct)                                       |
| Config       | Configurações (segurança, Swagger etc)                                      |
| Annotations  | Anotações customizadas                                                      |

---


## 👨‍💻 Desenvolvimento

### ➕ Criando Enums

1. Adicione em `domain/enums/`
2. Se necessário, crie validadores em `validator/`


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

---

### ⚠️ Tratamento de Exceções

- Exceções de negócio: `service/exception/`
- Tratamento global: `controller/exception/GlobalExceptionHandler.java`

---

## 🔗 Endpoints Usuários

| Método | Endpoint               | Descrição           | Corpo Requisição   | Respostas                                 |
|--------|------------------------|---------------------|--------------------|-------------------------------------------|
| POST   | `/api/v1/users`        | Criar usuário       | CreateUserDTO      | 201, 400, 409, 500                       |
| PUT    | `/api/v1/users/{id}`   | Atualizar usuário   | UpdateUserDTO      | 204, 400, 404, 409, 500                  |
| GET    | `/api/v1/users`        | Listar usuários     | -                  | 200, 500                                 |
| GET    | `/api/v1/users/{id}`   | Buscar por id       | -                  | 200, 404, 500                            |
| DELETE | `/api/v1/users/{id}`   | Remover usuário     | -                  | 204, 404, 500                            |

---

## 📝 Estruturas dos DTOs

#### **CreateUserDTO**
| Campo      | Tipo        | Regras                                                                     |
|------------|-------------|----------------------------------------------------------------------------|
| name       | String      | Obrigatório, até 150 caracteres                                             |
| email      | String      | Obrigatório, até 150 caracteres, formato válido                             |
| password   | String      | Obrigatório, 8-20 caracteres com requisitos de segurança                    |
| phone      | String      | Obrigatório, até 15 caracteres, formato válido                              |
| document   | String      | Obrigatório, 8-14 caracteres, aceita CPF/RG/Passaporte                      |
| birthDate  | LocalDate   | Obrigatório, formato ISO (ex: 1990-01-01), maior de idade                   |

#### **UpdateUserDTO**
Todos opcionais, mesmas regras do CreateUserDTO. Inclui campo adicional:

| Campo | Tipo          | Observação                                 |
|-------|---------------|--------------------------------------------|
| role  | String        | A função deve ser USER, ATENDENTE ou ADMIN |

#### **UserSearchResultDTO**
| Campo      | Tipo          | Observação         |
|------------|---------------|--------------------|
| id         | UUID          | Identificador      |
| name       | String        | Nome completo      |
| email      | String        | E-mail             |
| phone      | String        | Telefone           |
| document   | String        | Documento          |
| birthDate  | LocalDate     | Data de nascimento |

---
