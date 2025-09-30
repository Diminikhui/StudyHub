import pandas as pd
import numpy as np
from sklearn.datasets import load_wine

data = load_wine()
X = pd.DataFrame(data.data, columns=data.feature_names)
X['target'] = data['target'].astype(np.int64)

X_corr = X.corr()
high_corr = X_corr['target'][X_corr['target'].abs() > 0.5].drop('target').index.tolist()
print("Сильно коррелирующие признаки:", high_corr)

X = X.drop(columns='target')
for f in high_corr:
    X[f+'_2'] = X[f]**2

print(X.describe())
