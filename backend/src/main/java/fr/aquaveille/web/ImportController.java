package fr.aquaveille.web;

import fr.aquaveille.importation.ImportService;
import fr.aquaveille.importation.RapportImport;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Déclenche manuellement l'import d'un département (ex. POST /api/import/91). */
@RestController
@RequestMapping("/api/import")
public class ImportController {

    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @PostMapping("/{departement}")
    public RapportImport importer(
            @PathVariable @Pattern(regexp = "\\d{2}|2[AB]|97\\d", message = "Code département invalide")
            String departement) {
        return importService.importerDepartement(departement);
    }
}
