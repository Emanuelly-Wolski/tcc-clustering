from faker import Faker
from unidecode import unidecode
import pandas as pd
import random

# Atribuindo à 'fake' a versão em português da biblioteca:
fake = Faker('pt_BR')

# Definindo as opções para os campos
turnos = ['Vespertino', 'Noturno']

dominios_email = ['@gmail.com', '@hotmail.com', '@outlook.com', '@ufpr.br']

linguagens_de_programacao = [
    'Python', 'SQL', 'Java', 'C++', 'R', 'C#', 'C', 
    'PHP','TypeScript', 'JavaScript'
]

framework_front = [
    'React', 'Angular', 'Vue', 'Bootstrap'
]

bancos_de_dados = [
    'MySQL', 'PostgreSQL', 'MongoDB', 'Oracle',
    'Cassandra', 'MariaDB', 'SQL Server'
]

nivel_experiencia = ['Básico', 'Intermediário', 'Avançado']

habilidades_pessoais = [
    'Comunicação', 'Organização', 'Proatividade', 'Pensamento estratégico',
    'Liderança', 'Planejamento', 'Trabalho em equipe', 'Adaptabilidade',
    'Atenção', 'Criatividade','Resiliência','Gerenciamento de tempo', 'Negociação',
    'Resolução de problemas'
]

temas_de_interesse = [
    'Tecnologia e inovação', 'Educação', 'Meio ambiente e sustentabilidade',
    'Inteligência artificial', 'Análise de dados', 'Metodologias ágeis',
    'Economia e Finanças', 'Saúde e bem estar', 'Cidadania', 'Política'
]


# Novas colunas
disponibilidades = ['Dias úteis', 'Final de semana', 'Flexível']
modalidades_trabalho = ['Presencial', 'Remoto', 'Flexível']

alunos = []

for _ in range(300):
    nome = fake.name()
    email = nome.lower().replace(" ", "") + random.choice(dominios_email)
    email = unidecode(email)  # Retira os acentos
    turno = random.choice(turnos)
    linguagens = ', '.join(random.sample(linguagens_de_programacao, random.randint(1, 2)))
    framework = ', '.join(random.sample(framework_front, random.randint(1, 2)))
    banco_dados = ', '.join(random.sample(bancos_de_dados, random.randint(1, 2)))
    nivel = random.choice(nivel_experiencia)
    habilidades = ', '.join(random.sample(habilidades_pessoais, random.randint(2, 3)))
    temas = ', '.join(random.sample(temas_de_interesse, random.randint(2, 3)))
    disponibilidade = random.choice(disponibilidades)
    modalidade_trabalho = random.choice(modalidades_trabalho)
    
    alunos.append([
        nome, email, 'Aluno', turno, linguagens, framework, banco_dados, nivel,
        habilidades, temas, disponibilidade, modalidade_trabalho
    ])

# Criando o DataFrame
df_alunos = pd.DataFrame(alunos, columns=[
    "Nome Completo", "Email", "Tipo de Usuário", "Turno", "Linguagem de Programação", "Framework Front-end",
    "Banco de Dados", "Nível de Experiência", "Habilidades Pessoais", "Temas de Interesse",
    "Disponibilidade", "Modalidade de Trabalho"
])

# Remover os títulos dos nomes gerados... Sra. Sr. Dr. Dra.
df_alunos['Nome Completo'] = df_alunos['Nome Completo'].str.replace(
    r'^(Sra\. |Srta\. |Dr\. |Dra\. |Sr\. )', '', regex=True)

# Filtrando os nomes duplicados
nomes_duplicados = df_alunos['Nome Completo'].duplicated()
if nomes_duplicados.any():
    df_alunos.loc[nomes_duplicados, 'Nome Completo'] = df_alunos.loc[nomes_duplicados, 'Nome Completo'] + ' Silva'
    df_alunos['Email'] = df_alunos['Nome Completo'].str.lower().str.replace(" ", "") + random.choice(dominios_email)
    df_alunos['Email'] = df_alunos['Email'].apply(unidecode)

# Estatísticas
total_alunos = df_alunos.shape[0]
print(f'Total de alunos: {total_alunos}')

nomes_duplicados = df_alunos['Nome Completo'].duplicated().sum()
print(f'Quantidade de nomes duplicados: {nomes_duplicados}')

alunos_por_turno = df_alunos['Turno'].value_counts()
print('Quantidade de alunos por turno:')
print(alunos_por_turno)

# Salvando em Excel
df_alunos.to_excel('../../dados-testes/alunos.xlsx', index=False)