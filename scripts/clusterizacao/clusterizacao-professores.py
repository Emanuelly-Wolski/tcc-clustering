import pandas as pd
import matplotlib.pyplot as plt
from sklearn.cluster import KMeans
from sklearn.metrics import silhouette_score
from sklearn.decomposition import PCA
from sklearn.preprocessing import MultiLabelBinarizer, OneHotEncoder, StandardScaler

# ===========================
# 1. Carregamento dos Dados
# ===========================

def carregar_dados(caminho_arquivo):
    return pd.read_excel(caminho_arquivo)

# ====================================
# 2. Definição dos Temas de Interesse
# ====================================

# Temas de Interesse Selecionados
temas_de_interesse = [
    'Tecnologia e inovação', 'Educação', 'Meio ambiente e sustentabilidade',
    'Inteligência artificial', 'Análise de dados', 'Metodologias ágeis',
    'Economia e Finanças', 'Saúde e bem estar', 'Cidadania', 'Política'
]

# ============================
# 3. Processamento das Colunas
# ============================

def processar_colunas(df):
    """Processa as colunas de temas de interesse e disponibilidade."""
    # Convertendo strings em listas
    df['Temas de Interesse'] = df['Temas de Interesse'].str.split(', ')
    
    # Filtrando os temas de interesse para usar apenas as opções especificadas
    df['Temas de Interesse'] = df['Temas de Interesse'].apply(
        lambda temas: [tema for tema in temas if tema in temas_de_interesse]
    )
    
    return df

# ===========================
# 4. Vetorização das Colunas
# ===========================

def vetorizar_colunas(df):
    """Realiza a vetorização das colunas 'Temas de Interesse', 'Turno' e 'Disponibilidade'."""

    mlb = MultiLabelBinarizer()
    temas_dummies = pd.DataFrame(mlb.fit_transform(df['Temas de Interesse']),
                                 columns=mlb.classes_,
                                 index=df.index)

    # One-Hot Encoding para 'Turno' e 'Disponibilidade'
    ohe = OneHotEncoder(sparse_output=False, handle_unknown='ignore')
    
    turno_dummies = pd.DataFrame(ohe.fit_transform(df[['Turno']]),
                                 columns=ohe.get_feature_names_out(['Turno']),
                                 index=df.index)

    disponibilidade_dummies = pd.DataFrame(ohe.fit_transform(df[['Disponibilidade']]),
                                          columns=ohe.get_feature_names_out(['Disponibilidade']),
                                          index=df.index)

    # Concatenando as dummies
    df_dummies = pd.concat([temas_dummies, turno_dummies, disponibilidade_dummies], axis=1)

    return df_dummies

# ===========================
# 5. Normalização dos Dados
# ===========================

def normalizar_dados(df_dummies):
    """Normaliza os dados para que todas as variáveis contribuam igualmente para a clusterização."""
    scaler = StandardScaler()
    df_normalizado = pd.DataFrame(scaler.fit_transform(df_dummies),
                                  columns=df_dummies.columns,
                                  index=df_dummies.index)
    return df_normalizado, scaler

# ================================
# 6. Redução de Dimensionalidade
# ================================

def reduzir_dimensionalidade(df_normalizado, n_componentes=2):
    """Reduz a dimensionalidade dos dados usando PCA."""
    pca = PCA(n_components=n_componentes, random_state=42)
    df_reduzido = pca.fit_transform(df_normalizado)
    return df_reduzido, pca

# ======================================
# 7. Encontrar Número Ideal de Clusters
# ======================================

def encontrar_numero_ideal_clusters(df_features, k_max=10):
    """Encontra o número ideal de clusters usando o Silhouette Score."""
    scores = []
    for k in range(2, k_max+1):
        clusters, _ = realizar_clusterizacao(df_features, num_clusters=k)
        score = calcular_silhouette(df_features, clusters)
        scores.append((k, score))
        print(f'Número de Clusters: {k}, Silhouette Score: {score:.4f}')
    # Plotando o Silhouette Score para cada valor de k
    ks, silhouettes = zip(*scores)
    plt.figure(figsize=(8, 5))
    plt.plot(ks, silhouettes, 'o-')
    plt.xlabel('Número de Clusters')
    plt.ylabel('Silhouette Score')
    plt.title('Silhouette Score por Número de Clusters')
    plt.xticks(ks)
    plt.grid(True)
    plt.show()

# =================
# 8. Clusterização
# =================

def realizar_clusterizacao(df_features, num_clusters=3):
    """Aplica o algoritmo KMeans para clusterização."""
    kmeans = KMeans(n_clusters=num_clusters, random_state=42)
    clusters = kmeans.fit_predict(df_features)
    return clusters, kmeans

# ===========================
# 9. Avaliação do Modelo
# ===========================

def calcular_silhouette(df_features, clusters):
    """Calcula o coeficiente de Silhouette para avaliar a clusterização."""
    score = silhouette_score(df_features, clusters)
    return score

# =============================
# 10. Visualização dos Clusters
# =============================

def plotar_clusters(df_features, clusters):
    plt.figure(figsize=(10, 6))
    scatter = plt.scatter(df_features[:, 0], df_features[:, 1], c=clusters, cmap='viridis', marker='o', alpha=0.6)
    plt.title('Separação dos Clusters de Professores')
    plt.xlabel('Componente Principal 1')
    plt.ylabel('Componente Principal 2')
    plt.colorbar(scatter, label='Cluster')
    plt.grid(True)
    plt.show()

# =====================================
# 11. Encontrar Professores Compatíveis
# =====================================

def exibir_professores_compativeis(df, aluno_index, top_n=3):
    """Exibe os professores mais compatíveis com base na quantidade de temas em comum."""
    aluno_busca = df.loc[aluno_index]

    print(f"\n=== Informações do Aluno Analisado ===")
    print(f"Nome: {aluno_busca['Nome Completo']}")
    print(f"Email: {aluno_busca['Email']}")
    print(f"Turno: {aluno_busca['Turno']}")
    print(f"Disponibilidade: {aluno_busca['Disponibilidade']}")
    print(f"Temas de Interesse: {', '.join(aluno_busca['Temas de Interesse'])}")

    print("\n=== Professores Mais Compatíveis ===")

    # Filtra professores do mesmo cluster
    professores_compativeis = df[(df['Cluster'] == aluno_busca['Cluster']) & (df.index != aluno_index)].copy()

    # Calcula temas em comum
    temas_aluno_busca = set(aluno_busca['Temas de Interesse'])
    professores_compativeis['Temas Comuns'] = professores_compativeis['Temas de Interesse'].apply(
        lambda temas: len(temas_aluno_busca.intersection(set(temas)))
    )

    # Ordena por quantidade de temas em comum e exibe os top 3 professores
    top_professores = professores_compativeis.sort_values(by='Temas Comuns', ascending=False).head(top_n)

    for _, professor in top_professores.iterrows():
        print(f"\nNome: {professor['Nome Completo']}")
        print(f"Email: {professor['Email']}")
        print(f"Turno: {professor['Turno']}")
        print(f"Disponibilidade: {professor['Disponibilidade']}")
        print(f"Temas de Interesse: {', '.join(professor['Temas de Interesse'])}")
        print(f"Temas em comum: {professor['Temas Comuns']}")

# ===========================
# 12. Função Principal
# ===========================

def main():
    caminho_arquivo = '../../dados/professores.xlsx'

    # Carregamento e processamento dos dados
    df_professores = carregar_dados(caminho_arquivo)
    df_professores = processar_colunas(df_professores)
    df_dummies = vetorizar_colunas(df_professores)

    # Normalização dos dados
    df_normalizado, scaler = normalizar_dados(df_dummies)

    # Redução de dimensionalidade
    df_reduzido, pca_model = reduzir_dimensionalidade(df_normalizado, n_componentes=2)

    # Encontra o número ideal de clusters
    encontrar_numero_ideal_clusters(df_reduzido, k_max=10)

    num_clusters = 5  

    # Realização da clusterização
    clusters, kmeans_model = realizar_clusterizacao(df_reduzido, num_clusters)
    df_professores['Cluster'] = clusters

    # Avaliação do modelo
    silhouette_avg = calcular_silhouette(df_reduzido, clusters)
    print(f'\nSilhouette Score para Professores: {silhouette_avg:.2f}')

    # Visualização
    plotar_clusters(df_reduzido, clusters)

    # índice do aluno a ser analisado
    aluno_index = 5  

    # Exibir os professores mais compatíveis
    exibir_professores_compativeis(df_professores, aluno_index)

if __name__ == '__main__':
    main()
