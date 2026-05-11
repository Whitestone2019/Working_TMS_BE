package whitestone.trainee_management.models;


import jakarta.persistence.*;

@Entity
public class TestCase extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String input;

    @Column(length = 2000)
    private String output;

    public TestCase(){}

    public Long getId() {
        return id;
    }

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
