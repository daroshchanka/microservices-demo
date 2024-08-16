package dmax.demo.imagegenerationmanager.repositories;

import dmax.demo.imagegenerationmanager.models.GenerationResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GenerationResultRepository extends JpaRepository<GenerationResult, UUID> {

  List<GenerationResult> findByRelatedProcessId(UUID processId);

}
