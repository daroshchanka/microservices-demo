package dmax.demo.imagegenerationmanager.kafka.events;

import java.util.UUID;

public class GenerateImageRequestEvent {

  private UUID processId;
  private GenerateImageCommand command;

  public GenerateImageRequestEvent() {
  }

  public GenerateImageRequestEvent(UUID processId, GenerateImageCommand command) {
    this.processId = processId;
    this.command = command;
  }

  public UUID getProcessId() {
    return processId;
  }

  public void setProcessId(UUID processId) {
    this.processId = processId;
  }

  public GenerateImageCommand getCommand() {
    return command;
  }

  public void setCommand(GenerateImageCommand command) {
    this.command = command;
  }

  public static class GenerateImageCommand {
    private String imageText;
    private Font font;
    private Color color;

    public GenerateImageCommand() {
    }

    public GenerateImageCommand(String imageText) {
      this.imageText = imageText;
    }

    public String getImageText() {
      return imageText;
    }

    public void setImageText(String imageText) {
      this.imageText = imageText;
    }

    public Font getFont() {
      return font;
    }

    public void setFont(Font font) {
      this.font = font;
    }

    public Color getColor() {
      return color;
    }

    public void setColor(Color color) {
      this.color = color;
    }
  }

  public static class Font {
    private String name;
    private int size;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public int getSize() {
      return size;
    }

    public void setSize(int size) {
      this.size = size;
    }
  }

  public static class Color {
    private int r;
    private int g;
    private int b;

    public int getR() {
      return r;
    }

    public void setR(int r) {
      this.r = r;
    }

    public int getG() {
      return g;
    }

    public void setG(int g) {
      this.g = g;
    }

    public int getB() {
      return b;
    }

    public void setB(int b) {
      this.b = b;
    }
  }


}
