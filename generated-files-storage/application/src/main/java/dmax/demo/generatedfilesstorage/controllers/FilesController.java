package dmax.demo.generatedfilesstorage.controllers;

import dmax.demo.generatedfilesstorage.models.FileUploadResponseMessage;
import dmax.demo.generatedfilesstorage.services.FileStorageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.UUID;

@Tag(name = "files", description = "Manage files APIs")
@RestController
@RequestMapping("/api")
public class FilesController {

  @Autowired
  private FileStorageService fileStorageService;

  @GetMapping(
      value = "/files/{fileId}",
      produces = MediaType.IMAGE_PNG_VALUE
  )
  public @ResponseBody byte[] getFileById(@PathVariable("fileId") UUID fileId) throws IOException {
    if (!fileStorageService.isFileExists(fileId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "fileId " + fileId + " not found");
    }
    return fileStorageService.getFileContent(fileId);
  }

  @PostMapping(value = "/files/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<FileUploadResponseMessage> uploadFile(
      @RequestParam("file") MultipartFile file, @RequestPart("fileId") String fileId) {
    try {
      String successMessage = fileStorageService.doUpload(file, UUID.fromString(fileId));
      return ResponseEntity.status(HttpStatus.OK).body(new FileUploadResponseMessage(successMessage));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new FileUploadResponseMessage(e.getMessage()));
    }
  }

}
