package fr.aquaveille.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

/** Transforme les exceptions en réponses JSON standard (RFC 9457). */
@RestControllerAdvice
public class GestionErreurs {

    @ExceptionHandler(RessourceIntrouvableException.class)
    ProblemDetail introuvable(RessourceIntrouvableException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(RestClientException.class)
    ProblemDetail hubEauIndisponible(RestClientException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY,
                "L'API Hub'Eau n'a pas répondu correctement : " + e.getMessage());
    }
}
