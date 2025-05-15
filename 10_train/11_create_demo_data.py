# === generate_projectAB.py ===
import numpy as np, pandas as pd
from sklearn.model_selection import StratifiedKFold
from sklearn.linear_model import LogisticRegression
from sklearn.preprocessing import StandardScaler, OneHotEncoder
from sklearn.compose import ColumnTransformer
from sklearn.pipeline import Pipeline

np.random.seed(42)
n = 3000
df = pd.DataFrame({
    "customer_id": np.arange(1, n + 1),
    "utilization_ratio": np.round(np.random.beta(2, 5, n) * 100, 2),
    "current_balance": np.random.gamma(2.5, 1200, n).round(),
    "days_since_last_decrease": np.random.choice(list(range(0, 400))+[9999],
                                                size=n, p=[0.002]*400+[0.2]),
    "age": np.random.randint(20, 75, n),
    "region_code": np.random.choice(["Kanto","Kansai","Tokai","Other"], n,
                                    p=[0.4,0.25,0.15,0.2]),
})
df["bureau_flag"] = np.random.choice([1,0], n, p=[0.4,0.6])
df["bureau_score"]       = np.where(df.bureau_flag==1, np.random.normal(650,50,n).round(), np.nan)
df["bureau_delinq_cnt"]  = np.where(df.bureau_flag==1, np.random.poisson(0.3,n), np.nan)
df["bureau_open_accts"]  = np.where(df.bureau_flag==1, np.random.poisson(4,n), np.nan)

base_prob = 0.03 + 0.002*(df.utilization_ratio/10) - 0.01*(df.bureau_flag==1)
df["bad_flag"] = np.random.binomial(1, base_prob.clip(0.01,0.25))

internal = ["utilization_ratio","current_balance","days_since_last_decrease","age","region_code"]
X, y = df[internal], df.bad_flag
prep = ColumnTransformer([
    ("num", StandardScaler(), ["utilization_ratio","current_balance",
                               "days_since_last_decrease","age"]),
    ("cat", OneHotEncoder(handle_unknown="ignore"), ["region_code"])
])
pipe = Pipeline([("prep",prep),
                 ("clf",LogisticRegression(max_iter=1000,solver='liblinear'))])

oof = np.zeros(n)
for tr,te in StratifiedKFold(5,shuffle=True,random_state=42).split(X,y):
    pipe.fit(X.iloc[tr], y.iloc[tr])
    oof[te] = pipe.predict_proba(X.iloc[te])[:,1]
df["pred_A"] = oof

df[internal+["bad_flag"]].to_csv("projectA_train.csv", index=False)
df.loc[df.bureau_flag==1,
       internal+["pred_A","bureau_score","bureau_delinq_cnt","bureau_open_accts","bad_flag"]
      ].to_csv("projectB_train.csv", index=False)
print("CSV files written.")