package dmax.demo.imagegenerationmanager.kafka.consumers;

import dmax.demo.imagegenerationmanager.kafka.KafkaTopicConfiguration;
import dmax.demo.imagegenerationmanager.models.GenerateImageProcess;
import dmax.demo.imagegenerationmanager.models.GenerationResult;
import dmax.demo.imagegenerationmanager.services.GenerateImageProcessService;
import dmax.demo.imagegenerationmanager.services.GenerationResultService;
import dmax.demo.imagegenerator.events.ImageGenerationFinishedEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class ImageGenerationFinishedEventConsumer {

  @Autowired
  GenerateImageProcessService generateImageProcessService;

  @Autowired
  GenerationResultService generationResultService;

  private static final String TOPIC = KafkaTopicConfiguration.IMAGE_GENERATOR_MANAGER_IMAGE_GENERATOR_EVENTS_TOPIC;

  @KafkaListener(topics = TOPIC, groupId = "image-generation-manager")
  public void handleEvent(ImageGenerationFinishedEvent event) throws Exception {
    log.info("Received {} Event: {}", ImageGenerationFinishedEvent.class.getName(), event);
    generateImageProcessService.updateStatusAndError(event.getProcessId(), mapStatus(event.getStatus()), event.getError());

    GenerationResult generationResult = new GenerationResult(event.getProcessId(), event.getFileId());
    generationResultService.saveResult(generationResult);
  }

  private GenerateImageProcess.Status mapStatus(dmax.demo.imagegenerator.events.ImageGenerationFinishedEvent.Status eventStatus) {
    return eventStatus == dmax.demo.imagegenerator.events.ImageGenerationFinishedEvent.Status.OK ?
        GenerateImageProcess.Status.FINISHED :
        GenerateImageProcess.Status.FAILED;
  }
}
