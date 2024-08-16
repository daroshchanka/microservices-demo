package dmax.demo.imagegenerationmanager.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "generation_results")
public class GenerationResult {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  private UUID relatedProcessId;
  private UUID fileId;

  public GenerationResult(UUID relatedProcessId, UUID fileId) {
    this.relatedProcessId = relatedProcessId;
    this.fileId = fileId;
  }

}
