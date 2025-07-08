# Orivan Travel API

API RestFul para registro, consulta de reservas e pacotes de viagens.

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