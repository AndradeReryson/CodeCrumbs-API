DELIMITER $$
CREATE PROCEDURE proc_cadastrar_usuario(
IN param_email VARCHAR(255),
IN param_senha VARCHAR(64),
IN param_apelido VARCHAR(50)
)
BEGIN
	DECLARE var_id int; -- variavel para guardar o Id do usuário que foi cadastrado
    
    /*Insere o Apelido selecionado pelo usuário na T01*/
	INSERT INTO T01_Usuario (A01_Apelido)
    VALUES (param_apelido);
	
    /* Salva o Id do usuário na variavel "var_id", o qual ele encontra pelo apelido que foi inserido anteriormente na T01*/
    SELECT A01_Id INTO var_id from t01_usuario where A01_Apelido = param_apelido; 	
    
    /* Insere as credenciais do usuário na T02 usando o email, senha e o Id*/
    INSERT INTO T02_Credenciais (A02_Id_T01_Usuario, A02_Email, A02_Senha)
    VALUES (var_id, param_email, param_senha);
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE proc_fazer_login(
IN param_email varchar(255),
IN param_senha varchar(64)
)
BEGIN
	DECLARE var_id int;

	IF EXISTS (SELECT * FROM t02_credenciais where A02_Email = param_email AND A02_Senha = param_senha) then
		SELECT A02_Id_T01_Usuario INTO var_id from t02_credenciais where A02_Email = param_email AND A02_Senha = param_senha;
        
        SELECT A01_Apelido, A01_Id from t01_usuario where A01_Id = var_id;
	END IF;
END $$
DELIMITER ;

