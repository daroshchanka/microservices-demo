package dmax.demo.imagegenerator.utils;

import dmax.demo.imagegenerationmanager.kafka.events.GenerateImageRequestEvent;
import jakarta.servlet.ServletContext;
import joptsimple.internal.Strings;
import lombok.SneakyThrows;
import net.datafaker.Faker;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.io.File;
import java.util.UUID;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9093", "port=9093"})
@SpringBootTest
class TextToImageConverterImplTest {

  @Autowired
  ServletContext servletContext;

  @Autowired
  TextToImageConverter textToImageConverter;

  static private boolean convertPassed = false;
  static private UUID fileId;
  static private File result;

  @Order(1)
  @SneakyThrows
  @Test
  void convertTest() {
    GenerateImageRequestEvent.GenerateImageCommand generateImageCommand
        = new GenerateImageRequestEvent.GenerateImageCommand(
        Strings.join(new Faker().lorem().words(50), "\n"),
        new GenerateImageRequestEvent.Font("Monospaced", 30),
        new GenerateImageRequestEvent.Color(21, 234, 99)
    );

    fileId = UUID.randomUUID();
    result = textToImageConverter.convert(generateImageCommand, fileId, servletContext);
    Assertions.assertTrue(result.exists());
    Assertions.assertTrue(result.getName().endsWith(fileId + ".png"));
    Assertions.assertTrue(result.length() > 5_000);
    convertPassed = true;
  }

  @Order(2)
  @SneakyThrows
  @Test
  void cleanupTest() {
    Assumptions.assumeTrue(convertPassed, "depends on[convertPassed] test not passed");
    textToImageConverter.cleanupTempFile(fileId, servletContext);
    Assertions.assertFalse(result.exists());
  }

}
