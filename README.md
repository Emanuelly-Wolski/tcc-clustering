# 🎓 TCC Clustering

_Este repositório contém o sistema de clusterização de perfis de alunos e professores, desenvolvido como parte do Trabalho de Conclusão de Curso do Tecnologia em Análise e Desenvolvimento de Sistemas - UFPR 2025/01._

O objetivo desse microsserviço é agrupar perfis semelhantes com base em critérios como **temas de interesse**, **turno** e **disponibilidade**, utilizando técnicas de Machine Learning (clusterização) para facilitar a formação de equipes compatíveis de TCC.

---

## 🧰 Tecnologias Utilizadas

- **Java + Spring Boot** – Backend principal  
- **Python + FastAPI** – Serviço de clusterização  
- **React + Material-UI** – Frontend  
- **PostgreSQL** – Banco de dados relacional  
- **K-Means** – Algoritmo de clusterização

---

## 🧠 Como Funciona

- O usuário preenche suas preferências no sistema (aluno ou professor).  
- Ao buscar por perfis compatíveis, os dados são enviados para o serviço de clusterização.  
- O algoritmo **K-Means** agrupa os usuários com base nas semelhanças.  
- O sistema retorna os **3 perfis mais compatíveis** com o usuário logado.  
- Os clusters também são salvos em uma tabela específica no banco de dados para futuras análises.

---

## ⚙️ Inicialização dos Serviços

Para a clusterização funcionar corretamente, é necessário iniciar dois serviços:

### 🔹 1. Serviço de Clusterização (Python)

1. Instale as dependências (apenas uma vez):
pip install fastapi uvicorn pandas scikit-learn

2. Sempre que for utilizar o sistema, acesse o diretório:
tcc-clustering/clustering-service

3. Inicie o serviço com o comando:
uvicorn clustering_service:app --reload --port 8001

Explicando o comando:

- `clustering_service`: nome do arquivo Python  
- `app`: nome da variável que contém a instância do `FastAPI()`  
- `--reload`: reinicia automaticamente o servidor ao detectar alterações  
- `--port 8001`: define a porta de execução do serviço

### 🔹 2. Backend (Spring Boot)

Após iniciar o serviço Python, inicie o backend Java normalmente com:
./mvnw spring-boot:run
