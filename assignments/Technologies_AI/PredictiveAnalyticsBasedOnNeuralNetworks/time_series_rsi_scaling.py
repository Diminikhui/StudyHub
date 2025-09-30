import pandas as pd
import numpy as np
from sklearn.preprocessing import MinMaxScaler

def calculate_rsi(prices, window=14):
    delta = prices.diff()
    gain = (delta.where(delta > 0, 0)).rolling(window).mean()
    loss = (-delta.where(delta < 0, 0)).rolling(window).mean()
    rs = gain / loss
    return 100 - (100 / (1 + rs))

df = pd.read_csv('weather_time_series.csv', parse_dates=['date'])
df['temperature'] = df['temperature_scaled']
df['RSI'] = calculate_rsi(df['temperature'])
df = df.dropna()

scaler = MinMaxScaler()
scaled = scaler.fit_transform(df[['temperature', 'RSI']])
scaled_df = pd.DataFrame(scaled, columns=['temperature', 'RSI'])
scaled_df['date'] = df['date'].values

print(scaled_df.head())
