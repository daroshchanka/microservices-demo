package dmax.demo.imagegenerator.kafka.producers;

import dmax.demo.imagegenerator.events.ImageGenerationFinishedEvent;
import dmax.demo.imagegenerator.kafka.KafkaTopicConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ImageGenerationFinishedEventProducer {

  private static final String TOPIC = KafkaTopicConfiguration.IMAGE_GENERATOR_MANAGER_IMAGE_GENERATOR_EVENTS_TOPIC;

  @Autowired
  private KafkaTemplate<String, ImageGenerationFinishedEvent> kafkaTemplate;

  public void sendEvent(ImageGenerationFinishedEvent event) {
    log.info("Received ImageGenerationFinishedEvent Event: {}", event);
    kafkaTemplate.send(TOPIC, event);
  }
}
