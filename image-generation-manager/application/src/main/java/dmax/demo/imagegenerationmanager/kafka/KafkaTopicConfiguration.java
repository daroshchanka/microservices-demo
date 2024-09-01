package dmax.demo.imagegenerationmanager.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfiguration {

  public final static String IMAGE_GENERATOR_GENERATION_REQUESTS_EVENTS_TOPIC = "image-generator.generation-requests";
  public final static String IMAGE_GENERATOR_MANAGER_IMAGE_GENERATOR_EVENTS_TOPIC = "image-generation-manager.image-generator-events";

  @Bean
  public NewTopic topic1() {
    return new NewTopic(IMAGE_GENERATOR_GENERATION_REQUESTS_EVENTS_TOPIC, 1, (short) 1);
  }

  @Bean
  public NewTopic topic2() {
    return new NewTopic(IMAGE_GENERATOR_MANAGER_IMAGE_GENERATOR_EVENTS_TOPIC, 1, (short) 1);
  }
}