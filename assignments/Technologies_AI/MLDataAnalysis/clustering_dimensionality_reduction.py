import pandas as pd
import matplotlib.pyplot as plt
from sklearn.datasets import fetch_california_housing
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.manifold import TSNE
from sklearn.cluster import KMeans

data = fetch_california_housing()
X = pd.DataFrame(data.data, columns=data.feature_names)
y = pd.DataFrame(data.target, columns=['price'])
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
scaler = StandardScaler()
X_train_scaled = scaler.fit_transform(X_train)
X_test_scaled = scaler.transform(X_test)

tsne = TSNE(n_components=2, learning_rate=250, random_state=42)
X_train_tsne = tsne.fit_transform(X_train_scaled)

plt.figure(figsize=(10,6))
plt.scatter(X_train_tsne[:,0], X_train_tsne[:,1])
plt.title("t-SNE обучающая выборка")
plt.show()

kmeans = KMeans(n_clusters=3, max_iter=100, random_state=42)
train_clusters = kmeans.fit_predict(X_train_scaled)

plt.figure(figsize=(10,6))
plt.scatter(X_train_tsne[:,0], X_train_tsne[:,1], c=train_clusters, cmap='viridis')
plt.title("KMeans кластеры на обучающей выборке")
plt.show()
