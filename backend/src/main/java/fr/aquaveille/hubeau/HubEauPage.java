package fr.aquaveille.hubeau;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** Enveloppe paginée renvoyée par toutes les opérations Hub'Eau. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record HubEauPage<T>(long count, String next, List<T> data) {
}
