package dmax.demo.imagegenerationmanager.services;

import dmax.demo.imagegenerationmanager.models.GenerationResult;
import dmax.demo.imagegenerationmanager.repositories.GenerationResultRepository;
import io.micrometer.observation.annotation.Observed;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Observed
@Service
public class GenerationResultService {

  @Autowired
  private GenerationResultRepository generationResultRepository;

  public List<GenerationResult> getList(@NotNull UUID processId) {
    List<GenerationResult> results = new ArrayList<>();
    if (processId == null) {
      results.addAll(generationResultRepository.findAll());
    } else {
      results.addAll(generationResultRepository.findByRelatedProcessId(processId));
    }
    return results;
  }

  public GenerationResult saveResult(@NotNull GenerationResult result) {
    return generationResultRepository.save(result);
  }

  public Optional<GenerationResult> getById(@NotNull UUID id) {
    return generationResultRepository.findById(id);
  }

}
