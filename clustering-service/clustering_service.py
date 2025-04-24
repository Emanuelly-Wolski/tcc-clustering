from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List
import pandas as pd
from sklearn.cluster import KMeans
from sklearn.preprocessing import MultiLabelBinarizer, OneHotEncoder, StandardScaler

app = FastAPI()

class Student(BaseModel):
    id: int
    nomeCompleto: str
    email: str
    turno: str = None              # alunos
    shift: str = None             # professores
    disponibilidade: str = None   # alunos
    availability: str = None      # professores
    temasDeInteresse: List[str] = None
    interestTopics: List[str] = None
    userRole: str = None

class MatchingRequest(BaseModel):
    userId: int
    students: List[Student]

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

    df.rename(columns={"nomeCompleto": "Nome Completo"}, inplace=True)
    df["Temas de Interesse"] = df["Temas de Interesse"].apply(lambda x: x if isinstance(x, list) else [])
    return df

@app.post("/clustering")
def get_matching(request: MatchingRequest):
    df = pd.DataFrame([s.dict() for s in request.students])
    if df.empty:
        raise HTTPException(status_code=400, detail="Nenhum dado fornecido")

    df = padronizar_campos(df)

    mlb = MultiLabelBinarizer()
    temas_bin = mlb.fit_transform(df["Temas de Interesse"])
    ohe = OneHotEncoder(sparse_output=False, handle_unknown='ignore')
    turno_disp = ohe.fit_transform(df[["Turno", "Disponibilidade"]].fillna(""))

    X = pd.concat([
        pd.DataFrame(temas_bin),
        pd.DataFrame(turno_disp)
    ], axis=1)

    X_scaled = StandardScaler().fit_transform(X)
    kmeans = KMeans(n_clusters=4, random_state=42, n_init=10)
    df["cluster"] = kmeans.fit_predict(X_scaled)

    cluster_usuario = df[df["id"] == request.userId]["cluster"]
    if cluster_usuario.empty:
        raise HTTPException(status_code=404, detail="Usuário não encontrado.")

    cluster_id = cluster_usuario.values[0]
    sugestoes = df[(df["cluster"] == cluster_id) & (df["id"] != request.userId)].copy()

    # Calcula temas em comum para ordenação por relevância
    temas_usuario = set(df[df["id"] == request.userId].iloc[0]["Temas de Interesse"])
    sugestoes["temasComuns"] = sugestoes["Temas de Interesse"].apply(
        lambda temas: len(set(temas).intersection(temas_usuario))
    )

    sugestoes = sugestoes.sort_values(by="temasComuns", ascending=False).head(3)

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

@app.post("/clustering/all")
def cluster_all(request: MatchingRequest):
    df = pd.DataFrame([s.dict() for s in request.students])
    if df.empty:
        raise HTTPException(status_code=400, detail="Nenhum dado fornecido")

    df = padronizar_campos(df)

    mlb = MultiLabelBinarizer()
    temas_bin = mlb.fit_transform(df["Temas de Interesse"])
    ohe = OneHotEncoder(sparse_output=False, handle_unknown='ignore')
    turno_disp = ohe.fit_transform(df[["Turno", "Disponibilidade"]].fillna(""))

    X = pd.concat([
        pd.DataFrame(temas_bin),
        pd.DataFrame(turno_disp)
    ], axis=1)

    X_scaled = StandardScaler().fit_transform(X)
    kmeans = KMeans(n_clusters=4, random_state=42, n_init=10)
    df["cluster"] = kmeans.fit_predict(X_scaled)

    return {
        "clusters": df[["id", "cluster"]].to_dict(orient="records")
    }
