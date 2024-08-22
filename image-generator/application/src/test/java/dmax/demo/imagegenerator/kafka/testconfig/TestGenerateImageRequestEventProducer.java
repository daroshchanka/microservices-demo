package dmax.demo.imagegenerator.kafka.testconfig;

import dmax.demo.imagegenerationmanager.kafka.events.GenerateImageRequestEvent;
import dmax.demo.imagegenerator.kafka.KafkaTopicConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TestGenerateImageRequestEventProducer {

  private static final String TOPIC = KafkaTopicConfiguration.IMAGE_GENERATOR_GENERATION_REQUESTS_EVENTS_TOPIC;

  @Autowired
  private KafkaTemplate<String, GenerateImageRequestEvent> kafkaTemplate;

  public void sendEvent(GenerateImageRequestEvent event) {
    kafkaTemplate.send(TOPIC, event);
  }
}
