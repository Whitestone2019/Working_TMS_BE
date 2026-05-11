package whitestone.trainee_management.models;

import jakarta.persistence.*;

@Entity
@Table(name = "schedule_user_map")
public class ScheduleTraineeMap extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to interview schedule
    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private InterviewSchedule interviewSchedule;

    // Link to user (instead of trainee)
    @ManyToOne
    @JoinColumn(name = "trngid", referencedColumnName = "trngid")
    private User user;

	@Column(name = "event_id")
    private String eventId;

	@Column(name = "role_rvsp")
	private String roleRvsp = "Trainee";
	
	
	
    public String getRoleRvsp() {
		return roleRvsp;
	}

	public void setRoleRvsp(String roleRvsp) {
		this.roleRvsp = roleRvsp;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getRsvpStatus() {
		return rsvpStatus;
	}

	public void setRsvpStatus(String rsvpStatus) {
		this.rsvpStatus = rsvpStatus;
	}

	// RSVP response (accepted, declined, tentative, needsAction)
	@Column(name = "rsvp_status")
    private String rsvpStatus = "PENDING";
    
  
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InterviewSchedule getInterviewSchedule() {
        return interviewSchedule;
    }

    public void setInterviewSchedule(InterviewSchedule interviewSchedule) {
        this.interviewSchedule = interviewSchedule;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
