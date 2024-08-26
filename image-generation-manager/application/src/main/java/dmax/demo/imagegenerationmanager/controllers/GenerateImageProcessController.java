package dmax.demo.imagegenerationmanager.controllers;

import dmax.demo.imagegenerationmanager.kafka.events.GenerateImageRequestEvent;
import dmax.demo.imagegenerationmanager.kafka.producers.GenerateImageRequestEventProducer;
import dmax.demo.imagegenerationmanager.models.GenerateImageCommand;
import dmax.demo.imagegenerationmanager.models.GenerateImageProcess;
import dmax.demo.imagegenerationmanager.services.GenerateImageProcessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "generate-image", description = "Generate Image APIs")
@RestController
@RequestMapping("/api")
public class GenerateImageProcessController {

  @Autowired
  GenerateImageProcessService generateImageProcessService;

  @Autowired
  private GenerateImageRequestEventProducer generateImageRequestEventProducer;

  @Operation(
      summary = "Retrieve Processes",
      description = "Get a GenerateImageProcess array."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", content = @Content(
          mediaType = "application/json",
          array = @ArraySchema(schema = @Schema(implementation = GenerateImageProcess.class))), description = "Get GenerateImageProcess"
      ),
      @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})}
  )
  @GetMapping("/processes")
  public ResponseEntity<List<GenerateImageProcess>> getAllProcesses(
      @Parameter(name = "status", in = ParameterIn.QUERY) @RequestParam(required = false, name = "status") GenerateImageProcess.Status status
  ) {
    try {
      List<GenerateImageProcess> processes = generateImageProcessService.getListByStatus(status);
      return new ResponseEntity<>(processes, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/processes/{processId}")
  public ResponseEntity<GenerateImageProcess> getGenerateImageProcessById(@PathVariable("processId") UUID processId) {
    Optional<GenerateImageProcess> processData = generateImageProcessService.getByProcessId(processId);
    return processData
        .map(generateImageProcess -> new ResponseEntity<>(generateImageProcess, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @PostMapping("/action/generate")
  public ResponseEntity<GenerateImageProcess> createGenerateImageProcess(@RequestBody GenerateImageCommand command) {
    try {
      GenerateImageRequestEvent.GenerateImageCommand eventCommand =
          new GenerateImageRequestEvent.GenerateImageCommand(
              command.getImageText(),
              new GenerateImageRequestEvent.Font(
                  command.getFont().getName(),
                  command.getFont().getSize()
              ),
              new GenerateImageRequestEvent.Color(
                  command.getColor().getR(),
                  command.getColor().getG(),
                  command.getColor().getB()
              )
          );
      GenerateImageProcess process = generateImageProcessService.saveProcess(new GenerateImageProcess(command));
      generateImageRequestEventProducer.sendEvent(
          new GenerateImageRequestEvent(process.getProcessId(), eventCommand)
      );
      return new ResponseEntity<>(process, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
