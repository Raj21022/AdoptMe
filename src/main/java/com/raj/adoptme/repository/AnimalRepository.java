package com.raj.adoptme.repository;

import com.raj.adoptme.entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {
    
    List<Animal> findByAdoptionStatus(Animal.AdoptionStatus status);
    
    List<Animal> findByListerIdOrderByCreatedAtDesc(Long listerId);
    
    List<Animal> findBySpeciesIgnoreCaseOrderByCreatedAtDesc(String species);
    
    List<Animal> findByLocationContainingIgnoreCaseOrderByCreatedAtDesc(String location);
    
    Optional<Animal> findByIdAndAdoptionStatus(Long id, Animal.AdoptionStatus status);
}
