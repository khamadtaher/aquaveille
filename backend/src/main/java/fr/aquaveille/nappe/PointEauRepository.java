package fr.aquaveille.nappe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PointEauRepository extends JpaRepository<PointEau, String> {

    /** Points d'un département, avec leurs entités hydrogéologiques chargées en une seule requête. */
    @Query("""
            select p from PointEau p left join fetch p.entites
            where p.codeDepartement = :departement
            order by p.commune
            """)
    List<PointEau> findByDepartementAvecEntites(@Param("departement") String codeDepartement);

    @Query("select p from PointEau p left join fetch p.entites order by p.commune")
    List<PointEau> findAllAvecEntites();
}
