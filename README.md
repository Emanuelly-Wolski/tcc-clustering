
## TCC Clustering

Este repositório contém o sistema de clusterização de perfis de alunos e professores, desenvolvido como parte do Trabalho de Conclusão de Curso de Tecnologia em Análise e Desenvolvimento de Sistemas - UFPR 2025/01.

O objetivo deste microsserviço é agrupar perfis semelhantes com base em critérios como temas de interesse, turno e disponibilidade, utilizando técnicas de Machine Learning (clusterização) para facilitar a formação de equipes compatíveis de TCC.

## Tecnologias Utilizadas

- Java + Spring Boot – Backend principal
- Python + FastAPI – Serviço de clusterização
- React + Material-UI – Frontend
- PostgreSQL – Banco de dados relacional
- K-Means – Algoritmo de clusterização

## Como Funciona

- O usuário preenche suas preferências no sistema (aluno ou professor).
- Ao buscar por perfis compatíveis, os dados são enviados ao serviço de clusterização.
- O algoritmo K-Means agrupa os usuários com base nas semelhanças entre os perfis.
- O sistema retorna os três perfis mais compatíveis com o usuário logado.
- Os dados de cluster são salvos em uma tabela específica no banco para futuras análises e recomendações.

## Inicialização dos Serviços

Para que a clusterização funcione corretamente, é necessário iniciar dois serviços principais: o backend em Java (Spring Boot) e o serviço de clusterização em Python (FastAPI). Além disso, o banco de dados PostgreSQL deve estar disponível e configurado.

### 1. Serviço de Clusterização (Python)

1. Instale as dependências (apenas uma vez):

```
pip install fastapi uvicorn pandas scikit-learn
```

2. Acesse o diretório:

```
tcc-clustering/clustering-service
```

3. Inicie o serviço com o comando:

```
uvicorn clustering_service:app --reload --port 8001
```

Explicação do comando:

- `clustering_service`: nome do arquivo `.py`
- `app`: nome da variável que contém a instância FastAPI
- `--reload`: reinicia automaticamente ao detectar alterações no código
- `--port 8001`: define a porta de execução do serviço

### 2. Backend (Spring Boot)

Após iniciar o serviço Python, inicie o backend com:

```
./mvnw spring-boot:run
```

A aplicação ficará disponível em:

```
http://localhost:8082/api/cluster
```

## Estrutura do Projeto

- `clustering-service/`  
  Serviço FastAPI responsável pela clusterização e sugestões de perfis.

- `spring-clustering/`  
  Backend Java responsável pelo armazenamento de preferências, controle de clusters e integração com o serviço Python.

## Observações

- A comunicação entre backend Java e o serviço Python é feita via requisições HTTP usando `RestTemplate`.
- O sistema armazena o cluster de cada usuário para futuras recomendações.
