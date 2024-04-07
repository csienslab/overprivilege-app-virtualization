import pandas as pd

df = pd.read_csv("./res_add_request_duplicated.csv")
pkgs = set(df['Package Name'])
print(pkgs)

#df_dup = pd.concat(g for _, g in df.groupby('Package Name') if len(g) > 1)
#print(df_dup)

for i in pkgs:
    keys = set(df[df['Package Name'].str.contains(i)]['public_key_v2'])
    if len(keys) > 1:
        print(i)