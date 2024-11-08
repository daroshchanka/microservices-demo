package dmax.demo.imagegenerationmanager.repositories;

import dmax.demo.imagegenerationmanager.models.GenerateImageProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GenerateImageProcessRepository extends JpaRepository<GenerateImageProcess, Long> {

  Optional<GenerateImageProcess> findByProcessId(UUID processId);

  List<GenerateImageProcess> findByStatus(GenerateImageProcess.Status status);

}
