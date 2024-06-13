

/************************************************************/
/************************************************************/
/************************************************************/
/*						CREATE PROCEDURES					*/
/************************************************************/
/************************************************************/
/************************************************************/

DELIMITER $$
CREATE PROCEDURE proc_cadastrar_usuario(
IN param_email VARCHAR(255),
IN param_senha VARCHAR(64),
IN param_apelido VARCHAR(50)
)
root:BEGIN
	DECLARE var_id int; -- variavel para guardar o Id do usuário que foi cadastrado
    DECLARE error_msg varchar(200);
    
    /* IFs que quebram o laço caso o email ou senha ja existam nas tabelas */
    
    IF EXISTS(SELECT * FROM t02_credenciais WHERE A02_Email = param_email) then
		SET error_msg = concat("(45001) Erro: O Email ", param_email, " ja esta em uso");
		SIGNAL sqlstate '45001' set message_text = error_msg;
		LEAVE root;
    END if;
    
	IF EXISTS(SELECT * FROM t01_usuario WHERE A01_Apelido = param_apelido) then 
    SET error_msg = concat("(45002) Erro: O Apelido ", param_apelido, " ja esta em uso");
		SIGNAL sqlstate '45002' set message_text = error_msg;
		LEAVE root;
	END if;
    
	/* Inserção dos dados nas tabelas t01_usuario e t02_credenciais */
    
	INSERT INTO T01_Usuario (A01_Apelido, A01_Id_T00_Privilegios)
	VALUES (param_apelido, 1);
	
	/* Salva o Id do usuário na variavel "var_id", o qual ele encontra pelo apelido que foi inserido anteriormente na T01*/
	SELECT A01_Id from t01_usuario where A01_Apelido = param_apelido INTO var_id; 	
	
	/* Insere as credenciais do usuário na T02 usando o email, senha e o Id*/
	INSERT INTO T02_Credenciais (A02_Id_T01_Usuario, A02_Email, A02_Senha)
	VALUES (var_id, param_email, param_senha);
    
	/* Retornando um model*/
	SELECT * FROM t01_usuario WHERE A01_apelido = param_apelido;
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE proc_fazer_login(
IN param_email varchar(255),
IN param_senha varchar(64)
)
root:BEGIN
	declare id_do_cursor int;
    declare senha_do_cursor varchar(64);
    declare concluido BOOL DEFAULT false;
    
    
    -- Cursor para guardar a linha que contem o email informado
    declare cur_row_email CURSOR FOR 
    SELECT A02_Id_T01_Usuario, A02_Senha FROM t02_credenciais WHERE A02_Email = param_email;
    
    -- Declarando o handler "Não Encontrado" de iteracao do cursor
    declare continue handler for not found 
    SET concluido = true;
		
    OPEN cur_row_email;
    
    FETCH cur_row_email INTO id_do_cursor, senha_do_cursor;
    
    CLOSE cur_row_email;
    
    -- Se o fetch vir vazio, não há linhas na tabela que correspondam ao email
    IF concluido then
		SIGNAL sqlstate '45003' set message_text = "(45003) Email ou Senha invalidos";
		LEAVE root;
    END IF;
    
    IF senha_do_cursor <> param_senha then
		SIGNAL sqlstate '45003' set message_text = "(45003) Email ou Senha invalidos";
		LEAVE root;
    END IF;

	SELECT * FROM t01_usuario where A01_Id = id_do_cursor;
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE proc_buscar_usuario_por_email(
IN param_email varchar(255)
)
root:BEGIN
	declare id_vinculado_email int;
    
    SELECT A02_Id_T01_Usuario 
    FROM t02_credenciais 
    WHERE A02_email = param_email
    INTO id_vinculado_email;
    
    IF (id_vinculado_email IS NULL) then
		SIGNAL sqlstate '45001' set message_text = "(45001) Erro: email não cadastrado";
		LEAVE root;
    END if;
    
    SELECT * FROM t01_usuario WHERE A01_id = id_vinculado_email;
END $$
DELIMITER ;

DELIMITER $$
/* 	
	Essa procedure é a unica que usa parametros de saida OUT
	O Motivo é pois não há um modelo (tabela) para esse retorno, é necessário mapear um DTO com esses dados.
    Para isso usei o JDBC ao inves do JPA, assim faço o chamado via SimpleJdbcCall
    Para entender melhor, vá no UsuarioController da API e veja o método GET chamado "getDashboardInfo"
*/
CREATE PROCEDURE proc_carregar_dashboard(
IN param_id int,
OUT porcent_quizzes decimal(5,2),
OUT porcent_exercicios decimal(5,2),
OUT quant_flashcards int,
OUT linguagem_favorita ENUM('CSS', 'SQL')
)
root:BEGIN
	DECLARE ref_total_quizzes int;
    DECLARE ref_total_exercicios int;
    
    SELECT COUNT(*) AS total_quizzes
    FROM t03_quiz 
    INTO ref_total_quizzes;
    
    SELECT COUNT(*) AS total_exercicios
    FROM t09_exercicio
    INTO ref_total_exercicios;
    
    /* CALCULANDO PORCENT. DE QUIZZES*/
    SELECT ROUND((SELECT COUNT(*) FROM t06_progresso_usuario_quiz 
					WHERE A06_Id_T01_Usuario = param_id) * 100 / 
				(SELECT ref_total_quizzes), 2) 
			INTO porcent_quizzes;
    
    /* CALCULANDO PORCENT. DE EXERCICIOS */
    SELECT ROUND((SELECT COUNT(*) FROM t10_progresso_usuario_exercicio
					WHERE A10_Id_T01_Usuario = param_id) * 100 / 
				(SELECT ref_total_exercicios), 2)
			INTO porcent_exercicios;
	
    /* CONTANDO TOTAL DE FLASH CARDS DO USUARIO */
    SELECT COUNT(*) 
    FROM t07_flashcard_baralho 
    WHERE A07_Criador_T01_Usuario = param_id
    INTO quant_flashcards;
    
    /* LINGUAGEM FAVORITA */
    SET linguagem_favorita = 1;
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE proc_buscar_quizzes_ref_usuario(
IN param_id int,
IN param_linguagem int,
IN param_limit int,
IN param_offset int
)
BEGIN
    DECLARE intPrivilegio int;
	DECLARE isAdministrador bool;
    
    SELECT A01_Id_T00_Privilegios FROM t01_usuario WHERE A01_Id = param_id INTO intPrivilegio;
    
    IF intPrivilegio = 1 THEN
		SET isAdministrador = FALSE;
	ELSE 
		SET isAdministrador = TRUE;
	END IF;
    
    
    SELECT 
	t03.A03_id AS 'id', 
	t03.A03_titulo AS 'titulo', 
	t03.A03_linguagem AS 'linguagem', 
	t03.A03_Criador_T01_usuario AS 'id_criador',
	t06.A06_Nota_Quiz as 'nota'
	FROM t03_quiz t03
		LEFT JOIN ( 
			SELECT * 
			FROM t06_progresso_usuario_quiz
			WHERE A06_Id_T01_Usuario = param_id
		) t06 on t03.A03_Id = t06.A06_Id_T03_Quiz
	WHERE (t03.A03_Criador_T01_usuario = CASE WHEN (isAdministrador = FALSE) THEN 1 ELSE param_id END)
		AND t03.A03_linguagem = param_linguagem
	LIMIT param_limit
	OFFSET param_offset;
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE proc_buscar_meus_quizzes(
IN param_id int,
IN param_linguagem int,
IN param_limit int,
IN param_offset int
)
BEGIN
	SELECT 
	t03.A03_id AS 'id', 
	t03.A03_titulo AS 'titulo', 
	t03.A03_linguagem AS 'linguagem', 
	t03.A03_Criador_T01_usuario AS 'id_criador',
	t06.A06_Nota_Quiz as 'nota'
	FROM t03_quiz t03
		LEFT JOIN ( 
			SELECT * 
			FROM t06_progresso_usuario_quiz
			WHERE A06_Id_T01_Usuario = param_id
		) t06 on t03.A03_Id = t06.A06_Id_T03_Quiz
	WHERE t03.A03_Criador_T01_usuario = param_id AND t03.A03_linguagem = param_linguagem
	LIMIT param_limit
	OFFSET param_offset;
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE proc_pesquisar_quizzes(
IN param_id int,
IN param_linguagem int,
IN param_termo varchar(100),
IN param_limit int,
IN param_offset int
)
BEGIN
    DECLARE intPrivilegio int;
	DECLARE isAdministrador bool;
    
    SELECT A01_Id_T00_Privilegios FROM t01_usuario WHERE A01_Id = param_id INTO intPrivilegio;
    
    IF intPrivilegio = 1 THEN
		SET isAdministrador = FALSE;
	ELSE 
		SET isAdministrador = TRUE;
	END IF;
    
    
    SELECT 
	t03.A03_id AS 'id', 
	t03.A03_titulo AS 'titulo', 
	t03.A03_linguagem AS 'linguagem', 
	t03.A03_Criador_T01_usuario AS 'id_criador',
	t06.A06_Nota_Quiz as 'nota'
	FROM t03_quiz t03
		LEFT JOIN ( 
			SELECT * 
			FROM t06_progresso_usuario_quiz
			WHERE A06_Id_T01_Usuario = param_id
		) t06 on t03.A03_Id = t06.A06_Id_T03_Quiz
	WHERE (t03.A03_Criador_T01_usuario = CASE WHEN (isAdministrador = FALSE) THEN 1 ELSE param_id END)
		AND t03.A03_linguagem = param_linguagem
	LIMIT param_limit
	OFFSET param_offset;
END $$
DELIMITER ;

/************************************************************/
/************************************************************/
/************************************************************/
/*						INSERT DUMMY VALUES					*/
/************************************************************/
/************************************************************/
/************************************************************/

DELIMITER $$
CREATE PROCEDURE proc_popular_banco_dummy(
)
BEGIN
	DECLARE id_do_dummy int;
    DECLARE id_do_quiz_teste int;
    DECLARE id_da_pergunta_teste int;
    DECLARE id_do_baralho_teste int;
    DECLARE id_do_exercicio_teste int;
    
    /* POPULANDO T00 */
	INSERT INTO t00_privilegios (a00_privilegio)
    VALUES
		('usuario'),
        ('admin');
    
    /* POPULANDO T01 */
    INSERT INTO t01_usuario (A01_apelido, A01_Id_T00_Privilegios)
    VALUES
		('Admin', 2);
        
        /* guardando ID */
		SELECT A01_id FROM t01_usuario WHERE A01_Apelido = "Admin" INTO id_do_dummy;
    
    /* POPULANDO T02 */
	INSERT INTO t02_credenciais (A02_Id_T01_Usuario, A02_Email, A02_Senha)
    VALUES
		(id_do_dummy, "admin@cdcrmbs.com", "admin1234");
    
		/*CALL proc_cadastrar_usuario("admin@cdcrmbs.com", "admin", "Admin");*/
    
    /* POPULANDO T03 */
    INSERT INTO t03_quiz (A03_Titulo, A03_Linguagem, A03_Criador_T01_Usuario) 
    VALUES 
		('TESTE Alinhamento de Elementos', 1, id_do_dummy);

		/* PEGANDO REFERENCIA DO PRIMEIRO QUIZ*/
		SELECT A03_Id from t03_quiz WHERE A03_Titulo = 'TESTE Alinhamento de Elementos' INTO id_do_quiz_teste;
        /***************************************************************************************************/
        /* POPULANDO T04 - PRIMEIRA PERGUNTA*/
		INSERT INTO t04_quiz_perguntas (A04_Enunciado, A04_Id_T03_Quiz) 
		VALUES
			('TESTE qual é a propriedade que alinha o texto de um elemento ao centro?', id_do_quiz_teste);
		SELECT A04_Id from t04_quiz_perguntas 
		WHERE A04_Enunciado = 'TESTE qual é a propriedade que alinha o texto de um elemento ao centro?' INTO id_da_pergunta_teste;
			
			/* POPULANDO T05 - PRIMEIRA PERGUNTA - PERGUNTA 01 */
			INSERT INTO t05_quiz_respostas (A05_Texto_Resposta, A05_is_Resposta_Correta, A05_Id_T04_Quiz_Perguntas)
			VALUES
				('text-align: center', 1, id_da_pergunta_teste),
				('align-text: center', 0, id_da_pergunta_teste),
				('text-alignment: center', 0, id_da_pergunta_teste),
				('text: center', 0, id_da_pergunta_teste);
		
        /* POPULANDO T04 - SEGUNDA PERGUNTA*/
        INSERT INTO t04_quiz_perguntas (A04_Enunciado, A04_Id_T03_Quiz) 
		VALUES
			('TESTE qual comando muda a cor de fundo de um elemento para vermelho?', id_do_quiz_teste);
		SELECT A04_Id from t04_quiz_perguntas 
		WHERE A04_Enunciado = 'TESTE qual comando muda a cor de fundo de um elemento para vermelho?' INTO id_da_pergunta_teste;
        
			/* POPULANDO T05 - PRIMEIRA PERGUNTA - PERGUNTA 01 */
			INSERT INTO t05_quiz_respostas (A05_Texto_Resposta, A05_is_Resposta_Correta, A05_Id_T04_Quiz_Perguntas)
			VALUES
				('background: red', 0, id_da_pergunta_teste),
				('bg-color: red', 0, id_da_pergunta_teste),
                ('background-color: red', 1, id_da_pergunta_teste),
				('background-image: red', 0, id_da_pergunta_teste);
                
		/* POPULANDO T04 - TERCEIRA PERGUNTA*/
        INSERT INTO t04_quiz_perguntas (A04_Enunciado, A04_Id_T03_Quiz) 
		VALUES
			('TESTE qual comando altera o alinhamento horizontal do display flex para alinhamento vertical', id_do_quiz_teste);
		SELECT A04_Id from t04_quiz_perguntas 
		WHERE A04_Enunciado = 'TESTE qual comando altera o alinhamento horizontal do display flex para alinhamento vertical' INTO id_da_pergunta_teste;
        
			/* POPULANDO T05 - PRIMEIRA PERGUNTA - PERGUNTA 01 */
			INSERT INTO t05_quiz_respostas (A05_Texto_Resposta, A05_is_Resposta_Correta, A05_Id_T04_Quiz_Perguntas)
			VALUES
				('flex-alignment: vertical', 0, id_da_pergunta_teste),
				('flex-direction: vertical', 0, id_da_pergunta_teste),
                ('flex-orientation: column', 0, id_da_pergunta_teste),
				('flex-direction: column', 1, id_da_pergunta_teste);
				
			
	

        
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

