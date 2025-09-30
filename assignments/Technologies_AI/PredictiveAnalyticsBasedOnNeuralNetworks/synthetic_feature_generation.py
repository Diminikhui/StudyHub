import pandas as pd
import numpy as np

np.random.seed(42)

# Предполагается, что scaled_df уже есть
# Если запускать отдельно, нужно сначала выполнить weather_rsi_scaling.py

def generate_synthetic_features(scaled_df):
    n_days = len(scaled_df)
    date_range = scaled_df['date']

    base_pressure = 1013 + 5 * np.cos(np.linspace(0, 3 * np.pi, n_days))
    base_precip = np.abs(10 * np.sin(np.linspace(0, 3 * np.pi, n_days)))

    synthetic_data = {
        'date': date_range,
        'humidity': 60 + 20 * np.sin(np.linspace(0, 4 * np.pi, n_days)) + np.random.normal(0, 5, n_days),
        'pressure': base_pressure + np.random.normal(0, 1, n_days),
        'precipitation': base_precip + np.random.normal(0, 2, n_days),
        'is_weekend': [(date.weekday() >= 5) * 1 for date in date_range],
        'storm_alert': np.random.binomial(1, 0.1, n_days)
    }

    extra_df = pd.DataFrame(synthetic_data)
    full_df = pd.merge(scaled_df, extra_df, on='date')
    return full_df

# Пример использования:
# full_df = generate_synthetic_features(scaled_df)
# print(full_df.head())
