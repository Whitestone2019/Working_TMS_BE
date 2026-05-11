	package whitestone.trainee_management.models;
	
	import jakarta.persistence.*;
	import java.time.LocalDate;
	import java.time.LocalDateTime;
import java.util.List;
	
	@Entity
	@Table(name = "trainee_assessment_form")
	public class TraineeAssessment extends AuditModel {
	
	    @Id
	    private String assessmentId;
	
	    @ManyToOne
	    @JoinColumn(name = "trngid", referencedColumnName = "trngid")
	    private User user;
	
	    private LocalDate assessmentDate;
	    private String assessmentType;
	    private String marks;
	    private String maxMarks;
	    private Integer percentage;
	    private String remarks;
	    private String strengths;
	    private String improvements;
	    private String recommendations;
	    private LocalDateTime submittedAt;
	    private Integer currentStep = 0;
	    private boolean interviewDone;
	    
	    
	    public boolean isInterviewDone() {
			return interviewDone;
		}
		public void setInterviewDone(boolean interviewDone) {
			this.interviewDone = interviewDone;
		}


		private String subTopics;

		
		@Transient
		private List<Long> subTopicIds;
		
		
		

		public List<Long> getSubTopicIds() {
		    return subTopicIds;
		}

		public void setSubTopicIds(List<Long> subTopicIds) {
		    this.subTopicIds = subTopicIds;
		}

		
		
		public String getSubTopics() {
			return subTopics;
		}
		public void setSubTopics(String subTopics) {
			this.subTopics = subTopics;
		}
//		public void setSubTopics(List<SubTopic> subTopics) {
//			this.subTopics = subTopics;
//		}
		
		
		// ---- Getters & Setters ----
	    public String getAssessmentId() {
	        return assessmentId;
	    }
	    public void setAssessmentId(String assessmentId) {
	         this.assessmentId = assessmentId;
	    }
	
	    public User getUser() {
	        return user;
	    }
	    public void setUser(User user) {
	        this.user = user;
	    }
	
	    public LocalDate getAssessmentDate() {
	        return assessmentDate;
	    }
	    public void setAssessmentDate(LocalDate assessmentDate) {
	        this.assessmentDate = assessmentDate;
	    }
	
	    public String getAssessmentType() {
	        return assessmentType;
	    }
	    public void setAssessmentType(String assessmentType) {
	        this.assessmentType = assessmentType;
	    }
	
	    public String getMarks() {
	        return marks;
	    }
	    public void setMarks(String marks) {
	        this.marks = marks;
	    }
	
	    public String getMaxMarks() {
	        return maxMarks;
	    }
	    public void setMaxMarks(String maxMarks) {
	        this.maxMarks = maxMarks;
	    }
	
	    public Integer getPercentage() {
	        return percentage;
	    }
	    public void setPercentage(Integer percentage) {
	        this.percentage = percentage;
	    }
	
	    public String getRemarks() {
	        return remarks;
	    }
	    public void setRemarks(String remarks) {
	        this.remarks = remarks;
	    }
	
	    public String getStrengths() {
	        return strengths;
	    }
	    public void setStrengths(String strengths) {
	        this.strengths = strengths;
	    }
	
	    public String getImprovements() {
	        return improvements;
	    }
	    public void setImprovements(String improvements) {
	        this.improvements = improvements;
	    }
	
	    public String getRecommendations() {
	        return recommendations;
	    }
	    public void setRecommendations(String recommendations) {
	        this.recommendations = recommendations;
	    }
	
	    public LocalDateTime getSubmittedAt() {
	        return submittedAt;
	    }
	    public void setSubmittedAt(LocalDateTime submittedAt) {
	        this.submittedAt = submittedAt;
	    }
	
	
		public Integer getCurrentStep() {
			return currentStep;
		}
		public void setCurrentStep(Integer currentStep) {
			this.currentStep = currentStep;
		}
	}