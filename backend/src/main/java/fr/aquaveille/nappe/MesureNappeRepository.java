package fr.aquaveille.nappe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MesureNappeRepository extends JpaRepository<MesureNappe, Long> {

    /** Couples (code paramètre, date) déjà enregistrés pour les points d'un département, pour ignorer les doublons à l'import. */
    @Query("""
            select m.point.bssId, m.parametre.code, m.datePrelevement from MesureNappe m
            where m.point.codeDepartement = :departement
            """)
    List<Object[]> findClesExistantes(@Param("departement") String codeDepartement);

    // Le cast évite l'erreur PostgreSQL « could not determine data type of parameter » quand le filtre est absent.
    @Query("""
            select m from MesureNappe m join fetch m.parametre
            where m.point.bssId = :bssId
              and (cast(:parametre as String) is null or m.parametre.code = :parametre)
            order by m.datePrelevement desc
            """)
    List<MesureNappe> findHistorique(@Param("bssId") String bssId,
                                     @Param("parametre") String codeParametre);

    /** Identifiants des points ayant au moins un dépassement depuis la date donnée. */
    @Query("""
            select distinct m.point.bssId from MesureNappe m
            where m.horsSeuil = true and m.datePrelevement >= :depuis
            """)
    List<String> findPointsEnAlerte(@Param("depuis") LocalDate depuis);
}
