package whitestone.trainee_management.models;

import jakarta.persistence.*;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "syllabus")
@JsonIdentityInfo(
	    generator = ObjectIdGenerators.PropertyGenerator.class,
	    property = "id"
	)
public class Syllabus extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String topic;
    
    private Integer durationInDays;

    @ManyToMany
   @JoinTable(
           name = "syllabus_trainers",
           joinColumns = @JoinColumn(name = "syllabus_id"),
           inverseJoinColumns = @JoinColumn(name = "trainer_id")
    )
    private Set<User> managers;
    
//    @ManyToOne(fetch = FetchType.EAGER)  // LAZY → EAGER
//    @JoinColumn(name = "trainer_id")
//    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//    private User manager;


    // Many-to-Many with Departments
    @ManyToMany
    @JoinTable(
            name = "syllabus_departments",
            joinColumns = @JoinColumn(name = "syllabus_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    private Set<Department> departments;
    

	@OneToMany(
            mappedBy = "syllabus",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )

    private List<SubTopic> subTopics;
    
    
    
    // Getters and Setters
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
    
    public Set<User> getManagers() {
        return managers;
    }

    public void setManagers(Set<User> managers) {
        this.managers = managers;
   }
    
    

    public Set<Department> getDepartments() {
        return departments;
    }

   

//	public User getManager() {
//		return manager;
//	}
//
//	public void setManager(User manager) {
//		this.manager = manager;
//	}

	public void setDepartments(Set<Department> departments) {
        this.departments = departments;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
    public Integer getDurationInDays() {
		return durationInDays;
	}

	public void setDurationInDays(Integer durationInDays) {
		this.durationInDays = durationInDays;
	}
    public List<SubTopic> getSubTopics() {
        return subTopics;
    }

    public void setSubTopics(List<SubTopic> subTopics) {
        this.subTopics = subTopics;
        if (subTopics != null) {
            subTopics.forEach(sub -> sub.setSyllabus(this));
        }
    }
    
}
