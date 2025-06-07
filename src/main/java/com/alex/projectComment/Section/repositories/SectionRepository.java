package com.alex.projectComment.Section.repositories;

import com.alex.projectComment.Section.entities.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
  @Query("SELECT s FROM Section s WHERE s.name = :name AND s.status != 'DELETED'")
  Optional<Section> findByNameWithStatusNotDeleted(String name);

  @Query("SELECT s FROM Section s WHERE s.id = :id AND s.status != 'DELETED'")
  Optional<Section> findByIdWithStatusNotDeleted(Long id);

}
