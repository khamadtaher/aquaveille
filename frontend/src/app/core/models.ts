export interface Station {
  code: string;
  libelle: string;
  commune: string | null;
  codeDepartement: string | null;
  coursEau: string | null;
  latitude: number;
  longitude: number;
  enAlerte: boolean;
}

export interface Mesure {
  date: string;
  codeParametre: string;
  parametre: string;
  unite: string;
  valeur: number;
  horsSeuil: boolean;
}

export interface Parametre {
  code: string;
  libelle: string;
  unite: string;
  seuilMin: number | null;
  seuilMax: number | null;
  seuilMinNappe: number | null;
  seuilMaxNappe: number | null;
}

export interface Alerte {
  codeStation: string;
  station: string;
  commune: string | null;
  date: string;
  parametre: string;
  valeur: number;
  unite: string;
  seuilMin: number | null;
  seuilMax: number | null;
}

export interface RapportImport {
  departement: string;
  stations: number;
  nouvellesMesures: number;
  depassements: number;
}

/** Entité hydrogéologique BDLISA (nappe) rattachée à un point d'eau. */
export interface EntiteHydrogeologique {
  code: string;
  nom: string | null;
}

/** Point d'eau souterraine (forage, puits, source) issu d'ADES via Hub'Eau. */
export interface PointEau {
  bssId: string;
  codeBss: string | null;
  libelle: string | null;
  commune: string | null;
  codeDepartement: string | null;
  latitude: number;
  longitude: number;
  nature: string | null;
  profondeur: number | null;
  modeGisement: string | null;
  caracteristiqueAquifere: string | null;
  massesEau: string[];
  entites: EntiteHydrogeologique[];
  nappeUnique: boolean;
  urnBss: string | null;
  enAlerte: boolean;
}
