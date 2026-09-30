# 🏨 API de Gerenciamento de Reservas de Salas

Uma API RESTful desenvolvida para gerenciar reservas de espaços corporativos, focada em resolver conflitos de horários e garantir a integridade dos dados através de regras de negócio consistentes e bem encapsuladas.

## 🎯 Objetivos e Aprendizados
Este projeto foi construído para consolidar a aplicação de boas práticas de engenharia de software. Os principais focos técnicos incluem:
- Implementação da lógica matemática de **intervalo semiaberto** para evitar sobreposição de reservas na mesma sala.
- Aplicação de **Princípios SOLID** e **Domain-Driven Design (DDD)** focado na camada de domínio para blindar regras de negócio.
- Construção de uma suíte de testes de unidade robusta para a camada de serviços.
- Configuração de um ambiente de execução padronizado via contêineres Docker.

## 🛠️ Tecnologias Utilizadas
- **Linguagem:** Java 21
- **Framework:** Spring Boot 3
- **Persistência:** Spring Data JPA / Hibernate
- **Banco de Dados:** H2 Database (em memória) e PostgreSQL
- **Testes:** JUnit 5 e Mockito
- **DevOps:** Docker e Docker Compose

## 🏗️ Arquitetura e Boas Práticas
O projeto adota uma **Arquitetura em Camadas** (Controller, Service, Repository, Model/DTO) para garantir baixo acoplamento e alta coesão.
- **Domínio Rico:** Validações de negócio encapsuladas diretamente nas entidades (ex: transições de estados no cancelamento de reservas).
- **Tratamento Global de Exceções:** Uso de `@RestControllerAdvice` para centralizar e padronizar as respostas de erros HTTP da API.
- **Performance:** Estratégias de *JOIN FETCH* para evitar problemas de consultas N+1 durante paginações.
- **Versionamento:** Fluxo de trabalho baseado em *Feature Branching*, *Pull Requests* e *Conventional Commits*.

## 🚀 Como Executar Localmente

### Pré-requisitos
- Git
- Docker e Docker Compose instalados

### Passo a Passo
1. Clone este repositório:
   ```bash
   git clone [https://github.com/Vinizeira/nome-do-repositorio.git](https://github.com/Vinizeira/nome-do-repositorio.git)
Acesse o diretório do projeto:

Bash
cd nome-do-repositorio
Suba a aplicação e o banco de dados via container:

Bash
docker compose up -d --build
A API estará disponível em: http://localhost:8080/api/v1

Nota: Se estiver usando o perfil local H2, acesse o painel em http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:reservasdb, User: sa, sem senha).

👨‍💻 Contato

viniciushpereira08@outlook.com

LinkedIn: [Vinicius Pereira](https://www.linkedin.com/in/oviniciuspereira/)