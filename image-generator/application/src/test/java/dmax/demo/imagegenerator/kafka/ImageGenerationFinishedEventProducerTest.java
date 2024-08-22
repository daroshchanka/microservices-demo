package dmax.demo.imagegenerator.kafka;

import dmax.demo.imagegenerator.events.ImageGenerationFinishedEvent;
import dmax.demo.imagegenerator.kafka.producers.ImageGenerationFinishedEventProducer;
import dmax.demo.imagegenerator.kafka.testconfig.TestImageGenerationFinishedEventConsumer;
import joptsimple.internal.Strings;
import net.datafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class ImageGenerationFinishedEventProducerTest {

  @Autowired
  ImageGenerationFinishedEventProducer imageGenerationFinishedEventProducer;

  @Autowired
  TestImageGenerationFinishedEventConsumer consumer;

  @Test
  public void imageGenerationFinishedEventProducerTest() throws Exception {
    ImageGenerationFinishedEventProducer producer = imageGenerationFinishedEventProducer;
    ImageGenerationFinishedEvent event =
        new ImageGenerationFinishedEvent(UUID.randomUUID(), ImageGenerationFinishedEvent.Status.OK, null);
    event.setFileId(UUID.randomUUID());
    event.setError(Strings.join(new Faker().lorem().words(7), " "));
    producer.sendEvent(event);

    boolean messageConsumed = consumer.getLatch().await(10, TimeUnit.SECONDS);
    Assertions.assertTrue(messageConsumed);
    Assertions.assertEquals(event.getProcessId(), consumer.getConsumed().getProcessId());
    Assertions.assertEquals(event.getStatus(), consumer.getConsumed().getStatus());
    Assertions.assertEquals(event.getFileId(), consumer.getConsumed().getFileId());
    Assertions.assertEquals(event.getError(), consumer.getConsumed().getError());
  }
}
