package dmax.demo.imagegenerator.events;

import java.util.UUID;

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

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public UUID getProcessId() {
    return processId;
  }

  public void setProcessId(UUID processId) {
    this.processId = processId;
  }

  public UUID getFileId() {
    return fileId;
  }

  public void setFileId(UUID fileId) {
    this.fileId = fileId;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

}
