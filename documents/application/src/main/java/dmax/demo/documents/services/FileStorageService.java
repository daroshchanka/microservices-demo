package dmax.demo.documents.services;

import io.micrometer.observation.annotation.Observed;
import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Observed
@Service
public class FileStorageService {

  private static final Path UPLOADS = Paths.get("uploads");

  public boolean isFileExists(@NotNull UUID fileId) {
    return resolveFile(fileId).exists();
  }

  @SneakyThrows
  public byte[] getFileContent(@NotNull UUID fileId) {
    try (InputStream targetStream = FileUtils.openInputStream(resolveFile(fileId))) {
      return IOUtils.toByteArray(targetStream);
    }
  }

  private File resolveFile(@NotNull UUID fileId) {
    return new File(UPLOADS.toFile(), fileId + ".png");
  }

  /**
   * @return success message
   * */
  public String doUpload(@NotNull MultipartFile file, @NotNull UUID fileId) {
    String message = "";
    try {
      if (!UPLOADS.toFile().exists()) {
        Files.createDirectories(UPLOADS);
      }
      Files.copy(file.getInputStream(), resolveFile(fileId).toPath());
      message = "Uploaded the file successfully: " + UPLOADS.toFile().getAbsolutePath() + "/" + file.getOriginalFilename();
    } catch (Exception e) {
      throw new RuntimeException("Could not upload the file: " + file.getOriginalFilename() + ". Error: " + e.getMessage(), e);
    }
    return message;
  }
}
