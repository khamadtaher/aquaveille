package fr.aquaveille.mesure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MesureRepository extends JpaRepository<Mesure, Long> {

    boolean existsByCodeAnalyse(String codeAnalyse);

    // Le cast évite l'erreur PostgreSQL « could not determine data type of parameter » quand le filtre est absent.
    @Query("""
            select m from Mesure m join fetch m.parametre
            where m.station.code = :station
              and (cast(:parametre as String) is null or m.parametre.code = :parametre)
            order by m.datePrelevement desc
            """)
    List<Mesure> findHistorique(@Param("station") String codeStation,
                                @Param("parametre") String codeParametre);

    @Query("""
            select m from Mesure m join fetch m.station join fetch m.parametre
            where m.horsSeuil = true and m.datePrelevement >= :depuis
            order by m.datePrelevement desc
            """)
    List<Mesure> findDepassementsDepuis(@Param("depuis") LocalDate depuis);

    /** Codes des stations ayant au moins un dépassement depuis la date donnée. */
    @Query("""
            select distinct m.station.code from Mesure m
            where m.horsSeuil = true and m.datePrelevement >= :depuis
            """)
    List<String> findCodesStationsEnAlerte(@Param("depuis") LocalDate depuis);
}
