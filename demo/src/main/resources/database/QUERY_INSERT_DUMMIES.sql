DELIMITER $$
CREATE PROCEDURE proc_popular_banco_dummy(
)
BEGIN
	DECLARE id_do_dummy int;
    DECLARE id_do_quiz_teste int;
    DECLARE id_da_pergunta_teste int;
    DECLARE id_do_baralho_teste int;
    DECLARE id_do_exercicio_teste int;
    
    /* POPULANDO T01 e T02 */
	CALL proc_cadastrar_usuario("admin@codecrumbs.com", "12345678", "Admin");
	SELECT A01_id FROM t01_usuario WHERE A01_Apelido = "Admin" INTO id_do_dummy;
    
    /* POPULANDO T03 */
    INSERT INTO t03_quiz (A03_Titulo, A03_Linguagem, A03_Criador_T01_Usuario) 
    VALUES 
		('TESTE Alinhamento de Elementos', 1, id_do_dummy),
        ('TESTE Alterando cores', 1, id_do_dummy),
        ('TESTE Alinhamento de Elementos 2', id_do_dummy, 1),
        ('TESTE Alterando cores 2', id_do_dummy, 1);
	SELECT A03_Id from t03_quiz WHERE A03_Titulo = 'TESTE Alinhamento de Elementos' INTO id_do_quiz_teste;
    
    /* POPULANDO T04 */
    INSERT INTO t04_quiz_perguntas (A04_Enunciado, A04_Id_T03_Quiz) 
    VALUES
		('TESTE qual é a propriedade que alinha o texto de um elemento ao centro?', id_do_quiz_teste);
	SELECT A04_Id from t04_quiz_perguntas 
    WHERE A04_Enunciado = 'TESTE qual é a propriedade que alinha o texto de um elemento ao centro?' INTO id_da_pergunta_teste;
        
	/* POPULANDO T05 */
    INSERT INTO t05_quiz_respostas (A05_Texto_Resposta, A05_is_Resposta_Correta, A05_Id_T04_Quiz_Perguntas)
    VALUES
		('text-align: center', 1, id_da_pergunta_teste),
		('align-text: center', 0, id_da_pergunta_teste),
        ('text-alignment: center', 0, id_da_pergunta_teste),
        ('text: center', 0, id_da_pergunta_teste);
        
    /* POPULANDO T06 */
    INSERT INTO t06_progresso_usuario_quiz (A06_Id_T01_Usuario, A06_Id_T03_Quiz, A06_Nota_Quiz) 
    VALUES
		(id_do_dummy, id_do_quiz_teste, '0');
        
	/* POPULANDO T07 */
    INSERT INTO t07_flashcard_baralho (A07_Titulo, A07_Cor, A07_Linguagem, A07_Criador_T01_Usuario)
    VALUES
		('TESTE propriedades CSS', '#B0DD16', 1, id_do_dummy);
	SELECT A07_Id FROM t07_flashcard_baralho WHERE A07_Titulo = 'TESTE propriedades CSS' INTO id_do_baralho_teste;
        
	/* POPULANDO T08 */
    INSERT INTO t08_flashcard_cartao (A08_Termo, A08_Definicao, A08_Id_T07_Flashcard_Baralho)
    VALUES 
		('Display: Flex', 'Define o display interno do elemento para flex, isto é, alinhar items um atrás do outro', id_do_baralho_teste),
        ('Display: None', 'Define o display para nenhum, ocultando o elemento do DOM', id_do_baralho_teste),
        ('Display: Grid', 'Define o display para grid, isto é, alinhamento interno em linhas e colunas', id_do_baralho_teste);
        
	/* POPULANDO T09 */
    INSERT INTO t09_exercicio (A09_Linguagem, A09_Enunciado, A09_Texto_Codigo, A09_Resposta)
	VALUES
		(1, 'Pinte o fundo da Div para a cor vermelha', '<style>\n    div {\n        background-color: ___\n    }\n</style>', 'red'),
        (1, 'Pinte o fundo da Div para a cor verde', '<style>\n    div {\n        background-color: _____\n    }\n</style>', 'green');
	SELECT A09_id from t09_exercicio where A09_Enunciado = 'Pinte o fundo da Div para a cor vermelha' INTO id_do_exercicio_teste;
    
    /* POPULANDO T10 */
    INSERT INTO t10_progresso_usuario_exercicio (A10_Id_T01_Usuario, A10_Id_T09_Exercicio)
    VALUES
		(id_do_dummy, id_do_exercicio_teste);
END $$
DELIMITER ;
        
CALL proc_popular_banco_dummy();