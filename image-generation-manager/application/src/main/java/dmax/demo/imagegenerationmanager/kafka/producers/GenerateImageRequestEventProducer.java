package dmax.demo.imagegenerationmanager.kafka.producers;

import dmax.demo.imagegenerationmanager.kafka.KafkaTopicConfiguration;
import dmax.demo.imagegenerationmanager.kafka.events.GenerateImageRequestEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class GenerateImageRequestEventProducer {

  private static final String TOPIC = KafkaTopicConfiguration.IMAGE_GENERATOR_GENERATION_REQUESTS_EVENTS_TOPIC;

  @Autowired
  private KafkaTemplate<String, GenerateImageRequestEvent> kafkaTemplate;

  public void sendEvent(GenerateImageRequestEvent event) {
    kafkaTemplate.send(TOPIC, event);
  }
}
