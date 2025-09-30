import pandas as pd
import matplotlib.pyplot as plt
from sklearn.datasets import load_iris
from sklearn.preprocessing import MinMaxScaler
from sklearn.cluster import KMeans
from sklearn.metrics import silhouette_score

data = load_iris()
df = pd.DataFrame(data.data, columns=data.feature_names)
scaler = MinMaxScaler()
X_scaled = scaler.fit_transform(df)

kmeans = KMeans(n_clusters=3, random_state=42, n_init=10)
clusters = kmeans.fit_predict(X_scaled)
df['cluster'] = clusters

plt.figure(figsize=(8,5))
plt.scatter(X_scaled[:,2], X_scaled[:,3], c=clusters, cmap='viridis')
plt.title("Кластеры Iris")
plt.xlabel("Petal length")
plt.ylabel("Petal width")
plt.colorbar(label='Cluster')
plt.show()

silhouette_avg = silhouette_score(X_scaled, clusters)
print(f"Коэффициент силуэта: {silhouette_avg:.3f}")
