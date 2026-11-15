package fr.aquaveille.importation;

/** Bilan d'un import, renvoyé par l'API et écrit dans les logs. */
public record RapportImport(String departement, int stations, int nouvellesMesures, int depassements) {
}
