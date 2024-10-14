CREATE TABLE preferencias_professor (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    turnos VARCHAR(20)[] NOT NULL,
    disponivel_orientacao VARCHAR(10)[] NOT NULL,
    linguagens_de_programacao VARCHAR(50)[] NOT NULL,
    disciplinas_lecionadas VARCHAR(100)[] NOT NULL,
    habilidades_pessoais VARCHAR(50)[] NOT NULL,
    temas_de_interesse VARCHAR(100)[] NOT NULL,
    disponibilidades VARCHAR(20)[] NOT NULL,
    modalidades_trabalho VARCHAR(20)[] NOT NULL
);
