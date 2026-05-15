package whitestone.trainee_management.models;



import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class TraineeAssessmentAttempt extends AuditModel{

// @Id
// @GeneratedValue(strategy = GenerationType.IDENTITY)
// private Long id;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attempt_seq")
	@SequenceGenerator(
	    name = "attempt_seq",
	    sequenceName = "attempt_seq",
	    allocationSize = 1
	)
	private Long id;

 private String traineeId;
 //private List<String> trainerIds;
 private Long assessmentId;
 private boolean submitted;
 
 private String remarks;

 private Integer totalMarks;
 
 private Integer overallMarks;
 
 @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
 @JoinColumn(name = "attempt_id")
 private List<SectionAnswer> answers;

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

 public boolean isSubmitted() {
	return submitted;
 }

 public void setSubmitted(boolean submitted) {
	this.submitted = submitted;
 }

 public List<SectionAnswer> getAnswers() {
	return answers;
 }

 public void setAnswers(List<SectionAnswer> answers) {
	this.answers = answers;
 }
 
 public String getRemarks() {
		return remarks;
	}

	 public void setRemarks(String remarks) {
		this.remarks = remarks;
	 }
	 @Column(name = "submitted_at")
	 private LocalDateTime submittedAt;

// public List<String> getTrainerIds() {
//	return trainerIds;
// }
//
// public void setTrainerIds(List<String> trainerIds) {
//	this.trainerIds = trainerIds;
// }

 // getters and setters
	 
	

	 public Integer getTotalMarks() {
	     return totalMarks;
	 }

	 public LocalDateTime getSubmittedAt() {
		return submittedAt;
	}

	 public void setSubmittedAt(LocalDateTime submittedAt) {
		 this.submittedAt = submittedAt;
	 }

	 public void setTotalMarks(Integer totalMarks) {
	     this.totalMarks = totalMarks;
	 }

	 public Integer getOverallMarks() {
		 return overallMarks;
	 }

	 public void setOverallMarks(Integer overallMarks) {
		 this.overallMarks = overallMarks;
	 }
	 
}
