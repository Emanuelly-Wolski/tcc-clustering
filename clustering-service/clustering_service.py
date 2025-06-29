from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List
import pandas as pd
from sklearn.cluster import KMeans
from sklearn.preprocessing import MultiLabelBinarizer, OneHotEncoder, StandardScaler

# Transforma o código Python em uma API REST o tornando acessível a outras partes do sistema
# Executar o comando para iniciar o serviço python: uvicorn clustering_service:app --reload --port 8001
# uvicorn é o servidor que executa o FastAPI.
app = FastAPI()

# Define o modelo dos dados recebidos de cada aluno ou professor, considerando apenas os campos utilizados na clusterização e identificadores
class Student(BaseModel):
    id: int
    nomeCompleto: str
    email: str
    turno: str = None             # alunos
    shift: str = None             # professores
    disponibilidade: str = None   # alunos
    availability: str = None      # professores
    temasDeInteresse: List[str] = None
    interestTopics: List[str] = None
    userRole: str = None

# Define o modelo da requisição completa enviada ao serviço de clusterização
# Contém o id do usuário logado (que vai buscar sugestões) e a lista de perfis para comparar
class MatchingRequest(BaseModel):
    userId: int
    students: List[Student]

# Função que padroniza os campos do DataFrame, converte campos equivalentes para uma nomenclatura padronizada
def padronizar_campos(df: pd.DataFrame):
    if "shift" in df.columns:
        df["Turno"] = df["shift"]
    if "turno" in df.columns:
        df["Turno"] = df["turno"]
    if "availability" in df.columns:
        df["Disponibilidade"] = df["availability"]
    if "disponibilidade" in df.columns:
        df["Disponibilidade"] = df["disponibilidade"]
    if "interestTopics" in df.columns:
        df["Temas de Interesse"] = df["interestTopics"]
    if "temasDeInteresse" in df.columns:
        df["Temas de Interesse"] = df["temasDeInteresse"]

    df.rename(columns={"nomeCompleto": "Nome Completo"}, inplace=True) #renomeia a coluna de nome

    # Garante que todos os valores de "Temas de Interesse" sejam listas
    df["Temas de Interesse"] = df["Temas de Interesse"].apply(lambda x: x if isinstance(x, list) else [])
    return df

# Endpoint que retorna as 3 sugestões mais compatíveis para o userId fornecido
@app.post("/clustering")
def get_matching(request: MatchingRequest):

    # Converte os dados recebidos para um DataFrame, esse dataframe guarda os dados "originais"
    df = pd.DataFrame([s.dict() for s in request.students])
    if df.empty:
        raise HTTPException(status_code=400, detail="Nenhum dado fornecido")

    # Padroniza os nomes dos campos
    df = padronizar_campos(df)

    # Codifica os temas de interesse em vetores binários, usado esse método porque ele aceita listas com múltiplos valores
    mlb = MultiLabelBinarizer()
    temas_bin = mlb.fit_transform(df["Temas de Interesse"])

    # Codifica turno e disponibilidade, utilizado nos campos com única escolha, cada valor vira uma coluna separada
    ohe = OneHotEncoder(sparse_output=False, handle_unknown='ignore')
    turno_disp = ohe.fit_transform(df[["Turno", "Disponibilidade"]].fillna(""))

    # Junta todos os dados codificados, vira uma matriz numérica (um array bidimensional) onde:
    # cada linha representa um usuário e cada coluna representa uma característica convertida em número
    X = pd.concat([
        pd.DataFrame(temas_bin),
        pd.DataFrame(turno_disp)
    ], axis=1)

    # Padroniza os dados em uma mesma escala para aplicar o KMeans, contém os dados numéricos 
    # (temas binarizados + turno/disponibilidade codificados)
    X_scaled = StandardScaler().fit_transform(X)

    # ------------- APLICAÇÃO K-MEANS ------------------
    # Instancia o KMeans para formar 4 clusters
    # O random_state garante resultados reprodutíveis
    # O n_init=10 significa que o algoritmo será executado 10 vezes com diferentes pontos iniciais, e a melhor solução será escolhida
    kmeans = KMeans(n_clusters=4, random_state=42, n_init=10)

    # Método fit_predict:
    # 1. "fit": analisa os dados e encontra os grupos automaticamente
    # 2. "predict": atribui a cada linha do DataFrame um número de cluster (0, 1, 2, 3)
    # Esse número é salvo na nova coluna 'cluster' no DataFrame original (df), que ainda contém os dados em texto
    # A partir daqui, vamos usar a coluna 'cluster' para filtrar perfis compatíveis com o usuário logado
    df["cluster"] = kmeans.fit_predict(X_scaled)

    # Filtra o dataframe para verificar qual cluster o usuário logado pertence
    cluster_usuario = df[df["id"] == request.userId]["cluster"]
    if cluster_usuario.empty:
        raise HTTPException(status_code=404, detail="Usuário não encontrado.")

    cluster_id = cluster_usuario.values[0]

    # Dataframe que contém apenas os usuários do mesmo cluster do usuário logado, exceto o próprio
    sugestoes = df[(df["cluster"] == cluster_id) & (df["id"] != request.userId)].copy()

    # Pegamos os temas de interesse do usuário logado e transformamos em um "set" que é um conjunto que vamos usar para comparar com os 
    # conjuntos de sugestões de outros usuários
    temas_usuario = set(df[df["id"] == request.userId].iloc[0]["Temas de Interesse"])

    # Para cada sugestão, conta quantos temas estão em comum com o usuário logado e salva esse número na nova coluna 'temasComuns'.
    sugestoes["temasComuns"] = sugestoes["Temas de Interesse"].apply( 
        lambda temas: len(set(temas).intersection(temas_usuario))
    )

    # Ordena as sugestões por temas em comum (decrescente) e pega os 3 com mais temas em comum
    sugestoes = sugestoes.sort_values(by="temasComuns", ascending=False).head(3)

    # Retorna as sugestões com os dados necessários
    return {
        "sugestoes": [
            {
                "id": row["id"],
                "userName": row["Nome Completo"],
                "email": row["email"],
                "turno": row.get("Turno"),
                "disponibilidade": row.get("Disponibilidade"),
                "temasDeInteresse": row.get("Temas de Interesse", []),
                "userRole": row.get("userRole")
            }
            for _, row in sugestoes.iterrows()
        ]
    }

# Endpoint que apenas agrupa todos os perfis e retorna o cluster de cada um
@app.post("/clustering/all")
def cluster_all(request: MatchingRequest):

    # Converte os dados para DataFrame
    df = pd.DataFrame([s.dict() for s in request.students])
    if df.empty:
        raise HTTPException(status_code=400, detail="Nenhum dado fornecido")

    # Padroniza os nomes dos campos
    df = padronizar_campos(df)

    # Codifica os temas de interesse
    mlb = MultiLabelBinarizer()
    temas_bin = mlb.fit_transform(df["Temas de Interesse"])

    # Codifica turno e disponibilidade
    ohe = OneHotEncoder(sparse_output=False, handle_unknown='ignore')
    turno_disp = ohe.fit_transform(df[["Turno", "Disponibilidade"]].fillna(""))

    # Junta todos os dados
    X = pd.concat([
        pd.DataFrame(temas_bin),
        pd.DataFrame(turno_disp)
    ], axis=1)

     # Normaliza e aplica o KMeans
    X_scaled = StandardScaler().fit_transform(X)
    kmeans = KMeans(n_clusters=4, random_state=42, n_init=10)
    df["cluster"] = kmeans.fit_predict(X_scaled)

    # Retorna o id e o cluster de cada usuário
    return {
        "clusters": df[["id", "cluster"]].to_dict(orient="records")
    }