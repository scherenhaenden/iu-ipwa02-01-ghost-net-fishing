package de.iu.project.iuipwa0201ghostnetfishing.repository;

import de.iu.project.iuipwa0201ghostnetfishing.model.GhostNet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GhostNetRepository extends JpaRepository<GhostNet, Long> {
    List<GhostNet> findAllByOrderByCreatedAtDesc();
}
