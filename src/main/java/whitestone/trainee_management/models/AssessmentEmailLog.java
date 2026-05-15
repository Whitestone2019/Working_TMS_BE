package whitestone.trainee_management.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
	    uniqueConstraints = @UniqueConstraint(columnNames = {"traineeId", "assessmentId"})
	)
public class AssessmentEmailLog extends AuditModel {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "assessment_email_log_seq")
	@SequenceGenerator(
	    name = "assessment_email_log_seq",
	    sequenceName = "assessment_email_log_seq",
	    allocationSize = 1
	)
	private Long id;

    private String traineeId;

    private Long assessmentId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTraineeId() {
		return traineeId;
	}

	public void setTraineeId(String traineeId) {
		this.traineeId = traineeId;
	}

	public Long getAssessmentId() {
		return assessmentId;
	}

	public void setAssessmentId(Long assessmentId) {
		this.assessmentId = assessmentId;
	}
    
}
