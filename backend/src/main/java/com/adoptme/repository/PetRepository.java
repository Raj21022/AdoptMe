package com.adoptme.repository;

import com.adoptme.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByListedById(Long userId);
    List<Pet> findByLocationContainingIgnoreCase(String location);
    List<Pet> findByTypeIgnoreCase(String type);
    List<Pet> findByLocationContainingIgnoreCaseAndTypeIgnoreCase(String location, String type);
}
