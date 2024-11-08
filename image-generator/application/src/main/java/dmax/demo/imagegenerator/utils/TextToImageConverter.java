package dmax.demo.imagegenerator.utils;

import dmax.demo.imagegenerationmanager.kafka.events.GenerateImageRequestEvent;
import jakarta.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public interface TextToImageConverter {

  private static Logger logger() {
    final class LogHolder {
      private static final Logger LOGGER = LoggerFactory.getLogger(TextToImageConverter.class);
    }
    return LogHolder.LOGGER;
  }

  default File convert(GenerateImageRequestEvent.GenerateImageCommand command, UUID fileId, ServletContext context) throws IOException {
    Files.createDirectories(Paths.get(context.getRealPath("tmp")));
    File targetFile = getFile(fileId, context);
    logger().debug(targetFile.getAbsolutePath());
    convert(command.getImageText(), command.getFont(), command.getColor(), targetFile);
    new Font("Consolas", Font.BOLD, 36);
    return getFile(fileId, context);
  }

  default void cleanupTempFile(UUID fileId, ServletContext context) throws IOException {
    Files.delete(getFile(fileId, context).toPath());
  }

  private File getFile(UUID fileId, ServletContext context) {
    return new File(context.getRealPath("tmp"), fileId + ".png");
  }

  private static void convert(String text, GenerateImageRequestEvent.Font font, GenerateImageRequestEvent.Color color, File targetFile) throws IOException {
    String[] textArray = text.split("\n");
    BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = img.createGraphics();
    Font fontAwt = new Font(font.getName(), Font.BOLD, font.getSize());
    g2d.setFont(fontAwt);
    FontMetrics fm = g2d.getFontMetrics();
    int width = fm.stringWidth(getLongestLine(textArray));
    int lines = getLineCount(text);
    int height = fm.getHeight() * (lines + 4);
    g2d.dispose();
    img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    g2d = img.createGraphics();
    g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
    g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
    g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    g2d.setFont(fontAwt);
    fm = g2d.getFontMetrics();
    g2d.setColor(new Color(color.getR(), color.getG(), color.getB()));

    for (int i = 1; i <= lines; ++i) {
      g2d.drawString(textArray[i - 1], 0, fm.getAscent() * i);
    }
    g2d.dispose();
    ImageIO.write(img, "png", targetFile);
  }

  private static int getLineCount(String text) {
    return text.split("\n").length;
  }

  private static String getLongestLine(String[] arr) {
    String max = arr[0];
    for (int i = 1; i < arr.length; i++) {
      if (max.length() < arr[i].length()) {
        max = arr[i];
      }
    }
    return max;
  }

}
