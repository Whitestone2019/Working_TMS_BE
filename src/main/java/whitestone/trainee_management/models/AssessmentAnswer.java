package whitestone.trainee_management.models;

import jakarta.persistence.*;

@Entity
@Table(name="assessment_answers")
public class AssessmentAnswer extends AuditModel{

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private Long assessmentId;

private Long questionId;

private String traineeId;

@Column(length = 2000)
private String answer;

private boolean correct;
private boolean submitted;

public Long getId() {
	return id;
}

public void setId(Long id) {
	this.id = id;
}

public Long getAssessmentId() {
	return assessmentId;
}

public void setAssessmentId(Long assessmentId) {
	this.assessmentId = assessmentId;
}

public Long getQuestionId() {
	return questionId;
}

public void setQuestionId(Long questionId) {
	this.questionId = questionId;
}

public String getTraineeId() {
	return traineeId;
}

public void setTraineeId(String traineeId) {
	this.traineeId = traineeId;
}

public String getAnswer() {
	return answer;
}

public void setAnswer(String answer) {
	this.answer = answer;
}

public boolean isCorrect() {
	return correct;
}

public void setCorrect(boolean correct) {
	this.correct = correct;
}

public boolean isSubmitted() {
    return submitted;
}

public void setSubmitted(boolean submitted) {
    this.submitted = submitted;
}


}