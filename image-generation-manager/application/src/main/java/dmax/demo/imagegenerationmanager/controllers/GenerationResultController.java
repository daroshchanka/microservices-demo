package dmax.demo.imagegenerationmanager.controllers;

import dmax.demo.imagegenerationmanager.models.GenerationResult;
import dmax.demo.imagegenerationmanager.services.GenerationResultService;
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

@Tag(name = "generation-results", description = "Generation results APIs")
@RestController
@RequestMapping("/api")
public class GenerationResultController {

  @Autowired
  GenerationResultService generationResultService;

  @Operation(
      summary = "Retrieve Results",
      description = "Get a GenerationResult array."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", content = @Content(
          mediaType = "application/json",
          array = @ArraySchema(schema = @Schema(implementation = GenerationResult.class))), description = "Get GenerationResults"
      ),
      @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})}
  )
  @GetMapping("/generation-results")
  public ResponseEntity<List<GenerationResult>> getAllResults(
      @Parameter(name = "processId", in = ParameterIn.QUERY) @RequestParam(required = false, name = "processId") UUID processId
  ) {
    try {
      return new ResponseEntity<>(generationResultService.getList(processId), HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/generation-results/{id}")
  public ResponseEntity<GenerationResult> getGenerationResultById(@PathVariable("id") UUID id) {
    Optional<GenerationResult> resultData = generationResultService.getById(id);
    return resultData
        .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

}
