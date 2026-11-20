USE bdarquitetura;
GO



    /* 1. TIPOS DE PROJETOS */
    INSERT INTO TIPO_COMERCIAL (TIPO) VALUES
    ('Clinica'),
    ('Escritorio'),
    ('Cafeteria'),
    ('Restaurante'),
    ('Loja'),
    ('Salao de beleza'),
    ('Consultorio'),
    ('Academia'),
    ('Hotel'),
    ('Espaco comercial');

    INSERT INTO TIPO_RESIDENCIAL (TIPO) VALUES
    ('Casa'),
    ('Apartamento'),
    ('Cobertura'),
    ('Casa de campo'),
    ('Sobrado'),
    ('Chacara'),
    ('Casa geminada'),
    ('Kitnet'),
    ('Condominio residencial'),
    ('Studio');

    /* 2. USUARIOS
       admin@beassi.com e o acesso do administrador.
       Todos usam a mesma senha: Beassi@123.
    */
    DECLARE @HashSenha VARCHAR(255) = '$2a$10$.k/duBK1RoErEROnkyRi/OzvzYFbz5XxlEL9flL5ThEYPnPnT4hDu';

    INSERT INTO USUARIO (EMAIL, SENHA) VALUES
    ('admin@beassi.com', @HashSenha),
    ('ana@email.com', @HashSenha),
    ('bruno@email.com', @HashSenha),
    ('carla@email.com', @HashSenha),
    ('diego@email.com', @HashSenha),
    ('eduarda@email.com', @HashSenha),
    ('felipe@email.com', @HashSenha),
    ('gabriela@email.com', @HashSenha),
    ('henrique@email.com', @HashSenha),
    ('isabela@email.com', @HashSenha),
    ('joao@email.com', @HashSenha),
    ('mariana@email.com', @HashSenha),
    ('rafael@email.com', @HashSenha);

/* 3. ADMINISTRADOR */
INSERT INTO ADMINISTRADOR (ID_USUARIO, REGISTRO_CAU) VALUES
(1, 'CAU-SP-000001');
GO

/* 4. CLIENTES */
INSERT INTO CLIENTE (ID_USUARIO, NOME, CPF, TELEFONE, STATUS) VALUES
(2, 'Ana Souza', '000.000.000-01', '(17) 99111-1001', 1),
(3, 'Bruno Lima', '000.000.000-02', '(17) 99111-1002', 1),
(4, 'Carla Mendes', '000.000.000-03', '(17) 99111-1003', 1),
(5, 'Diego Santos', '000.000.000-04', '(17) 99111-1004', 1),
(6, 'Eduarda Oliveira', '000.000.000-05', '(17) 99111-1005', 1),
(7, 'Felipe Costa', '000.000.000-06', '(17) 99111-1006', 1),
(8, 'Gabriela Martins', '000.000.000-07', '(17) 99111-1007', 1),
(9, 'Henrique Alves', '000.000.000-08', '(17) 99111-1008', 1),
(10, 'Isabela Rocha', '000.000.000-09', '(17) 99111-1009', 1),
(11, 'João Ferreira', '000.000.000-10', '(17) 99111-1010', 1),
(12, 'Mariana Ribeiro', '000.000.000-11', '(17) 99111-1011', 1),
(13, 'Rafael Almeida', '000.000.000-12', '(17) 99111-1012', 1);
GO

/* 5. PROJETOS
   Os quatro primeiros são os projetos que receberão as imagens reais depois.
   Os outros oito são projetos fictícios para demonstração.
   TIPO_COMERCIAL_ID e TIPO_RESIDENCIAL_ID usam os IDs dos tipos
   que você já cadastrou nos seus três INSERTs.
*/
INSERT INTO PROJETO
(TITULO, DESCRICAO, DATA, STATUS_PUBLICACAO, ATIVO, CLIENTE_ID, ADMINISTRADOR_ID, TIPO_COMERCIAL_ID, TIPO_RESIDENCIAL_ID, ETAPA_ATUAL, PORCENTAGEM_CONCLUSAO)
VALUES
('Clínica Omne', 'Projeto comercial de uma clínica contemporânea, com recepção acolhedora, consultórios funcionais, circulação acessível e ambientes planejados para transmitir tranquilidade e profissionalismo.', '20260112', 1, 1, 2, 1, 1, NULL, 'Concluído', 100),
('Hype', 'Projeto residencial contemporâneo que combina personalidade, conforto e praticidade. A proposta valoriza a integração dos ambientes sociais, a iluminação natural e materiais de acabamento marcantes.', '20260203', 1, 1, 3, 1, NULL, 2, 'Projeto Executivo', 83),
('Apartamento Carol', 'Projeto de interiores para apartamento, com paleta de tons neutros, marcenaria sob medida e soluções inteligentes de armazenamento. Os ambientes foram pensados para oferecer conforto e aproveitar cada metro quadrado.', '20260218', 1, 1, 4, 1, NULL, 2, 'Concluído', 100),
('Tour de Coffee', 'Projeto comercial de cafeteria com atmosfera convidativa, identidade visual integrada ao ambiente, fluxo eficiente de atendimento e espaços confortáveis para permanência dos clientes.', '20260305', 1, 1, 5, 1, 3, NULL, 'Revisões', 67),
('Casa Horizonte', 'Residência contemporânea com ambientes integrados, grandes aberturas para entrada de luz natural, ventilação cruzada e conexão visual entre sala, cozinha e jardim.', '20260314', 1, 1, 6, 1, NULL, 1, 'Concluído', 100),
('Escritório Nexo', 'Projeto de escritório corporativo com salas de reunião, estações colaborativas e áreas de apoio. A organização espacial busca favorecer produtividade, conforto acústico e identidade profissional.', '20260402', 1, 1, 7, 1, 2, NULL, 'Projeto Executivo', 83),
('Casa Ipê', 'Projeto residencial com materiais naturais, madeira, pedra e paisagismo. A proposta cria uma relação harmoniosa entre os espaços internos e a área externa.', '20260419', 1, 1, 8, 1, NULL, 1, 'Apresentação ao cliente', 50),
('Bistrô Aurora', 'Projeto de restaurante com layout funcional, iluminação aconchegante e composição de materiais que reforçam a experiência gastronômica. Inclui salão, caixa e apoio de serviço.', '20260506', 1, 1, 9, 1, 4, NULL, 'Desenvolvimento do projeto', 33),
('Cobertura Mirante', 'Projeto de interiores para cobertura residencial, com varanda gourmet, integração da área social e especificação de acabamentos resistentes e elegantes.', '20260521', 0, 1, 10, 1, NULL, 3, 'Revisões', 67),
('Loja Forma', 'Projeto comercial de loja com exposição organizada de produtos, percurso intuitivo, provadores funcionais e linguagem visual alinhada ao posicionamento da marca.', '20260604', 1, 1, 11, 1, 5, NULL, 'Medição', 17),
('Refúgio das Oliveiras', 'Projeto de casa de campo que prioriza o contato com a natureza, ambientes amplos, varanda sombreada e materiais de baixa manutenção.', '20260617', 1, 1, 12, 1, NULL, 4, 'Não iniciado', 0),
('Studio Alameda', 'Projeto residencial compacto com mobiliário multifuncional, integração entre cozinha e estar e soluções para ampliar a percepção de espaço.', '20260701', 1, 1, 13, 1, NULL, 2, 'Desenvolvimento do projeto', 33);
GO

/* 6. IMAGENS FICTÍCIAS DOS 12 PROJETOS
   Todos os projetos possuem pelo menos uma imagem cadastrada.
   Os quatro primeiros mantêm a quantidade de imagens prevista para os projetos principais.
   Os demais recebem uma imagem fictícia cada, que poderá ser substituída depois.
*/

INSERT INTO IMAGEM (CAMINHO, PROJETO_ID) VALUES
-- Projeto 1: Clínica Omne
('/imagens/projeto-omne/placeholder-01.jpg', 1),
('/imagens/projeto-omne/placeholder-02.jpg', 1),
('/imagens/projeto-omne/placeholder-03.jpg', 1),
('/imagens/projeto-omne/placeholder-04.jpg', 1),
('/imagens/projeto-omne/placeholder-05.jpg', 1),
('/imagens/projeto-omne/placeholder-06.jpg', 1),
('/imagens/projeto-omne/placeholder-07.jpg', 1),
('/imagens/projeto-omne/placeholder-08.jpg', 1),
('/imagens/projeto-omne/placeholder-09.jpg', 1),
('/imagens/projeto-omne/placeholder-10.jpg', 1),

-- Projeto 2: Hype
('/imagens/projeto-hype/placeholder-01.jpg', 2),
('/imagens/projeto-hype/placeholder-02.jpg', 2),
('/imagens/projeto-hype/placeholder-03.jpg', 2),
('/imagens/projeto-hype/placeholder-04.jpg', 2),
('/imagens/projeto-hype/placeholder-05.jpg', 2),
('/imagens/projeto-hype/placeholder-06.jpg', 2),

-- Projeto 3: Apartamento Carol
('/imagens/projeto-carol/placeholder-01.jpg', 3),
('/imagens/projeto-carol/placeholder-02.jpg', 3),
('/imagens/projeto-carol/placeholder-03.jpg', 3),
('/imagens/projeto-carol/placeholder-04.jpg', 3),
('/imagens/projeto-carol/placeholder-05.jpg', 3),
('/imagens/projeto-carol/placeholder-06.jpg', 3),
('/imagens/projeto-carol/placeholder-07.jpg', 3),

-- Projeto 4: Tour de Coffee
('/imagens/projeto-cafeteria/placeholder-01.jpg', 4),
('/imagens/projeto-cafeteria/placeholder-02.jpg', 4),
('/imagens/projeto-cafeteria/placeholder-03.jpg', 4),
('/imagens/projeto-cafeteria/placeholder-04.jpg', 4),

-- Projeto 5
('/imagens/projeto-escritorio-nexo/placeholder-01.jpg', 5),

-- Projeto 6
('/imagens/projeto-bistro-aurora/placeholder-01.jpg', 6),

-- Projeto 7
('/imagens/projeto-loja-forma/placeholder-01.jpg', 7),

-- Projeto 8
('/imagens/projeto-casa-horizonte/placeholder-01.jpg', 8),

-- Projeto 9
('/imagens/projeto-casa-ipe/placeholder-01.jpg', 9),

-- Projeto 10
('/imagens/projeto-espaco-vitta/placeholder-01.jpg', 10),

-- Projeto 11
('/imagens/projeto-cobertura-mirante/placeholder-01.jpg', 11),

-- Projeto 12
('/imagens/projeto-studio-alameda/placeholder-01.jpg', 12);
GO


/* 7. CATEGORIAS */
INSERT INTO CATEGORIA (CATEGORIA, ATIVO) VALUES
('Projeto arquitetônico', 1),
('Projeto de interiores', 1),
('Fachada', 1),
('Paisagismo', 1),
('Iluminação', 1),
('Decoração', 1),
('Reforma', 1),
('Design de ambientes', 1),
('Área gourmet', 1),
('Projeto comercial', 1),
('Marcenaria planejada', 1),
('Acessibilidade', 1),
('Ambientes integrados', 1),
('Espaços corporativos', 1);
GO


/* 8. ASSOCIAÇÃO ENTRE PROJETOS E CATEGORIAS */
INSERT INTO PROJETO_CATEGORIA (PROJETO_ID, CATEGORIA_ID) VALUES
(1, 10), (1, 1), (1, 12), (1, 5),
(2, 2), (2, 13), (2, 11),
(3, 2), (3, 6), (3, 11),
(4, 10), (4, 8), (4, 5),
(5, 1), (5, 13), (5, 4),
(6, 10), (6, 14), (6, 5),
(7, 1), (7, 4), (7, 6),
(8, 10), (8, 8), (8, 5),
(9, 2), (9, 9), (9, 13),
(10, 10), (10, 8), (10, 5),
(11, 1), (11, 4),
(12, 2), (12, 11), (12, 13);
GO


/* 9. FEEDBACKS
   Cada cliente avalia somente projetos dos quais é proprietário.
*/
INSERT INTO FEEDBACK
(CLIENTE_ID, PROJETO_ID, NOTA, COMENTARIO, DATA_ENVIO, STATUS_APROVACAO, EXIBIR_NOME)
VALUES
(2, 1, 5, 'A proposta ficou muito acolhedora e funcional. A organização dos consultórios e da recepção atendeu ao que precisávamos.', '20260410', 'APROVADO', 1),
(2, 1, 4, 'Gostamos bastante da escolha dos materiais e da iluminação. O resultado transmite profissionalismo.', '20260418', 'APROVADO', 0),
(3, 2, 5, 'O projeto conseguiu traduzir nossa personalidade. Os ambientes ficaram modernos, confortáveis e muito bem aproveitados.', '20260422', 'APROVADO', 1),
(4, 3, 5, 'A marcenaria e as soluções de armazenamento fizeram muita diferença. O apartamento ficou bonito e prático.', '20260502', 'APROVADO', 0),
(4, 3, 4, 'Aproveitamento excelente dos espaços e cuidado nos detalhes. Fiquei satisfeita com o resultado.', '20260509', 'PENDENTE', 1),
(5, 4, 4, 'A proposta da cafeteria ficou muito interessante. O fluxo entre atendimento e mesas está bem resolvido.', '20260515', 'APROVADO', 1),
(6, 5, 5, 'A integração com o jardim e a entrada de luz natural foram os pontos que mais gostamos.', '20260520', 'APROVADO', 1),
(7, 6, 4, 'O layout atende bem à rotina da equipe e trouxe mais clareza para a organização dos ambientes.', '20260527', 'PENDENTE', 0),
(8, 7, 5, 'A combinação dos materiais naturais com o paisagismo ficou muito bonita e harmoniosa.', '20260603', 'APROVADO', 1),
(9, 8, 3, 'A proposta está no caminho certo. Ainda queremos avaliar alguns detalhes do salão e da iluminação.', '20260611', 'PENDENTE', 0),
(10, 9, 5, 'A varanda gourmet ficou excelente e a integração dos ambientes valorizou muito a cobertura.', '20260618', 'PENDENTE', 1),
(11, 10, 4, 'A distribuição dos produtos ficou mais intuitiva e o espaço ganhou uma identidade própria.', '20260625', 'REJEITADO', 1),
(12, 11, 5, 'Estamos animados com a proposta e com a forma como a casa se conecta com a paisagem.', '20260702', 'PENDENTE', 0),
(13, 12, 4, 'As ideias para os móveis multifuncionais parecem resolver muito bem as necessidades do studio.', '20260708', 'PENDENTE', 1);
GO