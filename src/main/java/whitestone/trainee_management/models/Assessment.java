package whitestone.trainee_management.models;



import jakarta.persistence.*;
import java.util.List;

@Entity

public class Assessment extends AuditModel {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "assessment_seq")
	@SequenceGenerator(
	    name = "assessment_seq",
	    sequenceName = "assessment_seq",
	    allocationSize = 1
	)
	private Long id;

    private String title;

//    private Boolean emailSent = false;
    
    @ElementCollection
    private List<Long> departmentIds;

    @ElementCollection
    private List<Long> syllabusIds;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "assessment_id")
    private List<Section> sections;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Long> getDepartmentIds() {
		return departmentIds;
	}

	public void setDepartmentIds(List<Long> departmentIds) {
		this.departmentIds = departmentIds;
	}

	public List<Long> getSyllabusIds() {
		return syllabusIds;
	}

	public void setSyllabusIds(List<Long> syllabusIds) {
		this.syllabusIds = syllabusIds;
	}

	public List<Section> getSections() {
		return sections;
	}

	public void setSections(List<Section> sections) {
		this.sections = sections;
	}

//	public Boolean getEmailSent() {
//		return emailSent;
//	}
//
//	public void setEmailSent(Boolean emailSent) {
//		this.emailSent = emailSent;
//	}
    
    
}