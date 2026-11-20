# BEASSI — Plataforma de Gestão de Projetos de Arquitetura

O **BEASSI** é uma aplicação web desenvolvida para aproximar clientes e profissionais de arquitetura, reunindo a apresentação de projetos e a organização dos atendimentos em um só lugar. O sistema combina um site de navegação pública com áreas específicas para clientes e administradores, permitindo acompanhar projetos, registrar avaliações e gerenciar informações do escritório.

Este repositório reúne o **front-end**, a **API em Java com Spring Boot** e os **scripts do banco de dados SQL Server**. É um projeto acadêmico de desenvolvimento full-stack, com foco na integração entre interface, regras de negócio e persistência de dados.

## Sobre o projeto

A proposta do BEASSI é ir além de um portfólio de arquitetura. Enquanto visitantes podem conhecer os projetos, clientes contam com funcionalidades voltadas ao acompanhamento de seus trabalhos. Já a área administrativa concentra cadastros, organização dos projetos, feedbacks e relatórios.

O projeto foi estruturado para exercitar o desenvolvimento de uma aplicação completa, desde a modelagem do banco até a comunicação entre as páginas web e os endpoints REST.

## Funcionalidades

### Visitantes e clientes

- Visualização de projetos e informações apresentadas no site.
- Cadastro e acesso à conta de usuário.
- Consulta de projetos vinculados ao cliente.
- Visualização de detalhes e acompanhamento do progresso dos projetos.
- Envio de avaliações e feedbacks.
- Edição de informações do perfil.
- Solicitação de recuperação de senha por e-mail.

### Administração

- Painel administrativo para acesso às funções de gestão.
- Cadastro, consulta e atualização de projetos.
- Gerenciamento de clientes e suas informações.
- Organização de categorias e tipos de projetos, incluindo residenciais e comerciais.
- Consulta e gerenciamento de feedbacks.
- Relatórios relacionados a projetos, clientes e avaliações.
- Gerenciamento de imagens associadas aos projetos.

> As funcionalidades acima refletem os módulos e telas encontrados no código. A execução completa dos fluxos integrados depende da configuração do ambiente e do banco de dados.

## Tecnologias utilizadas

| Camada | Tecnologias |
| --- | --- |
| Back-end | Java 21, Spring Boot 4.1.1 |
| API | Spring Web MVC, APIs REST |
| Persistência | Spring Data JPA, Hibernate |
| Banco de dados | Microsoft SQL Server |
| Front-end | HTML5, CSS3 e JavaScript |
| Segurança e validação | Spring Security Crypto, Bean Validation |
| E-mail | Spring Boot Mail / SMTP |
| Build | Maven e Maven Wrapper |

## Organização do código

```text
INTER/
└── INTER/
    ├── Banco/
    │   ├── BEASSI_ARQUITETURA.sql
    │   ├── BEASSI_OBJETOS_SQL_VIEWS_FUNCTIONS_PROCEDURES.sql
    │   └── INSERTS_BEASSI_DEMONSTRACAO.sql
    │
    ├── beassi/
    │   ├── pom.xml
    │   └── src/
    │       ├── main/java/com/fatec/beassi/
    │       │   ├── config/
    │       │   ├── controller/
    │       │   ├── dto/
    │       │   ├── entities/
    │       │   ├── exception/
    │       │   ├── repositories/
    │       │   └── services/
    │       ├── main/resources/
    │       └── test/
    │
    └── front/
        ├── Css/
        ├── Js/
        ├── index.html
        ├── login.html
        ├── meus_projetos.html
        ├── painel_admin.html
        └── ... outras páginas HTML
```

No back-end, os **controllers** recebem as requisições, os **services** concentram as operações do sistema, os **repositories** fazem a comunicação com o banco e as **entities** representam os dados persistidos. Os **DTOs** auxiliam na transferência de informações entre a API e a interface.

No front-end, as páginas HTML são acompanhadas por arquivos CSS e JavaScript, incluindo módulos de integração com a API, formulários, gerenciamento de projetos e relatórios.

## Como executar o projeto

### Pré-requisitos

- **JDK 21** instalado.
- **Microsoft SQL Server** disponível, com acesso à instância configurada.
- **Maven** ou o Maven Wrapper incluído no projeto.
- Um editor de código, como VS Code ou IntelliJ IDEA.
- Para abrir o front-end localmente, um servidor HTTP estático, como a extensão Live Server do VS Code.

### 1. Obtenha o código

```bash
git clone URL_DO_SEU_REPOSITORIO
cd INTER/INTER/beassi
```

Substitua `URL_DO_SEU_REPOSITORIO` pelo endereço do repositório. Se reorganizar as pastas antes de publicar, ajuste o caminho acima.

### 2. Prepare o banco de dados

Os arquivos SQL estão em `INTER/INTER/Banco/` no pacote original:

- `BEASSI_ARQUITETURA.sql`: estrutura principal do banco.
- `BEASSI_OBJETOS_SQL_VIEWS_FUNCTIONS_PROCEDURES.sql`: objetos SQL complementares.
- `INSERTS_BEASSI_DEMONSTRACAO.sql`: registros de demonstração.

Abra os scripts no SQL Server Management Studio, confira o banco de destino e execute-os na ordem apropriada para sua instância. **Revise os INSERTs antes de executá-los em um banco que já contenha dados.**

### 3. Configure a aplicação

No arquivo `beassi/src/main/resources/application.properties`, configure a conexão com o SQL Server e as informações de e-mail necessárias para a recuperação de senha.

O projeto utiliza o banco `bdarquitetura` na configuração encontrada no ZIP e define `spring.jpa.hibernate.ddl-auto=validate`, ou seja, o Hibernate espera que o esquema já exista.

**Não publique usuários, senhas ou credenciais SMTP no GitHub.** Antes de compartilhar o código, substitua os valores sensíveis por variáveis de ambiente ou uma configuração local ignorada pelo Git.

### 4. Inicie o back-end

No diretório `beassi/`, execute:

**Windows:**

```powershell
.\mvnw.cmd spring-boot:run
```

**macOS / Linux:**

```bash
./mvnw spring-boot:run
```

Sem alteração da porta padrão do Spring Boot, a API estará em `http://localhost:8080`.

### 5. Abra o front-end

Abra a pasta `front/` no VS Code e inicie o `index.html` com um servidor local. A configuração do back-end inclui a URL padrão `http://localhost:5500` para o front-end; confira se a porta e os endereços usados em `front/Js/api.js` correspondem ao seu ambiente.

## Banco de dados e integração

O projeto possui entidades relacionadas a **usuários, administradores, clientes, projetos, categorias, tipos residenciais e comerciais, imagens e feedbacks**. Também há uma entidade de associação entre projetos e categorias.

Além das tabelas principais, o repositório inclui um script dedicado a **views, functions e procedures** do SQL Server. Os módulos de relatórios do back-end permitem explorar informações relacionadas a clientes, projetos e feedbacks.

A integração entre as páginas e o servidor acontece por requisições JavaScript à API Spring Boot.

## Testes e desenvolvimento

O projeto contém uma classe de teste inicial (`BeassiApplicationTests.java`). Para executar os testes disponíveis:

```bash
./mvnw test
```

No Windows, utilize `mvnw.cmd test`. A presença dessa classe não comprova, por si só, cobertura de todos os fluxos: é importante validar separadamente os endpoints, os cadastros, as consultas, as atualizações e a integração com o front-end.

## Próximos passos

- Concluir e validar os fluxos de cadastro e edição em toda a aplicação.
- Ampliar os testes da API e a verificação da integração com o SQL Server.
- Revisar as configurações de segurança e as permissões de acesso.
- Adicionar capturas de tela das principais páginas.
- Preparar uma demonstração navegável do sistema.

## Visualização do sistema

As telas principais do projeto incluem a **página inicial**, o **catálogo de projetos**, a **área do cliente** e o **painel administrativo**. Capturas reais dessas páginas podem ser adicionadas aqui para apresentar o BEASSI antes mesmo de executar a aplicação.

## Contexto acadêmico

O BEASSI foi desenvolvido como projeto acadêmico, aplicando conhecimentos de programação orientada a objetos, desenvolvimento web, modelagem de dados e integração entre sistemas. A implementação permite explorar tanto a experiência de quem utiliza a plataforma quanto a organização técnica necessária para mantê-la.

---

**BEASSI — Arquitetura** · Um projeto para conectar ideias, pessoas e projetos em um ambiente digital.
