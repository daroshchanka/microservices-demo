package dmax.demo.imagegenerationmanager.controllers;

import dmax.demo.imagegenerationmanager.kafka.events.GenerateImageRequestEvent;
import dmax.demo.imagegenerationmanager.kafka.producers.GenerateImageRequestEventProducer;
import dmax.demo.imagegenerationmanager.models.GenerateImageCommand;
import dmax.demo.imagegenerationmanager.models.GenerateImageProcess;
import dmax.demo.imagegenerationmanager.repositories.GenerateImageProcessRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "generate-image", description = "Generate Image APIs")
@RestController
@RequestMapping("/api")
public class GenerateImageProcessController {

  @Autowired
  GenerateImageProcessRepository generateImageProcessRepository;

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
      List<GenerateImageProcess> processes = new ArrayList<>();
      if (status == null) {
        processes.addAll(generateImageProcessRepository.findAll());
      } else {
        processes.addAll(generateImageProcessRepository.findByStatus(status));
      }
      return new ResponseEntity<>(processes, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/processes/{processId}")
  public ResponseEntity<GenerateImageProcess> getGenerateImageProcessById(@PathVariable("processId") UUID processId) {
    Optional<GenerateImageProcess> processData = generateImageProcessRepository.findByProcessId(processId);

    return processData
        .map(generateImageProcess -> new ResponseEntity<>(generateImageProcess, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @PostMapping("/action/generate")
  public ResponseEntity<GenerateImageProcess> createGenerateImageProcess(@RequestBody GenerateImageCommand command) {
    try {
      GenerateImageProcess entity = new GenerateImageProcess(command);
      GenerateImageProcess process = generateImageProcessRepository.save(entity);

      GenerateImageRequestEvent.GenerateImageCommand eventCommand =
          new GenerateImageRequestEvent.GenerateImageCommand();
      eventCommand.setImageText(entity.getDetails().getImageText());
      GenerateImageRequestEvent.Font eventCommandFont = new GenerateImageRequestEvent.Font();
      eventCommandFont.setName(entity.getDetails().getFont().getName());
      eventCommandFont.setSize(entity.getDetails().getFont().getSize());
      eventCommand.setFont(eventCommandFont);
      GenerateImageRequestEvent.Color eventCommandColor = new GenerateImageRequestEvent.Color();
      eventCommandColor.setR(entity.getDetails().getColor().getR());
      eventCommandColor.setG(entity.getDetails().getColor().getG());
      eventCommandColor.setB(entity.getDetails().getColor().getB());
      eventCommand.setColor(eventCommandColor);

      generateImageRequestEventProducer.sendEvent(
          new GenerateImageRequestEvent(entity.getProcessId(), eventCommand)
      );
      return new ResponseEntity<>(process, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
