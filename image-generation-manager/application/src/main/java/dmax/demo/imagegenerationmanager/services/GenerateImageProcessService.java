package dmax.demo.imagegenerationmanager.services;

import dmax.demo.imagegenerationmanager.models.GenerateImageProcess;
import dmax.demo.imagegenerationmanager.repositories.GenerateImageProcessRepository;

import io.micrometer.observation.annotation.Observed;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Observed
@Service
public class GenerateImageProcessService {

  @Autowired
  private GenerateImageProcessRepository generateImageProcessRepository;

  public Optional<GenerateImageProcess> getByProcessId(@NotNull UUID processId) {
    return generateImageProcessRepository.findByProcessId(processId);
  }

  public List<GenerateImageProcess> getListByStatus(@Nullable GenerateImageProcess.Status status) {
    List<GenerateImageProcess> processes = new ArrayList<>();
    if (status == null) {
      processes.addAll(generateImageProcessRepository.findAll());
    } else {
      processes.addAll(generateImageProcessRepository.findByStatus(status));
    }
    return processes;
  }

  public GenerateImageProcess saveProcess(@NotNull GenerateImageProcess process) {
    return generateImageProcessRepository.save(process);
  }

  public GenerateImageProcess updateStatusAndError(@NotNull UUID processId, @NotNull GenerateImageProcess.Status status, @Nullable String error) {
    GenerateImageProcess processToUpdate = getByProcessId(processId)
        .orElseThrow(() -> new RuntimeException(String.format("%s with processId %s not found", GenerateImageProcess.class, processId)));
    processToUpdate.setStatus(status);
    if(null != error) {
      processToUpdate.setError(error);
    }
    return saveProcess(processToUpdate);
  }


}
