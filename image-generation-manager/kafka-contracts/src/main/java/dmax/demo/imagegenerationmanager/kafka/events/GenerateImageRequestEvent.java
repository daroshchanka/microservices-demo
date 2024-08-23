package dmax.demo.imagegenerationmanager.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class GenerateImageRequestEvent {

  private UUID processId;
  private GenerateImageCommand command;

  @NoArgsConstructor
  @AllArgsConstructor
  @Setter
  @Getter
  public static class GenerateImageCommand {
    private String imageText;
    private Font font;
    private Color color;
  }

  @NoArgsConstructor
  @AllArgsConstructor
  @Setter
  @Getter
  public static class Font {
    private String name;
    private int size;
  }

  @NoArgsConstructor
  @AllArgsConstructor
  @Setter
  @Getter
  public static class Color {
    private int r;
    private int g;
    private int b;
  }


}
