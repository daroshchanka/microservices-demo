package dmax.demo.imagegenerationmanager.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table
public class GenerateImageCommand {

  @Schema(hidden = true)
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;
  private String imageText;
  @ManyToOne(cascade = CascadeType.ALL)
  private Font font;
  @ManyToOne(cascade = CascadeType.ALL)
  private Color color;

  @ToString
  @Getter
  @Setter
  @Entity
  public static class Font {

    @Schema(hidden = true)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private int size;
  }

  @ToString
  @Getter
  @Setter
  @Entity
  public static class Color {

    @Schema(hidden = true)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private int r;
    private int g;
    private int b;
  }

  public GenerateImageCommand(String imageText) {
    this.imageText = imageText;
  }

}
