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
    
	INSERT INTO T01_Usuario (A01_Apelido)
	VALUES (param_apelido);
	
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
IN param_limit int,
IN param_offset int
)
BEGIN
	SELECT 
	t03.A03_id AS 'id', 
	t03.A03_titulo AS 'titulo', 
    t03.A03_linguagem AS 'linguagem', 
    t03.A03_Criador_T01_usuario AS 'id_criador',
    CASE WHEN EXISTS (SELECT A06_Id FROM t06_progresso_usuario_quiz WHERE A06_Id_T01_Usuario = param_id AND A06_Id_T03_Quiz = t03.A03_Id)
		THEN TRUE
        ELSE FALSE
	END AS 'concluido',
    CASE WHEN EXISTS (SELECT A06_Nota_Quiz FROM t06_progresso_usuario_quiz WHERE A06_Id_T01_Usuario = param_id AND A06_Id_T03_Quiz = t03.A03_Id)
		THEN A06_Nota_Quiz
        ELSE NULL
	END AS 'nota'
	FROM t06_progresso_usuario_quiz t06
	RIGHT JOIN t03_quiz t03 ON t06.A06_Id_T03_Quiz = t03.A03_Id
    LIMIT param_limit
    OFFSET param_offset;
END $$
DELIMITER ;
    

/* TESTES */
CALL proc_carregar_dashboard(1, @out_porcent_quiz, @out_porcent_exerc, @out_quant_flashcards, @out_ling_fav);
CALL proc_buscar_quizzes_ref_usuario(1, 2, 2);