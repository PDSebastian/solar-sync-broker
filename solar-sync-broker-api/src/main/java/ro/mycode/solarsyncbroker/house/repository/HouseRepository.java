package ro.mycode.solarsyncbroker.house.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.mycode.solarsyncbroker.house.model.House;

@Repository
public interface HouseRepository extends JpaRepository<House, Long> {
    boolean existsByName(String name);
    boolean existsByIdAndOwner_Email(Long id, String email);
}