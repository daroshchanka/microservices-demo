package dmax.demo.documents.feign;

import dmax.demo.documents.models.FileUploadResponseMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@FeignClient(
    name = "documents",
    configuration = FeignConfig.class
)
public interface DocumentsClient {

  @PostMapping(path = "/api/files/upload", consumes = "multipart/form-data;*", produces = MediaType.APPLICATION_JSON_VALUE)
  FileUploadResponseMessage uploadFile(URI url, @RequestPart("file") MultipartFile file, @RequestPart("fileId") String fileId);

}
