package dmax.demo.imagegenerationmanager.kafka.consumers;

import dmax.demo.imagegenerationmanager.kafka.KafkaTopicConfiguration;
import dmax.demo.imagegenerationmanager.models.GenerateImageProcess;
import dmax.demo.imagegenerationmanager.models.GenerationResult;
import dmax.demo.imagegenerationmanager.repositories.GenerateImageProcessRepository;
import dmax.demo.imagegenerationmanager.repositories.GenerationResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ImageGenerationFinishedEventConsumer {

  @Autowired
  GenerateImageProcessRepository generateImageProcessRepository;

  @Autowired
  GenerationResultRepository generationResultRepository;

  private static final String TOPIC = KafkaTopicConfiguration.IMAGE_GENERATOR_MANAGER_IMAGE_GENERATOR_EVENTS_TOPIC;

  @KafkaListener(topics = TOPIC, groupId = "image-generator-manager")
  public void handleEvent(dmax.demo.imagegenerator.events.ImageGenerationFinishedEvent event) {
    System.out.println("Received GenerateImageRequestEvent Event: " + event);
    UUID processId = event.getProcessId();
    GenerateImageProcess processToUpdate = generateImageProcessRepository.findByProcessId(processId).get();
    processToUpdate.setStatus(mapStatus(event.getStatus()));
    processToUpdate.setError(event.getError());
    generateImageProcessRepository.save(processToUpdate);
    GenerationResult generationResult = new GenerationResult(processId, event.getFileId());
    generationResultRepository.save(generationResult);
  }

  private GenerateImageProcess.Status mapStatus(dmax.demo.imagegenerator.events.ImageGenerationFinishedEvent.Status eventStatus) {
    return eventStatus == dmax.demo.imagegenerator.events.ImageGenerationFinishedEvent.Status.OK ? GenerateImageProcess.Status.FINISHED : GenerateImageProcess.Status.FAILED;
  }
}
