-- Paramètres physico-chimiques suivis (codes SANDRE) et seuils d'alerte.
-- Seuils indicatifs, inspirés des limites de "bon état" des cours d'eau
-- et de la norme de 50 mg/L pour les nitrates. À ajuster selon l'usage.
INSERT INTO parametre (code, libelle, unite, seuil_min, seuil_max) VALUES
    ('1301', 'Température de l''eau', '°C',          NULL, 25.0),
    ('1302', 'pH',                    'unité pH',    6.0,  9.0),
    ('1311', 'Oxygène dissous',       'mg(O2)/L',    6.0,  NULL),
    ('1335', 'Ammonium',              'mg(NH4)/L',   NULL, 0.5),
    ('1340', 'Nitrates',              'mg(NO3)/L',   NULL, 50.0),
    ('1433', 'Orthophosphates',       'mg(PO4)/L',   NULL, 0.5);
