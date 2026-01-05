package org.example.assignment.repository;

import org.example.assignment.domain.Extension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExtensionRepository extends JpaRepository<Extension, Long> {

    boolean existsByName(String name);

    Optional<Extension> findByName(String name);

    List<Extension> findByFixedTrueOrderByNameAsc();

    List<Extension> findByFixedFalseOrderByCreatedAtDesc();

    long countByFixedFalse();
}
