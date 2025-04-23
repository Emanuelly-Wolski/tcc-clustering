# TCC Clustering

_Este repositório contém o sistema de clusterização de perfis de alunos e professores, desenvolvido como parte do Trabalho de Conclusão de Curso do TADS - UFPR 2025._

O objetivo é agrupar perfis semelhantes com base em critérios como temas de interesse, turno e disponibilidade, utilizando técnicas de machine learning (Clusterização) para facilitar a formação de equipes compatíveis.

## Tecnologias Utilizadas
* Java + Spring Boot – Backend principal
* Python + FastAPI – Serviço de clusterização
* React, Material-UI - Frontend
* PostgreSQL – Banco de dados relacional
* K-Means – Algoritmo de clusterização

## Como Funciona
* O usuário preenche suas preferências no sistema.
* Ao buscar por perfis compatíveis, os dados são enviados para o serviço de clusterização.
* O algoritmo K-Means agrupa os usuários com base nas semelhanças.
* O sistema retorna os 3 perfis mais compatíveis com o usuário logado.
* Os clusters também são salvos em uma tabela específica para futuras análises.

## Inicialização dos serviços:
Para a clusterização funcionar corretamente é preciso:

* Instalar 1x no terminal do cluster:
  pip install fastapi uvicorn pandas scikit-learn

* Sempre que for utilizar o sistema devemos estar dentro da pasta tcc-clustering\clustering-service e digitar o comando abaixo para iniciar o serviço python:
  uvicorn clustering_service:app --reload --port 8001

Explicando o comando:
* clustering_service: é o nome do arquivo

* app: é o nome da variável que contém o FastAPI() que é a camada que transforma o código Python em uma API REST o tornando acessível a outras partes do sistema.

* --reload: reinicia o servidor automaticamente.

Por fim, deve ser iniciado o Spring boot.