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
