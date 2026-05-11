package whitestone.trainee_management.models;


import jakarta.persistence.*;

@Entity
@Table(name = "trainee_departments")
public class TraineeDepartment extends AuditModel{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(
        name = "trngid",
        referencedColumnName = "trngid",
        nullable=false
       
    )
    private User trainee;


    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    // getters & setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getTrainee() {
        return trainee;
    }

    public void setTrainee(User trainee) {
        this.trainee = trainee;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
