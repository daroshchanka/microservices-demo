package dmax.demo.documents.controllers;

import dmax.demo.documents.models.FileUploadResponseMessage;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Tag(name = "documents", description = "Manage documents APIs")
@RestController
@RequestMapping("/api")
public class DocumentsController {

  private static final Path UPLOADS = Paths.get("uploads");

  @GetMapping(
      value = "/files/{fileId}",
      produces = MediaType.IMAGE_PNG_VALUE
  )
  public @ResponseBody byte[] getFileById(@PathVariable("fileId") UUID fileId) throws IOException {
    File initialFile = resolveFile(fileId);
    if (!initialFile.exists()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "fileId " + fileId + " not found");
    }
    try (InputStream targetStream = FileUtils.openInputStream(initialFile)) {
      return IOUtils.toByteArray(targetStream);
    }
  }

  @PostMapping(value = "/files/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<FileUploadResponseMessage> uploadFile(
      @RequestParam("file") MultipartFile file, @RequestPart("fileId") String fileId) {
    String message = "";
    try {
      if (!UPLOADS.toFile().exists()) {
        Files.createDirectories(UPLOADS);
      }
      File initialFile = resolveFile(UUID.fromString(fileId));
      try {
        Files.copy(file.getInputStream(), initialFile.toPath());
      } catch (Exception e) {
        if (e instanceof FileAlreadyExistsException) {
          throw new RuntimeException("A file of that name already exists.");
        }
        throw new RuntimeException(e.getMessage());
      }
      message = "Uploaded the file successfully: " + UPLOADS.toFile().getAbsolutePath() + "/" + file.getOriginalFilename();
      System.out.println(message);
      return ResponseEntity.status(HttpStatus.OK).body(new FileUploadResponseMessage(message));
    } catch (Exception e) {
      message = "Could not upload the file: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new FileUploadResponseMessage(message));
    }
  }

  private File resolveFile(UUID fileId) {
    return new File(UPLOADS.toFile(), fileId + ".png");
  }

}
