CREATE TABLE preferencias_aluno (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    turnos VARCHAR(20)[] NOT NULL,
    linguagens_de_programacao VARCHAR(50)[] NOT NULL,
    bancos_de_dados VARCHAR(50)[] NOT NULL,
    nivel_experiencia VARCHAR(20) NOT NULL,
    habilidades_pessoais VARCHAR(50)[] NOT NULL,
    temas_de_interesse VARCHAR(100)[] NOT NULL,
    disponibilidades VARCHAR(20)[] NOT NULL,
    modalidade_trabalho VARCHAR(20)[] NOT NULL,
    framework_front VARCHAR(30)[] NOT NULL
);

