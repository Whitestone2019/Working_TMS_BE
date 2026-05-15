package whitestone.trainee_management.models;



import jakarta.persistence.*;
import java.util.List;

@Entity

public class Question extends AuditModel{

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "question_seq")
	@SequenceGenerator(
	    name = "question_seq",
	    sequenceName = "question_seq",
	    allocationSize = 1
	)
	private Long id;
	
    private String question;

    @ElementCollection
    private List<String> options;

    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String correctAnswer;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	public String getCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
	}
    
}