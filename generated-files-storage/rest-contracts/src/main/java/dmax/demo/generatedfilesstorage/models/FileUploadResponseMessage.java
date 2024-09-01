package dmax.demo.generatedfilesstorage.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class FileUploadResponseMessage {

  private String message;

  public FileUploadResponseMessage(String message) {
    this.message = message;
  }

}
