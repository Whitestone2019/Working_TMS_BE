package whitestone.trainee_management.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import whitestone.trainee_management.models.Assessment;
import whitestone.trainee_management.models.Section;
import whitestone.trainee_management.models.SectionAnswer;
import whitestone.trainee_management.models.*;
import whitestone.trainee_management.models.TraineeAssessmentAttempt;
import whitestone.trainee_management.repository.AssessmentRepository;
import whitestone.trainee_management.repository.QuestionAnswerRepository;
import whitestone.trainee_management.repository.StepProgressRepository;
import whitestone.trainee_management.repository.SyllabusRepository;
import whitestone.trainee_management.repository.TraineeAssessmentAttemptRepository;
import whitestone.trainee_management.repository.TraineeDepartmentRepository;
import whitestone.trainee_management.repository.UserRepository;

@Service
public class TraineeAssessmentAttemptService {

//    private final TraineeAssessmentAttemptRepository repo;
	
	@Autowired
    private QuestionAnswerRepository questionAnswerRepo;

    @Autowired
    private AssessmentRepository assessmentRepo;

    @Autowired
    private TraineeAssessmentAttemptRepository attemptRepo;
    
    @Autowired
    private StepProgressRepository stepProgressRepository;
    
    @Autowired
    private UserRepository userRepository;
   
    @Autowired
	private TraineeDepartmentRepository traineedepartmentRepository;
    
    @Autowired
    private SyllabusRepository syllabusRepo;

    public TraineeAssessmentAttemptService(TraineeAssessmentAttemptRepository attemptRepository) {
        this.attemptRepository = attemptRepository;
    }

   
    
    @Autowired
    private TraineeAssessmentAttemptRepository attemptRepository;

    /**
     * Checks if the trainee has already submitted the assessment.
     * @param traineeId trainee's ID
     * @param assessmentId assessment's ID
     * @return true if already submitted, false otherwise
     */
    public boolean hasAlreadySubmitted(String traineeId, Long assessmentId) {
        return attemptRepository
                .findByTraineeIdAndAssessmentId(traineeId, assessmentId)
                .map(TraineeAssessmentAttempt::isSubmitted)
                .orElse(false);
    }

    public TraineeAssessmentAttempt saveAttempt(TraineeAssessmentAttempt attempt) {
    	
    	LocalTime now = LocalTime.now();

        if (now.isBefore(LocalTime.of(9, 0)) || now.isAfter(LocalTime.of(16, 0))) {
            throw new RuntimeException("Assessment can only be attempted between 10 AM to 4 PM");
        }

        attempt.setSubmitted(true); // mark as submitted
        attempt.setSubmittedAt(LocalDateTime.now());
        return attemptRepository.save(attempt);
    }
   
    public int evaluateMCQ(TraineeAssessmentAttempt attempt, Assessment assessment) {

        int correct = 0;
        int totalMcq = 0;

        for (SectionAnswer secAns : attempt.getAnswers()) {
            for (QuestionAnswer qAns : secAns.getQuestions()) {

                for (Section sec : assessment.getSections()) {
                    for (Question q : sec.getQuestions()) {

                        if (q.getId().equals(qAns.getQuestionId())) {

                            if (q.getOptions() != null && !q.getOptions().isEmpty()) {
                                totalMcq++;

                                if (q.getCorrectAnswer().equalsIgnoreCase(qAns.getAnswer())) {
                                    correct++;
                                }
                            }
                        }
                    }
                }
            }
        }

        //  convert to 5 marks
        return totalMcq == 0 ? 0 : (correct * 5) / totalMcq;
    }

    //  TOTAL SCORE (MCQ + MANUAL)
    public int calculateTotalScore(TraineeAssessmentAttempt attempt, Assessment assessment) {

        int mcqScore = evaluateMCQ(attempt, assessment);

        int manualMarks = 0;

        for (SectionAnswer sec : attempt.getAnswers()) {
            for (QuestionAnswer q : sec.getQuestions()) {

                // coding/text marks (manual)
                if (q.getMarks() != null) {
                    manualMarks += q.getMarks();
                }
            }
        }

        return mcqScore + manualMarks;
    }
    
    public List<Map<String, Object>> getDetailedAttempts(String traineeId) {

        List<TraineeAssessmentAttempt> attempts =
                attemptRepo.findByTraineeIdAndSubmittedTrue(traineeId);

        List<Map<String, Object>> response = new ArrayList<>();

        for (TraineeAssessmentAttempt attempt : attempts) {

            Map<String, Object> attemptData = new HashMap<>();
            
            attemptData.put("attemptId", attempt.getId());

            //  assessment fetch by ID
            Assessment assessment = assessmentRepo
                    .findById(attempt.getAssessmentId())
                    .orElse(null);

            if (assessment == null) continue;

            //  BOTH VALUES ADD
            attemptData.put("assessmentId", assessment.getId());
            attemptData.put("assessmentTitle", assessment.getTitle());

            List<Map<String, Object>> allQuestions = new ArrayList<>();

            for (Section section : assessment.getSections()) {

                for (Question question : section.getQuestions()) {

                    Map<String, Object> qData = new HashMap<>();

                    qData.put("section", section.getSectionName());
                    qData.put("sectionType", section.getType());
                    qData.put("question", question.getQuestion());
                    qData.put("options", question.getOptions());
                    qData.put("correctAnswer", question.getCorrectAnswer());

                    String selectedAnswer = null;

                    if (attempt.getAnswers() != null) {

                        for (SectionAnswer secAns : attempt.getAnswers()) {

                            for (QuestionAnswer qa : secAns.getQuestions()) {

                                if (qa.getQuestionId().equals(question.getId())) {
                                    selectedAnswer = qa.getAnswer();
                                }
                            }
                        }
                    }

                    qData.put("selectedAnswer", selectedAnswer);

                    allQuestions.add(qData);
                }
            }

            attemptData.put("questions", allQuestions);

            response.add(attemptData);
        }

        return response;
    }
    
    public Map<String, Object> getAttemptDetailById(Long attemptId) {

        TraineeAssessmentAttempt attempt = attemptRepo.findById(attemptId).orElse(null);

        if (attempt == null) {
            throw new RuntimeException("Attempt not found");
        }

        Map<String, Object> response = new HashMap<>();

        //  fetch assessment
        Assessment assessment = assessmentRepo
                .findById(attempt.getAssessmentId())
                .orElse(null);

        if (assessment == null) {
            throw new RuntimeException("Assessment not found");
        }

        response.put("attemptId", attempt.getId());
        response.put("assessmentId", assessment.getId());
        response.put("assessmentTitle", assessment.getTitle());

        List<Map<String, Object>> allQuestions = new ArrayList<>();

        //  OPTIMIZED answer map
        Map<Long, String> answerMap = new HashMap<>();

        if (attempt.getAnswers() != null) {
            for (SectionAnswer secAns : attempt.getAnswers()) {
                for (QuestionAnswer qa : secAns.getQuestions()) {
                    answerMap.put(qa.getQuestionId(), qa.getAnswer());
                }
            }
        }

        //  loop sections → questions
        for (Section section : assessment.getSections()) {

            for (Question question : section.getQuestions()) {

                Map<String, Object> qData = new HashMap<>();
                
                qData.put("questionId", question.getId());

                qData.put("section", section.getSectionName());
                qData.put("sectionType", section.getType());
                qData.put("question", question.getQuestion());
                qData.put("options", question.getOptions());
                qData.put("correctAnswer", question.getCorrectAnswer());

                String selectedAnswer = answerMap.get(question.getId());
                qData.put("selectedAnswer", selectedAnswer);

                allQuestions.add(qData);
            }
        }

        response.put("questions", allQuestions);

        return response;
    }

    
    public Map<String, Object> getTraineeReport(String traineeId) {

        Map<String, Object> response = new HashMap<>();
        Optional<User> traineeOpt = userRepository.findByTrngid(traineeId);

        if (traineeOpt.isEmpty()) {
            return response;
        }

        User trainee = traineeOpt.get();

        // =========================
        //  GET TRAINEE DEPARTMENTS
        // =========================
        List<Long> traineeDeptIds =
                traineedepartmentRepository.findDepartmentIdsByTraineeTrngid(traineeId);

        // =========================
        //  SYLLABUS + STEP PROGRESS (FILTERED)
        // =========================
        List<Syllabus> syllabusList = syllabusRepo.findAll();
        List<Map<String, Object>> syllabusData = new ArrayList<>();

        for (Syllabus s : syllabusList) {

            // -------------------------
            //  DEPARTMENT FILTER (MAIN FIX)
            // -------------------------
            List<Map<String, Object>> departmentsList = new ArrayList<>();

            if (s.getDepartments() != null) {
                for (Department dept : s.getDepartments()) {
                    if (traineeDeptIds.contains(dept.getId())) {
                        Map<String, Object> deptMap = new LinkedHashMap<>();
                        deptMap.put("departmentId", dept.getId());
                        deptMap.put("name", dept.getName());
                        departmentsList.add(deptMap);
                    }
                }
            }

            //  Skip if no matching department
            if (departmentsList.isEmpty()) continue;

            Map<String, Object> sMap = new LinkedHashMap<>();
            sMap.put("id", s.getId());
            sMap.put("title", s.getTitle());
            sMap.put("topic", s.getTopic());
            sMap.put("duration", s.getDurationInDays());
            sMap.put("departments", departmentsList); //  add departments

            // -------------------------
            //  SUBTOPICS + PROGRESS
            // -------------------------
            List<Map<String, Object>> subTopicsList = new ArrayList<>();

            for (SubTopic st : s.getSubTopics()) {

                Map<String, Object> stMap = new LinkedHashMap<>();
                stMap.put("subTopicId", st.getId());
                stMap.put("name", st.getName());
                stMap.put("stepNumber", st.getStepNumber());
                stMap.put("description", st.getDescription());

                List<StepProgress> progressList =
                        stepProgressRepository.findByUser_UseridAndSubTopic_Id(
                                trainee.getUserid(), st.getId()
                        );

                List<Map<String, Object>> progressResponse = new ArrayList<>();

                for (StepProgress sp : progressList) {

                    Map<String, Object> pMap = new LinkedHashMap<>();
                    pMap.put("stepProgressId", sp.getId());
                    pMap.put("complete", sp.isComplete());
                    pMap.put("checker", sp.isChecker());
                    pMap.put("review", sp.getReview());
                    pMap.put("startDateTime", sp.getStartDateTime());
                    pMap.put("endDateTime", sp.getEndDateTime());

                    progressResponse.add(pMap);
                }

                stMap.put("stepProgress", progressResponse);
                subTopicsList.add(stMap);
            }

            sMap.put("subTopics", subTopicsList);
            syllabusData.add(sMap);
        }

        // =========================
        //  WEEKLY ASSESSMENTS
        // =========================
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        List<TraineeAssessmentAttempt> attempts =
                attemptRepo.findByTraineeId(traineeId);

        List<Map<String, Object>> assessments = new ArrayList<>();

        Map<Long, Assessment> assessmentMap = assessmentRepo.findAll()
                .stream()
                .collect(Collectors.toMap(Assessment::getId, a -> a));

        for (TraineeAssessmentAttempt a : attempts) {

            if (a.getSubmittedAt() == null) continue;

            LocalDate submittedDate = a.getSubmittedAt().toLocalDate();

            if (submittedDate.isBefore(startOfWeek) || submittedDate.isAfter(endOfWeek)) {
                continue;
            }

            Map<String, Object> aMap = new LinkedHashMap<>();

            Integer marks = a.getTotalMarks();
            Integer overallMarks = a.getOverallMarks();

            aMap.put("assessmentId", a.getAssessmentId());
            aMap.put("marks", marks != null ? marks : 0);
            aMap.put("overallMarks", overallMarks != null ? overallMarks : 0);
            aMap.put("remarks", a.getRemarks());
            aMap.put("submittedAt", a.getSubmittedAt());

            Long assessId = Long.valueOf(a.getAssessmentId());
            Assessment assessment = assessmentMap.get(assessId);

            if (assessment != null) {
                aMap.put("assessmentName", assessment.getTitle());

                List<String> sectionTypes = assessment.getSections()
                        .stream()
                        .map(Section::getType)
                        .distinct()
                        .toList();

                aMap.put("sectionTypes", sectionTypes);
            } else {
                aMap.put("assessmentName", "Not Found");
            }

            aMap.put("answers", a.getAnswers());
            assessments.add(aMap);
        }

        // =========================
        //  FINAL RESPONSE
        // =========================
        response.put("traineeId", traineeId);
        response.put("syllabus", syllabusData);
        response.put("assessments", assessments);
        response.put("totalAssessmentsThisWeek", assessments.size());

        return response;
    }
    
    
    
}