package whitestone.trainee_management.models;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.*;

@Entity
@Table(name = "SUB_TOPIC")
@JsonIdentityInfo(
	    generator = ObjectIdGenerators.PropertyGenerator.class,
	    property = "id"
	)
public class SubTopic extends AuditModel {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sub_topic_seq")
    @SequenceGenerator(
        name = "sub_topic_seq",
        sequenceName = "sub_topic_seq",
        allocationSize = 1
    )
	private Long id;

	private int stepNumber = 0;

	private String name;

	private String description;

	private String filePath;
	
	@Transient
	private MultipartFile file;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "syllabus_id", nullable = false)

	
	private Syllabus syllabus;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getStepNumber() {
		return stepNumber;
	}

	public void setStepNumber(int stepNumber) {
		this.stepNumber = stepNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public Syllabus getSyllabus() {
		return syllabus;
	}

	public void setSyllabus(Syllabus syllabus) {
		this.syllabus = syllabus;
	}


}
