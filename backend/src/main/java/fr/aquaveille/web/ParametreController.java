package fr.aquaveille.web;

import fr.aquaveille.parametre.ParametreRepository;
import fr.aquaveille.web.Dtos.ParametreDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/parametres")
public class ParametreController {

    private final ParametreRepository parametres;

    public ParametreController(ParametreRepository parametres) {
        this.parametres = parametres;
    }

    @GetMapping
    public List<ParametreDto> lister() {
        return parametres.findAll().stream().map(ParametreDto::of).toList();
    }
}
