package dmax.demo.imagegenerator.kafka;

import dmax.demo.documents.feign.DocumentsClient;
import dmax.demo.documents.models.FileUploadResponseMessage;
import dmax.demo.imagegenerationmanager.kafka.events.GenerateImageRequestEvent;
import dmax.demo.imagegenerator.events.ImageGenerationFinishedEvent;
import dmax.demo.imagegenerator.kafka.testconfig.TestGenerateImageRequestEventProducer;
import dmax.demo.imagegenerator.kafka.testconfig.TestImageGenerationFinishedEventConsumer;
import dmax.demo.imagegenerator.utils.TextToImageConverter;
import jakarta.servlet.ServletContext;
import joptsimple.internal.Strings;
import lombok.SneakyThrows;
import net.datafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class GenerateImageRequestEventConsumerTest {

  @MockBean
  TextToImageConverter textToImageConverterMock;

  @MockBean
  private DocumentsClient documentsClientMock;

  @Autowired
  TestGenerateImageRequestEventProducer testProducer;

  @Autowired
  TestImageGenerationFinishedEventConsumer testConsumer;

  private File file;

  @SneakyThrows
  @BeforeTestClass
  void initFile() {
    file = File.createTempFile("tmp", ".png");
  }

  @SneakyThrows
  @Test
  public void generateImageRequestEventConsumerPositiveTest() {
    GenerateImageRequestEvent.GenerateImageCommand generateImageCommand = provideTestCommand();
    when(textToImageConverterMock.convert(
        eq(generateImageCommand), any(UUID.class), any(ServletContext.class)))
        .thenReturn(file);
    doNothing().when(textToImageConverterMock).cleanupTempFile(any(UUID.class), any(ServletContext.class));
    FileUploadResponseMessage fileUploadResponseMessage = new FileUploadResponseMessage("ok");
    when(documentsClientMock.uploadFile(any(URI.class), any(MultipartFile.class), anyString()))
        .thenReturn(fileUploadResponseMessage);

    GenerateImageRequestEvent event = new GenerateImageRequestEvent(UUID.randomUUID(), generateImageCommand);
    testProducer.sendEvent(event);

    ImageGenerationFinishedEvent imageGenerationFinishedEvent = consume();

    Assertions.assertEquals(imageGenerationFinishedEvent.getStatus(), ImageGenerationFinishedEvent.Status.OK);
    Assertions.assertNotEquals(imageGenerationFinishedEvent.getFileId(), null);
    Assertions.assertEquals(imageGenerationFinishedEvent.getProcessId(), event.getProcessId());
    Assertions.assertNull(imageGenerationFinishedEvent.getError());
  }

  @SneakyThrows
  @Test
  public void generateImageRequestEventConsumerNegativeTest() {
    Exception exception = new RuntimeException(new Faker().text().text(255));
    when(textToImageConverterMock.convert(
        any(GenerateImageRequestEvent.GenerateImageCommand.class), any(UUID.class), any(ServletContext.class)))
        .thenThrow(exception);

    GenerateImageRequestEvent.GenerateImageCommand generateImageCommand = provideTestCommand();
    GenerateImageRequestEvent event = new GenerateImageRequestEvent(UUID.randomUUID(), generateImageCommand);
    testProducer.sendEvent(event);

    ImageGenerationFinishedEvent imageGenerationFinishedEvent = consume();

    Assertions.assertEquals(imageGenerationFinishedEvent.getStatus(), ImageGenerationFinishedEvent.Status.FAILED);
    Assertions.assertNull(imageGenerationFinishedEvent.getFileId());
    Assertions.assertEquals(imageGenerationFinishedEvent.getProcessId(), event.getProcessId());
    Assertions.assertEquals(imageGenerationFinishedEvent.getError(), exception.getMessage());
  }

  @SneakyThrows
  @Test
  public void generateImageRequestEventConsumerNegativeErrorMessageIsNullTest() {
    Exception exception = new RuntimeException();
    when(textToImageConverterMock.convert(
        any(GenerateImageRequestEvent.GenerateImageCommand.class), any(UUID.class), any(ServletContext.class)))
        .thenThrow(exception);

    GenerateImageRequestEvent event = new GenerateImageRequestEvent(UUID.randomUUID(), provideTestCommand());
    testProducer.sendEvent(event);

    ImageGenerationFinishedEvent imageGenerationFinishedEvent = consume();

    Assertions.assertEquals(ImageGenerationFinishedEvent.Status.FAILED, imageGenerationFinishedEvent.getStatus());
    Assertions.assertNull(imageGenerationFinishedEvent.getFileId());
    Assertions.assertEquals(event.getProcessId(), imageGenerationFinishedEvent.getProcessId());
    Assertions.assertNull(imageGenerationFinishedEvent.getError());
  }

  @SneakyThrows
  @Test
  public void generateImageRequestEventConsumerNegativeErrorMessageLengthIsOver255Test() {
    Exception exception = new RuntimeException(new Faker().text().text(256));
    when(textToImageConverterMock.convert(
        any(GenerateImageRequestEvent.GenerateImageCommand.class), any(UUID.class), any(ServletContext.class)))
        .thenThrow(exception);

    GenerateImageRequestEvent event = new GenerateImageRequestEvent(UUID.randomUUID(), provideTestCommand());
    testProducer.sendEvent(event);

    ImageGenerationFinishedEvent imageGenerationFinishedEvent = consume();

    Assertions.assertEquals(ImageGenerationFinishedEvent.Status.FAILED, imageGenerationFinishedEvent.getStatus());
    Assertions.assertNull(imageGenerationFinishedEvent.getFileId());
    Assertions.assertEquals(event.getProcessId(), imageGenerationFinishedEvent.getProcessId());
    Assertions.assertEquals(253, imageGenerationFinishedEvent.getError().length());
  }

  @SneakyThrows
  private ImageGenerationFinishedEvent consume() {
    boolean messageConsumed = testConsumer.getLatch().await(5, TimeUnit.SECONDS);
    Assertions.assertTrue(messageConsumed);
    return testConsumer.getConsumed();
  }

  private GenerateImageRequestEvent.GenerateImageCommand provideTestCommand() {
    GenerateImageRequestEvent.GenerateImageCommand generateImageCommand
        = new GenerateImageRequestEvent.GenerateImageCommand(Strings.join(new Faker().lorem().words(7), " "));
    GenerateImageRequestEvent.Font font = new GenerateImageRequestEvent.Font();
    font.setName("test");
    font.setSize(10);
    generateImageCommand.setFont(font);
    GenerateImageRequestEvent.Color color = new GenerateImageRequestEvent.Color();
    color.setR(100);
    color.setG(100);
    color.setB(100);
    generateImageCommand.setColor(color);
    return generateImageCommand;
  }
}
