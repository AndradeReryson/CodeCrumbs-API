-- CREATE DATABASE CodeCrumbs;
-- USE CodeCrumbs;

CREATE TABLE T01_Usuario (
	A01_Id INT PRIMARY KEY auto_increment,
    A01_Apelido VARCHAR(50) not null unique
);

CREATE TABLE T02_Credenciais (
	A02_Id int primary key auto_increment,
	A02_Id_T01_Usuario int not null,
    A02_Email varchar(255) unique not null,
    A02_Senha varchar(64) not null,
    FOREIGN KEY (A02_Id_T01_Usuario) REFERENCES T01_Usuario(A01_Id)
);

-- TABELAS QUIZ
CREATE TABLE T03_Quiz (
	A03_Id int primary key auto_increment,
    A03_Titulo varchar(100) unique not null, 
    A03_Linguagem ENUM("SQL", "CSS"),
    A03_Criador_T01_Usuario int not null,
    FOREIGN KEY (A03_Criador_T01_Usuario) REFERENCES T01_Usuario(A01_Id)
);

CREATE TABLE T04_Quiz_Perguntas (
	A04_Id int primary key auto_increment,
    A04_Enunciado varchar(200) not null,
    A04_Id_T03_Quiz int not null,
    foreign key (A04_Id_T03_Quiz) references T03_Quiz(A03_Id)
);

CREATE TABLE T05_Quiz_Respostas (
	A05_Id int primary key auto_increment,
    A05_Texto_Resposta varchar(100) not null,
    A05_is_Resposta_Correta tinyint(1) not null,
    A05_Id_T04_Quiz_Perguntas int not null,
    FOREIGN KEY (A05_Id_T04_Quiz_Perguntas) references T04_Quiz_Perguntas(A04_Id)
);

CREATE TABLE T06_Progresso_Usuario_Quiz (
	A06_Id_T01_Usuario int not null,
    A06_Id_T03_Quiz int not null,
    A06_Nota_Quiz Decimal(5,2),
    FOREIGN KEY (A06_Id_T01_Usuario) REFERENCES T01_Usuario(A01_Id),
    FOREIGN KEY (A06_Id_T03_Quiz) REFERENCES T03_Quiz(A03_Id)
);

-- TABELAS FLASHCARD
CREATE TABLE T07_Flashcard_Baralho (
	A07_Id int primary key auto_increment,
    A07_Titulo varchar(100) not null,
    A07_Cor varchar(7) not null,
    A07_Linguagem ENUM("SQL", "CSS", "Outro"),
    A07_Criador_T01_Usuario int not null,
    foreign key (A07_Criador_T01_Usuario) references T01_Usuario(A01_Id)
);

CREATE TABLE T08_Flashcard_Cartao (
	A08_Id int primary key auto_increment,
    A08_Termo varchar(100) not null,
    A08_Definicao varchar(100),
    A08_Id_T07_Flashcard_Baralho int not null,
    foreign key (A08_Id_T07_Flashcard_Baralho) references T07_Flashcard_Baralho(A07_Id)
);

-- TABELAS EXERCICIO
CREATE TABLE T09_Exercicio (
	A09_Id int primary key auto_increment,
    A09_Linguagem ENUM("SQL", "CSS"),
    A09_Enunciado varchar(150) not null,
    A09_Texto_Codigo text(200) not null,
    A09_Resposta varchar(200) not null
);

CREATE TABLE T10_Progresso_Usuario_Exercicio (
	A10_Id int primary key auto_increment,
	A10_Id_T01_Usuario int not null,
    A10_Id_T09_Exercicio int not null,
    FOREIGN KEY (A10_Id_T01_Usuario) REFERENCES T01_Usuario(A01_Id),
    FOREIGN KEY (A10_Id_T09_Exercicio) REFERENCES T09_Exercicio(A09_Id)
);