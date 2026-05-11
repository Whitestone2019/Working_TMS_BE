package whitestone.trainee_management.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
	    uniqueConstraints = {
	        @UniqueConstraint(columnNames = {"trainee_id","trainer_id", "syllabus_id"})
	    }
	)
public class SyllabusFeedback extends AuditModel{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trainee_id", referencedColumnName = "trngid")
    private User trainee;
    
    @ManyToOne
    @JoinColumn(name = "trainer_id", referencedColumnName = "trngid")
    private User trainer;



	@ManyToOne
    @JoinColumn(name = "syllabus_id")
    private Syllabus syllabus;

    @Column(columnDefinition = "TEXT")
    private String traineeFeedback;

    private Boolean feedbackGivenTrainee = false;

    @Column(columnDefinition = "TEXT")
    private String trainerFeedback;

    private Boolean feedbackGivenTrainer = false;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	public User getTrainee() {
		return trainee;
	}

	public void setTrainee(User trainee) {
		this.trainee = trainee;
	}

    public User getTrainer() {
		return trainer;
	}

	public void setTrainer(User trainer) {
		this.trainer = trainer;
	}
	public Syllabus getSyllabus() {
		return syllabus;
	}

	public void setSyllabus(Syllabus syllabus) {
		this.syllabus = syllabus;
	}

	public String getTraineeFeedback() {
		return traineeFeedback;
	}

	public void setTraineeFeedback(String traineeFeedback) {
		this.traineeFeedback = traineeFeedback;
	}

	public Boolean getFeedbackGivenTrainee() {
		return feedbackGivenTrainee;
	}

	public void setFeedbackGivenTrainee(Boolean feedbackGivenTrainee) {
		this.feedbackGivenTrainee = feedbackGivenTrainee;
	}

	public String getTrainerFeedback() {
		return trainerFeedback;
	}

	public void setTrainerFeedback(String trainerFeedback) {
		this.trainerFeedback = trainerFeedback;
	}

	public Boolean getFeedbackGivenTrainer() {
		return feedbackGivenTrainer;
	}

	public void setFeedbackGivenTrainer(Boolean feedbackGivenTrainer) {
		this.feedbackGivenTrainer = feedbackGivenTrainer;
	}
    
    
}
