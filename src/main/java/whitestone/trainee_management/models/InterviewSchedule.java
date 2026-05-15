package whitestone.trainee_management.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "interview_schedule")
public class InterviewSchedule extends AuditModel {

//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private Long scheduleId;
	
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "interview_schedule_seq")
    @SequenceGenerator(
        name = "interview_schedule_seq",
        sequenceName = "interview_schedule_seq",
        allocationSize = 1
    )
    private Long scheduleId;

	private LocalDate date;
	private LocalTime time;
	private String interviewType;
	private String location;
	private Integer duration;
	private String notes;
	private String meetingLink;


//	@ManyToOne
//	@JoinColumn(name = "manager_id")
//	private User managerId;
	
//	public User getManagerId() {
//		return managerId;
//	}
//
//	public void setManagerId(User managerId) {
//		this.managerId = managerId;
//	}

	@ManyToMany
	@JoinTable(
	    name = "interview_schedule_managers",
	    joinColumns = @JoinColumn(name = "schedule_id"),
	    inverseJoinColumns = @JoinColumn(name = "manager_id")
	)
	private List<User> managers = new ArrayList<>();
	
	
	
	
	public List<User> getManagers() {
		return managers;
	}

	public void setManagers(List<User> managers) {
		this.managers = managers;
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

	public Long getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(Long scheduleId) {
		this.scheduleId = scheduleId;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public String getInterviewType() {
		return interviewType;
	}

	public void setInterviewType(String interviewType) {
		this.interviewType = interviewType;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}


//	public String getMeetingLink() {
//		return meetingLink;
//	}
//
//	public void setMeetingLink(String meetingLink) {
//		this.meetingLink = meetingLink;
//	}

	
}
