# clustering_service.py
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List
import pandas as pd
from sklearn.cluster import KMeans
from sklearn.decomposition import PCA
from sklearn.preprocessing import MultiLabelBinarizer, OneHotEncoder, StandardScaler

app = FastAPI()

# Modelo para cada aluno
class Student(BaseModel):
    id: int
    nomeCompleto: str
    email: str
    turno: str
    disponibilidade: str
    temasDeInteresse: List[str]
    userRole: str = None 

# Modelo do request que o endpoint receberá
class MatchingRequest(BaseModel):
    userId: int
    students: List[Student]

# Endpoint para obter sugestões para um usuário específico
@app.post("/clustering")
def get_matching(request: MatchingRequest):
    # Converte a lista de alunos para DataFrame
    data = [student.dict() for student in request.students]
    df = pd.DataFrame(data)
    
    if df.empty:
        raise HTTPException(status_code=400, detail="Nenhum aluno fornecido")
    
    # Renomeia as colunas
    df.rename(columns={
        'nomeCompleto': 'Nome Completo',
        'turno': 'Turno',
        'disponibilidade': 'Disponibilidade',
        'temasDeInteresse': 'Temas de Interesse'
    }, inplace=True)
    
    # Vetorização dos temas de interesse
    mlb = MultiLabelBinarizer()
    temas_dummies = pd.DataFrame(
        mlb.fit_transform(df['Temas de Interesse']),
        columns=mlb.classes_,
        index=df.index
    )
    
    # One-Hot Encoding para 'Turno'
    ohe = OneHotEncoder(sparse_output=False, handle_unknown='ignore')
    turno_encoded = ohe.fit_transform(df[['Turno']])
    turno_dummies = pd.DataFrame(
        turno_encoded,
        columns=ohe.get_feature_names_out(['Turno']),
        index=df.index
    )
    
    # One-Hot Encoding para 'Disponibilidade'
    disponibilidade_encoded = ohe.fit_transform(df[['Disponibilidade']])
    disponibilidade_dummies = pd.DataFrame(
        disponibilidade_encoded,
        columns=ohe.get_feature_names_out(['Disponibilidade']),
        index=df.index
    )
    
    # Concatena todas as features
    df_features = pd.concat([temas_dummies, turno_dummies, disponibilidade_dummies], axis=1)
    
    # Normalização dos dados
    scaler = StandardScaler()
    df_normalized = scaler.fit_transform(df_features)
    
    # Clusterização com KMeans
    num_clusters = 4
    kmeans = KMeans(n_clusters=num_clusters, random_state=42)
    clusters = kmeans.fit_predict(df_normalized)
    df['cluster'] = clusters
    
    # Encontra o cluster do usuário logado
    user_df = df[df['id'] == request.userId]
    if user_df.empty:
        raise HTTPException(status_code=404, detail="Usuário não encontrado")
    user_cluster = user_df.iloc[0]['cluster']
    
    # Seleciona alunos do mesmo cluster (excluindo o usuário)
    same_cluster = df[(df['cluster'] == user_cluster) & (df['id'] != request.userId)].copy()
    
    # Calcula a compatibilidade: número de temas em comum
    user_temas = set(user_df.iloc[0]['Temas de Interesse'])
    same_cluster["temasComuns"] = same_cluster["Temas de Interesse"].apply(
        lambda temas: len(set(temas).intersection(user_temas))
    )
    
    # Seleciona os 3 alunos com maior compatibilidade
    top_matches = same_cluster.sort_values(by="temasComuns", ascending=False).head(3)
    
    # Renomeia as colunas para ficar compatível com o front-end, incluindo 'userRole'
    result = top_matches.rename(columns={
        'Nome Completo': 'userName',
        'Turno': 'turno',
        'Disponibilidade': 'disponibilidade',
        'Temas de Interesse': 'temasDeInteresse'
    })[['id', 'userName', 'email', 'turno', 'disponibilidade', 'temasDeInteresse', 'userRole']].to_dict(orient="records")
    
    return {"sugestoes": result}

# Endpoint para clusterizar todos os alunos
@app.post("/clustering/all")
def cluster_all(request: MatchingRequest):
    data = [student.dict() for student in request.students]
    df = pd.DataFrame(data)
    
    if df.empty:
        raise HTTPException(status_code=400, detail="Nenhum aluno fornecido")
    
    df.rename(columns={
        'nomeCompleto': 'Nome Completo',
        'turno': 'Turno',
        'disponibilidade': 'Disponibilidade',
        'temasDeInteresse': 'Temas de Interesse'
    }, inplace=True)
    
    mlb = MultiLabelBinarizer()
    temas_dummies = pd.DataFrame(
        mlb.fit_transform(df['Temas de Interesse']),
        columns=mlb.classes_,
        index=df.index
    )
    
    ohe = OneHotEncoder(sparse_output=False, handle_unknown='ignore')
    turno_encoded = ohe.fit_transform(df[['Turno']])
    turno_dummies = pd.DataFrame(
        turno_encoded,
        columns=ohe.get_feature_names_out(['Turno']),
        index=df.index
    )
    
    disponibilidade_encoded = ohe.fit_transform(df[['Disponibilidade']])
    disponibilidade_dummies = pd.DataFrame(
        disponibilidade_encoded,
        columns=ohe.get_feature_names_out(['Disponibilidade']),
        index=df.index
    )
    
    df_features = pd.concat([temas_dummies, turno_dummies, disponibilidade_dummies], axis=1)
    scaler = StandardScaler()
    df_normalized = scaler.fit_transform(df_features)
    
    num_clusters = 4
    kmeans = KMeans(n_clusters=num_clusters, random_state=42)
    clusters = kmeans.fit_predict(df_normalized)
    df['cluster'] = clusters
    
    result = df[['id', 'cluster']].to_dict(orient="records")
    return {"clusters": result}