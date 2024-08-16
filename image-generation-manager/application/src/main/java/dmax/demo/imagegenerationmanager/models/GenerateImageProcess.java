package dmax.demo.imagegenerationmanager.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "processes")
public class GenerateImageProcess {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;
  private UUID processId;

  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdAt;
  @UpdateTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  private Date updatedAt;
  @Enumerated(EnumType.STRING)
  private Status status;
  private String error;

  public enum Status {
    IN_PROGRESS,
    FINISHED,
    FAILED,
  }

  @OneToOne(cascade = CascadeType.ALL)
  private GenerateImageCommand details;

  public GenerateImageProcess(GenerateImageCommand command) {
    processId = UUID.randomUUID();
    details = command;
    status = Status.IN_PROGRESS;
  }

}
