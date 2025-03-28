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