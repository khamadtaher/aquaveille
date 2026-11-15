# AquaVeille

Application web de suivi de la qualité des cours d'eau et des eaux souterraines en France, à partir des données publiques [Hub'Eau](https://hubeau.eaufrance.fr) (API « Qualité des cours d'eau », données Naïades, et API « Qualité des nappes d'eau souterraine », données ADES).

L'application importe les stations de mesure et les analyses physico-chimiques d'un département, détecte les dépassements de seuils (nitrates, oxygène dissous, pH…) et les affiche sur une carte interactive.

> Projet personnel réalisé pour approfondir le développement full-stack Angular / Java Spring Boot, en lien avec ma formation en hydrogéologie.

**Démo en ligne :** https://khamadtaher.github.io/aquaveille/

> L'API est hébergée sur une offre gratuite qui se met en veille : le premier chargement peut prendre jusqu'à une minute.

## Fonctionnalités

- **Carte des stations** (Leaflet) d'un département, avec les stations en alerte mises en évidence.
- **Import à la demande** depuis Hub'Eau, plus un import automatique chaque nuit.
- **Fiche station** : dernière valeur de chaque paramètre suivi, seuils, historique filtrable.
- **Liste des alertes** : tous les dépassements des 12 derniers mois.
- **Eaux souterraines** : carte des forages, puits et sources (données ADES), avec pour chaque point la nature de l'ouvrage, sa profondeur, le mode de gisement, la masse d'eau et les **entités hydrogéologiques BDLISA** rattachées.
- **Nappe captée** : un point rattaché à une seule entité BDLISA est signalé « nappe unique » ; un forage multicouche est signalé comme tel, Hub'Eau ne permettant pas de savoir de quelle nappe provient chaque prélèvement (la coupe technique est à consulter dans la Banque du sous-sol du BRGM).
- **Seuils configurables** en base de données (table `parametre`), distincts pour les cours d'eau et les nappes.

## Architecture

```
Angular (carte, fiches, alertes)
        │  HTTP / JSON
        ▼
Spring Boot (API REST, import planifié) ──► API Hub'Eau
        │  JPA / Hibernate
        ▼
PostgreSQL (stations, paramètres, mesures) — schéma géré par Flyway
```

| Couche | Technologies |
| --- | --- |
| Front-end | Angular 21 (composants standalone, signals), TypeScript, RxJS, Leaflet, SCSS |
| Back-end | Java 21, Spring Boot 3.5, Spring Web (RestClient), Spring Data JPA, Bean Validation, planification `@Scheduled` |
| Base de données | PostgreSQL 17, migrations Flyway |
| Tests | JUnit 5, Mockito, MockMvc, H2 (back) · Vitest (front) |
| Déploiement | Docker, Docker Compose, nginx |

## API REST

| Méthode | URL | Description |
| --- | --- | --- |
| GET | `/api/stations?departement=91` | Stations d'un département, avec indicateur d'alerte |
| GET | `/api/stations/{code}` | Détail d'une station |
| GET | `/api/stations/{code}/mesures?parametre=1340` | Historique des mesures (filtre optionnel) |
| GET | `/api/parametres` | Paramètres suivis et seuils |
| GET | `/api/alertes?jours=365` | Dépassements de seuil récents |
| POST | `/api/import/{departement}` | Lance l'import d'un département depuis Hub'Eau |
| GET | `/api/nappes/points?departement=91` | Points d'eau souterraine, avec nappes BDLISA et indicateur d'alerte |
| GET | `/api/nappes/points/{bssId}` | Détail d'un point d'eau |
| GET | `/api/nappes/points/{bssId}/mesures?parametre=1340` | Historique des mesures d'un point d'eau |
| POST | `/api/nappes/import/{departement}` | Lance l'import des eaux souterraines d'un département |

## Paramètres suivis

| Code SANDRE | Paramètre | Seuil d'alerte |
| --- | --- | --- |
| 1301 | Température de l'eau | > 25 °C |
| 1302 | pH | < 6 ou > 9 |
| 1311 | Oxygène dissous | < 6 mg/L |
| 1335 | Ammonium | > 0,5 mg/L |
| 1340 | Nitrates | > 50 mg/L |
| 1433 | Orthophosphates | > 0,5 mg/L |

Pour les eaux souterraines, seuls les nitrates (> 50 mg/L) et l'ammonium (> 0,5 mg/L) déclenchent une alerte (colonnes `seuil_min_nappe` / `seuil_max_nappe`, migration `V3__eaux_souterraines.sql`).

Seuils indicatifs, modifiables dans `backend/src/main/resources/db/migration/V2__parametres_suivis.sql` ou directement en base.

## Lancer le projet

### Option 1 : tout avec Docker

```bash
docker compose up --build
```

- Application : http://localhost:4200
- API : http://localhost:8080/api/stations

Sur la carte, saisissez un département (ex. `91`) puis cliquez sur **Importer depuis Hub'Eau**.

### Option 2 : en développement

Prérequis : Java 21, Maven 3.9, Node.js 24 (ou 22.22.3 minimum), PostgreSQL 17.

```bash
# 1. Base de données (ou un PostgreSQL local avec la base "aquaveille")
docker compose up -d postgres

# 2. Back-end
cd backend
mvn spring-boot:run

# 3. Front-end (dans un autre terminal)
cd frontend
npm install
npm start
```

Le front tourne sur http://localhost:4200 et redirige `/api` vers le back-end (`proxy.conf.json`).

### Déploiement de la démo

| Élément | Hébergement |
| --- | --- |
| Front Angular | GitHub Pages, publié par GitHub Actions (`.github/workflows/deploy-frontend.yml`) |
| API Spring Boot | Render, image Docker (`render.yaml`) |
| Base PostgreSQL | Aiven (offre gratuite) |

Le front de la démo appelle l'API définie dans `frontend/src/environments/environment.github-pages.ts`.

### Tests

```bash
cd backend && mvn test
cd frontend && npm test -- --watch=false
```

## Pistes d'évolution

- Graphique d'évolution d'un paramètre dans le temps (Chart.js ou ngx-charts)
- Gestion d'état avec NgRx
- Tests end-to-end avec Cypress
- Authentification (Spring Security + JWT) pour protéger l'import
- Tests automatiques dans GitHub Actions
- Données d'eau potable (API Hub'Eau « Qualité de l'eau potable »)

## Source des données

Données publiques issues de [Hub'Eau](https://hubeau.eaufrance.fr) (OFB / BRGM), API « Qualité des cours d'eau ». Fond de carte © contributeurs OpenStreetMap.
