package dmax.demo.imagegenerator.kafka.consumers;

import dmax.demo.documents.feign.DocumentsClient;
import dmax.demo.documents.feign.JavaFileToMultipartFile;
import dmax.demo.imagegenerationmanager.kafka.events.GenerateImageRequestEvent;
import dmax.demo.imagegenerator.events.ImageGenerationFinishedEvent;
import dmax.demo.imagegenerator.kafka.KafkaTopicConfiguration;
import dmax.demo.imagegenerator.kafka.producers.ImageGenerationFinishedEventProducer;
import dmax.demo.imagegenerator.utils.TextToImageConverter;
import jakarta.servlet.ServletContext;
import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.UUID;

@Service
public class GenerateImageRequestEventConsumer {

  @Autowired
  ServletContext context;

  @Autowired
  TextToImageConverter textToImageConverter;

  @Autowired
  private ImageGenerationFinishedEventProducer imageGenerationFinishedEventProducer;

  @Autowired
  private DocumentsClient documentsClient;

  @Value("http://${integration.documents.server}")
  private String documentsIntegrationUrl;

  private static final String TOPIC = KafkaTopicConfiguration.IMAGE_GENERATOR_GENERATION_REQUESTS_EVENTS_TOPIC;
  @KafkaListener(
      topics = TOPIC, groupId = "image-generator"
  )
  public void handleEvent(ConsumerRecord<String, GenerateImageRequestEvent> event) {
    ImageGenerationFinishedEvent resultEvent = new ImageGenerationFinishedEvent(null, null, null);
    try {
      System.out.println("Received GenerateImageRequestEvent Event: " + event.value());
      UUID processId = event.value().getProcessId();
      resultEvent.setProcessId(processId);
      UUID fileId = UUID.randomUUID();
      documentsClient.uploadFile(
          URI.create(documentsIntegrationUrl),
          new JavaFileToMultipartFile(textToImageConverter.convert(event.value().getCommand(), fileId, context)),
          fileId.toString()
      );
      textToImageConverter.cleanupTempFile(fileId, context);

      resultEvent.setFileId(fileId);
      resultEvent.setStatus(ImageGenerationFinishedEvent.Status.OK);
    } catch (Exception e) {
      resultEvent.setStatus(ImageGenerationFinishedEvent.Status.FAILED);
      String errorMessage = e.getMessage();
      if (errorMessage != null) {
        if (errorMessage.length() > 255) {
          errorMessage = errorMessage.substring(0, 250) + "...";
        }
      }
      resultEvent.setError(errorMessage);
    }
    imageGenerationFinishedEventProducer.sendEvent(resultEvent);
  }
}
