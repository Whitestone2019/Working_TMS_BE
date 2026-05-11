package whitestone.trainee_management.models;

//SectionAnswer.java


import jakarta.persistence.*;
import java.util.List;

@Entity
public class SectionAnswer extends AuditModel {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 private Long sectionId;
 private String sectionName;
 
 private int timeSpent; // in seconds


 @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
 @JoinColumn(name = "section_answer_id")
 private List<QuestionAnswer> questions;

 public Long getId() {
	return id;
 }

 public void setId(Long id) {
	this.id = id;
 }

 public Long getSectionId() {
	return sectionId;
 }

 public void setSectionId(Long sectionId) {
	this.sectionId = sectionId;
 }

 public String getSectionName() {
	return sectionName;
 }

 public void setSectionName(String sectionName) {
	this.sectionName = sectionName;
 }

 public int getTimeSpent() { return timeSpent; }
 
 public void setTimeSpent(int timeSpent) { this.timeSpent = timeSpent; }
 
 public List<QuestionAnswer> getQuestions() {
	return questions;
 }

 public void setQuestions(List<QuestionAnswer> questions) {
	this.questions = questions;
 }

 
 // getters and setters
 
 
}