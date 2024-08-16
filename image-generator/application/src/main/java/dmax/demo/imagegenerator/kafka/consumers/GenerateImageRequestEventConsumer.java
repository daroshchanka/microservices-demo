package dmax.demo.imagegenerator.kafka.consumers;

import dmax.demo.documents.feign.DocumentsClient;
import dmax.demo.documents.feign.JavaFileToMultipartFile;
import dmax.demo.imagegenerationmanager.kafka.events.GenerateImageRequestEvent;
import dmax.demo.imagegenerator.events.ImageGenerationFinishedEvent;
import dmax.demo.imagegenerator.kafka.producers.ImageGenerationFinishedEventProducer;
import dmax.demo.imagegenerator.kafka.KafkaTopicConfiguration;
import dmax.demo.imagegenerator.utils.TextToImageConverter;
import jakarta.servlet.ServletContext;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.UUID;

@Service
public class GenerateImageRequestEventConsumer {

  @Autowired
  ServletContext context;

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
    System.out.println("Received GenerateImageRequestEvent Event: " + event);
    UUID processId = event.value().getProcessId();
    ImageGenerationFinishedEvent resultEvent = new ImageGenerationFinishedEvent(processId, null, null);
    try {
      UUID fileId = UUID.randomUUID();
      TextToImageConverter.convert(event.value().getCommand(), fileId, context);
      MultipartFile result = new JavaFileToMultipartFile(TextToImageConverter.getFile(fileId, context));
      URI determinedBasePathUri = URI.create(documentsIntegrationUrl);
      documentsClient.uploadFile(determinedBasePathUri, result, fileId.toString());
      TextToImageConverter.cleanupTempFile(fileId, context);
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
