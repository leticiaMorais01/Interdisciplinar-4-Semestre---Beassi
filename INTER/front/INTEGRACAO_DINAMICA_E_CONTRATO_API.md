# BEASSI — preparação para integração dinâmica com Spring Boot

## O que foi preparado nesta versão
- Criada `Js/api.js` como camada única de chamadas HTTP. Ajuste `BEASSI_API_BASE_URL` para o endereço do Spring Boot.
- Criada `Js/integracao_dinamica.js` para carregar clientes ativos e categorias pela API, sem IDs ou nomes de clientes fixos no HTML.
- Cadastro e edição de projetos enviam `clienteId` numérico ou `null`, tipo, categorias e etapa. A imagem é enviada como multipart (`dados` + `imagem`).
- Lista administrativa de clientes e contadores passam a ser preenchidos pela API, sem linhas fictícias.
- Formulários de login, cadastro, perfil, feedback e recuperação de senha foram conectados aos endpoints definidos abaixo.
- Edição de projeto, cliente e perfis consulta os dados pelo ID ou pela sessão autenticada.

## Contrato REST esperado (precisa ser implementado/alinhado no Spring Boot)
Todos os endpoints usam prefixo `/api` e JSON, exceto upload de projeto. Os nomes abaixo são uma proposta consistente para o Front-End; não afirmam que já existam no Back-End.

| Método | Endpoint | Finalidade |
|---|---|---|
| POST | `/auth/login` | Autenticar e criar sessão segura (cookie HttpOnly) |
| POST | `/auth/logout` | Encerrar sessão |
| POST | `/auth/recuperar-senha` | Solicitar token/link por e-mail |
| POST | `/auth/redefinir-senha` | Validar token e atualizar hash BCrypt |
| POST | `/clientes` | Cadastrar cliente e usuário |
| GET | `/clientes?status=ATIVO` | Listar clientes ativos para associação a projetos |
| GET | `/clientes` | Listar clientes para administração |
| GET/PUT | `/clientes/me` | Consultar/editar cliente autenticado |
| GET/PUT | `/clientes/{id}` | Consultar/editar cliente pelo ID (admin) |
| GET | `/clientes/{id}/projetos` | Consultar projetos vinculados ao cliente (admin) |
| PATCH | `/clientes/{id}/status` | Ativar/desativar cadastro do cliente (admin) |
| GET | `/clientes/me/projetos` | Listar projetos do cliente autenticado |
| GET | `/administradores/me` | Consultar perfil do administrador autenticado |
| PUT | `/administradores/me` | Atualizar e-mail/registro CAU |
| GET | `/projetos` | Listar projetos para administração |
| GET | `/projetos/publicados` | Listar projetos publicados no portfólio |
| GET | `/projetos/{id}/publico` | Consultar detalhes públicos de um projeto publicado |
| POST | `/projetos` | Criar projeto |
| GET/PUT | `/projetos/{id}` | Consultar/editar projeto |
| GET | `/categorias` | Listar categorias |
| GET | `/tipos-projeto/residenciais` | Listar classificações da tabela `TIPO_RESIDENCIAL` |
| GET | `/tipos-projeto/comerciais` | Listar classificações da tabela `TIPO_COMERCIAL` |
| GET/POST | `/feedbacks` | Listar/enviar feedbacks |
| GET | `/feedbacks/aprovados` | Listar somente avaliações aprovadas para o site público |
| PATCH | `/feedbacks/{id}/status` | Aprovar/rejeitar feedback |

## Regras de negócio que o Back-End deve garantir
1. `clienteId: null` deve persistir `PROJETO.CLIENTE_ID = NULL`; valor preenchido deve referenciar `CLIENTE.ID_USUARIO` válido e ativo.
2. O usuário autenticado determina `ADMINISTRADOR_ID` no cadastro do projeto. Nunca confiar em ID de administrador enviado pelo navegador.
3. Apenas administrador autenticado pode criar/editar projetos, gerenciar clientes e moderar feedbacks.
4. Cliente só consulta seus próprios projetos e só envia feedback para projeto vinculado à sua conta.
5. Projeto público deve respeitar `STATUS_PUBLICACAO = 1`; áreas privadas precisam de autorização no servidor.
6. Categorias, tipos, etapas e percentuais precisam respeitar as tabelas/constraints do SQL Server. O Back-End deve devolver os IDs e rótulos reais.
7. Senhas devem ser processadas com BCrypt no Spring Boot. Tokens de recuperação devem ser aleatórios, de uso único, expirar e ser armazenados como hash sempre que possível.
8. Upload de imagens deve validar tipo/tamanho, armazenar o arquivo em local apropriado e gravar caminho/alt text na tabela `IMAGEM`.
9. Para dados pessoais e sessão, preferir cookie HttpOnly/Secure/SameSite e proteção CSRF conforme a configuração do Spring Security.

## Atenção: o que ainda depende do Back-End
Este pacote contém somente o Front-End e a definição do contrato esperado. A comunicação só funcionará quando os endpoints acima forem implementados no Spring Boot, com DTOs compatíveis, autenticação/autorização, procedures/repositórios e conexão ao SQL Server. A URL padrão está em `Js/api.js` (`http://localhost:8080/api`).

## Dados e telas ainda a validar na integração
As páginas de painel administrativo, lista de clientes, lista de projetos, portfólio público, detalhe de projeto, área de projetos do cliente, detalhe de cliente e moderação de feedback já possuem pontos de carregamento pela API. O dashboard e alguns elementos secundários ainda podem precisar de ajustes conforme os DTOs reais do Spring Boot. O contrato dos endpoints deve ser alinhado com o Back-End antes do teste ponta a ponta.


## Relatórios gerenciais — preparação do Front-End
Foi adicionada a página `relatorios.html`, acessível pelo painel administrativo. Ela apresenta três relatórios:
- Projetos: informações consolidadas dos projetos cadastrados.
- Clientes: informações dos clientes e seus vínculos com projetos.
- Feedbacks: informações consolidadas das avaliações recebidas.

A página possui filtros opcionais por data inicial e final. Os endpoints abaixo são apenas uma proposta provisória para o Spring Boot e deverão ser alinhados ao banco e às consultas reais:
- `GET /relatorios/projetos`
- `GET /relatorios/clientes`
- `GET /relatorios/feedbacks`

Quando os endpoints retornarem uma lista JSON (ou `content`, `items`, `data` ou `resultados` contendo uma lista), a tela monta a tabela automaticamente com as colunas recebidas. Os parâmetros opcionais de período são `dataInicial` e `dataFinal`.

**Importante sobre os objetos SQL:** o Front-End não chama Views, Procedures ou Functions diretamente. O Java deverá executar as Procedures e Functions quando necessário e consultar as Views (ou consultas baseadas nelas), disponibilizando o resultado por meio dos endpoints REST. Os nomes, parâmetros e retornos definitivos só devem ser definidos após conferir o script atual do banco.
