from faker import Faker
from unidecode import unidecode
import pandas as pd
import random

# Atribuindo à 'fake' a versão em português da biblioteca:
fake = Faker('pt_BR')

# Definição das opções
turnos = ['Vespertino', 'Noturno']

disponivel_orientacao = ['Sim', 'Não']

dominios_email = ['@gmail.com', '@hotmail.com', '@outlook.com', '@ufpr.br']

linguagens_de_programacao = [
    'Python', 'SQL', 'Java', 'C++', 'R', 'C#', 'C', 
    'PHP','TypeScript', 'JavaScript'
]

framework = [
    'React', 'Angular', 'Vue', 'Bootstrap'
]

bancos_de_dados = [
    'MySQL', 'PostgreSQL', 'MongoDB', 'Oracle',
    'Cassandra', 'MariaDB', 'SQL Server'
]
disciplinas_lecionadas = [
    'Estrutura de Dados', 'Banco de Dados I', 'Banco de Dados II', 'Banco de Dados III', 'LPOO I', 'LPOO II',
    'Engenharia de Software I', 'Engenharia de Software II', 'Linguagem C', 'Estrutura de Dados II', 'Análise e Projeto de Sistemas I',
    'Análise e projeto de sistemas II', 'Interação humano - computador', 'Desenvolvimento Web I', 'Desenvolvimento Web II',
    'Desenvolvimento para dispositivos móveis', 'Desenvolvimento de Aplicações Corporativas', 'Rede de computadores', 'Sistemas de informação', 'Sistemas operacionais',
    'Administração de sistemas', 'Engenharia de Requisitos', 'Estatística para computação', 'Introdução a arquitetura de computadores', 'Lógica matemática',
    'Matemática para computação', 'Modelagem e Análise de processo de negócio', 'Gestão de empresas', 'Ferramentas da Qualidade', 'Empreendedorismo e inovação',
    'Governança e tecnologia da informação', 'TCC I', 'TCC II', 'Biologia computacional e sistemas', 'Inteligência artificial aplicada','Sistemas embarcados e internet das coisas'
]

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

# Lendo apenas a coluna 'Nome Completo' do arquivo Excel
nomes_alunos = pd.read_excel('../../dados/alunos.xlsx', usecols=['Nome Completo'])['Nome Completo'].tolist()

professores = []

for _ in range(20):

    while True:
        nome = fake.name()
        if nome not in nomes_alunos:
            break

    email = nome.lower().replace(" ", "") + random.choice(dominios_email)
    email = unidecode(email)  # Retira os acentos
    turno = random.choice(turnos)
    linguagens = ', '.join(random.sample(linguagens_de_programacao, random.randint(1, 2)))
    disciplinas = ', '.join(random.sample(disciplinas_lecionadas, random.randint(2, 4)))
    habilidades = ', '.join(random.sample(habilidades_pessoais, random.randint(2, 3)))
    temas = ', '.join(random.sample(temas_de_interesse, random.randint(2, 3)))
    orientacao = random.choice(disponivel_orientacao)
    disponibilidade = random.choice(disponibilidades)
    modalidade_trabalho = random.choice(modalidades_trabalho)

    professores.append([
        nome, email, 'Professor(a)', turno, linguagens, disciplinas,
        habilidades, temas, orientacao, disponibilidade, modalidade_trabalho
    ])

# Criando o DataFrame
df_professores = pd.DataFrame(professores, columns=[
    "Nome Completo", "Email", "Tipo de Usuário", "Turno", "Linguagem de Programação",
    "Disciplinas Lecionadas", "Habilidades Pessoais", "Temas de Interesse",
    "Disponível Orientação", "Disponibilidade", "Modalidade de Trabalho"
])

# Removendo os títulos dos nomes gerados... Sra. Sr. Dr. Dra
df_professores['Nome Completo'] = df_professores['Nome Completo'].str.replace(
    r'^(Sra\. |Srta\. |Dr\. |Dra\. |Sr\. )', '', regex=True)

# Salvando o DataFrame em Excel na pasta 'dados'
df_professores.to_excel('../../dados/professores.xlsx', index=False)