package dmax.demo.imagegenerator.kafka.producers;

import dmax.demo.imagegenerator.events.ImageGenerationFinishedEvent;
import dmax.demo.imagegenerator.kafka.KafkaTopicConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ImageGenerationFinishedEventProducer {

  private static final String TOPIC = KafkaTopicConfiguration.IMAGE_GENERATOR_MANAGER_IMAGE_GENERATOR_EVENTS_TOPIC;

  @Autowired
  private KafkaTemplate<String, ImageGenerationFinishedEvent> kafkaTemplate;

  public void sendEvent(ImageGenerationFinishedEvent event) {
    kafkaTemplate.send(TOPIC, event);
  }
}
