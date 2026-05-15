package whitestone.trainee_management.models;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "syllabus_deadline_status",
       uniqueConstraints = @UniqueConstraint(columnNames = {"trainee_id", "syllabus_id"}))
public class SyllabusDeadlineStatus extends AuditModel {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "syllabus_deadline_status_seq")
	@SequenceGenerator(
	    name = "syllabus_deadline_status_seq",
	    sequenceName = "syllabus_deadline_status_seq",
	    allocationSize = 1
	)
	private Long id;

    @Column(name = "trainee_id")
    private String traineeId;

    @Column(name = "manager_id")
    private String managerId;

    @Column(name = "syllabus_id")
    private Long syllabusId;

    private LocalDateTime deadlineDate;

    private long delayDays;

    private boolean mailSent;
    
    private LocalDateTime lastMailSentAt;

    // Getters & Setters

    public Long getId() { return id; }

    public String getTraineeId() { return traineeId; }
    public void setTraineeId(String traineeId) { this.traineeId = traineeId; }

    public String getManagerId() { return managerId; }
    public void setManagerId(String managerId) { this.managerId = managerId; }

    public Long getSyllabusId() { return syllabusId; }
    public void setSyllabusId(Long syllabusId) { this.syllabusId = syllabusId; }

    public LocalDateTime getDeadlineDate() { return deadlineDate; }
    public void setDeadlineDate(LocalDateTime deadlineDate) { this.deadlineDate = deadlineDate; }

    public long getDelayDays() { return delayDays; }
    public void setDelayDays(long delayDays) { this.delayDays = delayDays; }

    public boolean isMailSent() { return mailSent; }
    public void setMailSent(boolean mailSent) { this.mailSent = mailSent; }
    
    public LocalDateTime getLastMailSentAt() { return lastMailSentAt; }
    public void setLastMailSentAt(LocalDateTime lastMailSentAt) { this.lastMailSentAt = lastMailSentAt; }
    
}