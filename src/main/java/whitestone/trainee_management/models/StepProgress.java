


package whitestone.trainee_management.models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.*;

@Entity 

@Table(name = "approved_checking")

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)

public class StepProgress extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @ManyToOne
    @JoinColumn(name = "subtopic_id", nullable = false)
    private SubTopic subTopic;
    
    
    @ManyToOne
    @JoinColumn(name = "userid", nullable = false)
    private User user;
    
    @Column(name = "deadline_mail_sent")
    private Boolean deadlineMailSent = false;

      
	@Column(nullable = false)
    private boolean complete = false;

    private boolean checker = false;

    private String review;
    
    @Column(name = "start_time_seconds")
    private Long starttimeSeconds = 0L;

    @Column(name = "end_time_seconds")
    private Long endtimeSeconds = 0L;

    
    @Column(name = "time_spent_seconds")
    private Long timeSpentSeconds = 0L;
    
    
    
    //Start Time and End Time 
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    @Column(name = "start_datetime")
    private LocalDateTime startDateTime;

    
	
	//  END DATE TIME
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    @Column(name = "end_datetime")
    private LocalDateTime endDateTime;

    


    
    
    public Long getId() {
        return id;
    }

    public boolean isDeadlineMailSent() {
		return deadlineMailSent;
	}

	public void setDeadlineMailSent(boolean deadlineMailSent) {
		this.deadlineMailSent = deadlineMailSent;
	}

	public Long getTimeSpentSeconds() {
		return timeSpentSeconds;
	}

	public void setTimeSpentSeconds(Long timeSpentSeconds) {
		this.timeSpentSeconds = timeSpentSeconds;
	}

	public Long getStarttimeSeconds() {
		return starttimeSeconds;
	}

	public void setStarttimeSeconds(Long starttimeSeconds) {
		this.starttimeSeconds = starttimeSeconds;
	}

	public Long getEndtimeSeconds() {
		return endtimeSeconds;
	}

	public void setEndtimeSeconds(Long endtimeSeconds) {
		this.endtimeSeconds = endtimeSeconds;
	}
	// Start and End Time
	
	public LocalDateTime getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(LocalDateTime startDateTime) {
		this.startDateTime = startDateTime;
	}

	public LocalDateTime getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(LocalDateTime endDateTime) {
		this.endDateTime = endDateTime;
	}

	

	public void setId(Long id) {
        this.id = id;
    }

    public SubTopic getSubTopic() {
        return subTopic;
    }

    public void setSubTopic(SubTopic subTopic) {
        this.subTopic = subTopic;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean isChecker() {
        return checker;
    }

    public void setChecker(boolean checker) {
        this.checker = checker;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
    

    
    public Boolean getDeadlineMailSent() {
		return deadlineMailSent;
	}

	public void setDeadlineMailSent(Boolean deadlineMailSent) {
		this.deadlineMailSent = deadlineMailSent;
	}


}