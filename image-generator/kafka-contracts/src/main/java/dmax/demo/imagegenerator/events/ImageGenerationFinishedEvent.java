package dmax.demo.imagegenerator.events;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@ToString
@Setter
@Getter
public class ImageGenerationFinishedEvent {

  private UUID processId;
  private UUID fileId;
  private Status status;
  private String error;

  public enum Status {
    OK,
    FAILED,
  }

  public ImageGenerationFinishedEvent() {
  }

  public ImageGenerationFinishedEvent(UUID processId, Status status, String error) {
    this.processId = processId;
    this.status = status;
    this.error = error;
  }

}
