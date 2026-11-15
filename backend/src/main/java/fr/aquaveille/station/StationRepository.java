package fr.aquaveille.station;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, String> {

    List<Station> findByCodeDepartementOrderByLibelle(String codeDepartement);
}
