package whitestone.trainee_management.models;



import jakarta.persistence.*;

@Entity
public class QuestionAnswer extends AuditModel {

// @Id
// @GeneratedValue(strategy = GenerationType.IDENTITY)
// private Long id;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "question_answer_seq")
	@SequenceGenerator(
	    name = "question_answer_seq",
	    sequenceName = "question_answer_seq",
	    allocationSize = 1
	)
	private Long id;

 private Long questionId;

 
 @Lob
 @Column(columnDefinition = "TEXT")
 private String answer;
 
 private Integer marks;
 
 private boolean evaluated;

 public Long getId() {
	return id;
 }

 public void setId(Long id) {
	this.id = id;
 }

 public Long getQuestionId() {
	return questionId;
 }

 public void setQuestionId(Long questionId) {
	this.questionId = questionId;
 }

 public String getAnswer() {
	return answer;
 }

 public void setAnswer(String answer) {
	this.answer = answer;
 }

 public Integer getMarks() {
	return marks;
 }

 public void setMarks(Integer marks) {
	this.marks = marks;
 }

 

 public boolean isEvaluated() {
	return evaluated;
 }

 public void setEvaluated(boolean evaluated) {
	this.evaluated = evaluated;
 } 
 
 

 // getters and setters
}