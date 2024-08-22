package dmax.demo.imagegenerator.kafka.testconfig;

import dmax.demo.imagegenerator.events.ImageGenerationFinishedEvent;
import dmax.demo.imagegenerator.kafka.KafkaTopicConfiguration;
import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Getter
@Component
public class TestImageGenerationFinishedEventConsumer {

  private ImageGenerationFinishedEvent consumed;
  private CountDownLatch latch = new CountDownLatch(1);

  @KafkaListener(
      topics = KafkaTopicConfiguration.IMAGE_GENERATOR_MANAGER_IMAGE_GENERATOR_EVENTS_TOPIC,
      groupId = "image-generator"
  )
  public void receive(ConsumerRecord<?, ImageGenerationFinishedEvent> consumerRecord) {
    consumed = consumerRecord.value();
    latch.countDown();
  }

  public void resetLatch() {
    latch = new CountDownLatch(1);
  }

}
