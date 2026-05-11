package whitestone.trainee_management.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import whitestone.trainee_management.models.SectionAnswer;
import whitestone.trainee_management.models.TraineeAssessmentAttempt;
import whitestone.trainee_management.repository.AssessmentRepository;
import whitestone.trainee_management.repository.TraineeAssessmentAttemptRepository;
import whitestone.trainee_management.models.*;

@Service
public class EvaluationService {

	
    private final TraineeAssessmentAttemptRepository attemptRepo;
    
    
    private final AssessmentRepository assessmentRepo;

	
    public EvaluationService(TraineeAssessmentAttemptRepository attemptRepo,AssessmentRepository assessmentRepo) {
        this.attemptRepo = attemptRepo;
        this.assessmentRepo=assessmentRepo;
    }

    public TraineeAssessmentAttempt getAttempt(Long attemptId) {
        return attemptRepo.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));
    }
//    @Transactional
//    public TraineeAssessmentAttempt submitEvaluation(
//            Long attemptId,
//            Map<Long, Integer> marks,
//            String review) {
//
//        TraineeAssessmentAttempt attempt = getAttempt(attemptId);
//        List<SectionAnswer> sections = attempt.getAnswers();
//
//        int totalCalculated = 0;
//
//        System.out.println("Incoming Marks from Frontend: " + marks);
//
//        for (SectionAnswer section : sections) {
//            for (QuestionAnswer q : section.getQuestions()) {
//
//                Long qId = q.getQuestionId();
//
//                System.out.println("Processing QuestionId: " + qId);
//
//                if (marks != null && qId != null && marks.containsKey(qId)) {
//
//                    Integer m = marks.get(qId);
//
//                    System.out.println("MATCH FOUND ✅ → " + qId + " = " + m);
//
//                    q.setMarks(m);
//                    q.setEvaluated(true);
//                    totalCalculated += m;
//
//                } else {
//
//                    System.out.println("NO MATCH ❌ → " + qId + " → setting 0");
//
//                    //  Default marks (important fix)
//                    q.setMarks(0);
//                    q.setEvaluated(true);
//
//                    // MCQ case (optional auto scoring)
//                    if (q.getAnswer() != null && q.getAnswer().equalsIgnoreCase("correct")) {
//                        totalCalculated += 1; 
//                    }
//                }
//            }
//        }
//
//        //  Review save
//        attempt.setRemarks(review);
//
//        //  Submit flag
//        attempt.setSubmitted(true);
//
//        //  Agar future me field add kare:
//        // attempt.setTotalMarks(totalCalculated);
//
//        System.out.println("Final Total Marks: " + totalCalculated);
//
//        return attemptRepo.saveAndFlush(attempt);
//    }
    @Transactional
    public TraineeAssessmentAttempt submitEvaluation(
            Long attemptId,
            Map<Long, Integer> marks,
            String review) {

        TraineeAssessmentAttempt attempt = getAttempt(attemptId);

        Assessment assessment = assessmentRepo
                .findById(attempt.getAssessmentId())
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        List<SectionAnswer> sections = attempt.getAnswers();

        int totalCalculated = 0;
        int overallMarks = 0;

        System.out.println("Incoming Marks from Frontend: " + marks);

        for (SectionAnswer secAns : sections) {

            //  SECTION TYPE FETCH
            Section actualSection = assessment.getSections()
                    .stream()
                    .filter(s -> s.getSectionName().equalsIgnoreCase(secAns.getSectionName()))
                    .findFirst()
                    .orElse(null);

            if (actualSection == null) continue;

            String type = actualSection.getType(); //  correct place

            for (QuestionAnswer qAns : secAns.getQuestions()) {

                Long qId = qAns.getQuestionId();

                //  find actual question
                Question actualQuestion = actualSection.getQuestions()
                        .stream()
                        .filter(q -> q.getId().equals(qId))
                        .findFirst()
                        .orElse(null);

                if (actualQuestion == null) continue;

                //  1. MCQ AUTO
                if ("MCQ".equalsIgnoreCase(type)) {
                	overallMarks += 1;

                    if (qAns.getAnswer() != null &&
                            qAns.getAnswer().equalsIgnoreCase(actualQuestion.getCorrectAnswer())) {

                        qAns.setMarks(1);
                        totalCalculated += 1;

                    } else {
                        qAns.setMarks(0);
                    }

                    qAns.setEvaluated(true);
                }

                //  2. CODING / TEXT → FRONTEND MARKS
//                else {
//
//                    if (marks != null && marks.containsKey(qId)) {
//
//                        Integer m = marks.get(qId);
//
//                        qAns.setMarks(m);
//                        totalCalculated += m;
//
//                    } else {
//                        qAns.setMarks(0);
//                    }
//
//                    qAns.setEvaluated(true);
//                }
                
                else if ("TEXT".equalsIgnoreCase(type)) {

                    overallMarks += 5;

                    if (marks != null && marks.containsKey(qId)) {
                        Integer m = marks.get(qId);
                        qAns.setMarks(m);
                        totalCalculated += m;
                    } else {
                        qAns.setMarks(0);
                    }
                }

                
                //  CODING
              
                else if ("CODING".equalsIgnoreCase(type)) {

                    overallMarks += 10;

                    if (marks != null && marks.containsKey(qId)) {
                        Integer m = marks.get(qId);
                        qAns.setMarks(m);
                        totalCalculated += m;
                    } else {
                        qAns.setMarks(0);
                    }
                }

                qAns.setEvaluated(true);
            }
        
            
        }

        attempt.setRemarks(review);
        attempt.setTotalMarks(totalCalculated);
        attempt.setSubmitted(true);
attempt.setOverallMarks(overallMarks);
        return attemptRepo.save(attempt);
    }    
    
//    public Map<String, Object> getTraineeResult(Long attemptId) {
//
//        TraineeAssessmentAttempt attempt = attemptRepo.findById(attemptId)
//                .orElseThrow(() -> new RuntimeException("Attempt not found"));
//
//        Assessment assessment = assessmentRepo
//                .findById(attempt.getAssessmentId())
//                .orElseThrow(() -> new RuntimeException("Assessment not found"));
//
//        Map<String, Object> response = new HashMap<>();
//
//        response.put("attemptId", attempt.getId());
//        response.put("assessmentId", assessment.getId());
//        response.put("assessmentTitle", assessment.getTitle());
//        response.put("totalMarks", attempt.getTotalMarks());
//        response.put("remarks", attempt.getRemarks());
//
//        List<Map<String, Object>> questionList = new ArrayList<>();
//
//        for (Section section : assessment.getSections()) {
//
//            for (Question question : section.getQuestions()) {
//
//                Map<String, Object> qData = new HashMap<>();
//
//                qData.put("questionId", question.getId());
//                qData.put("question", question.getQuestion());
//                qData.put("section", section.getSectionName());
//                qData.put("correctAnswer", question.getCorrectAnswer());
//
//                String selectedAnswer = null;
//                Integer marks = 0;
//
//                // 🔥 match trainee answer
//                for (SectionAnswer secAns : attempt.getAnswers()) {
//                    for (QuestionAnswer qa : secAns.getQuestions()) {
//
//                        if (qa.getQuestionId().equals(question.getId())) {
//                            selectedAnswer = qa.getAnswer();
//                            marks = qa.getMarks(); // ✅ important
//                        }
//                    }
//                }
//
//                qData.put("selectedAnswer", selectedAnswer);
//                qData.put("marks", marks);
//
//                // optional
//                boolean isCorrect = selectedAnswer != null &&
//                        selectedAnswer.equalsIgnoreCase(question.getCorrectAnswer());
//
//                qData.put("isCorrect", isCorrect);
//
//                questionList.add(qData);
//            }
//        }
//
//        response.put("questions", questionList);
//
//        return response;
//    }
    
    
    
    public Map<String, Object> getTraineeResult(String traineeId, Long assessmentId) {

        TraineeAssessmentAttempt attempt = attemptRepo
                .findByTraineeIdAndAssessmentIdAndSubmittedTrue(traineeId, assessmentId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        Assessment assessment = assessmentRepo
                .findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        Map<String, Object> response = new HashMap<>();

        response.put("attemptId", attempt.getId());
        response.put("assessmentId", assessment.getId());
        response.put("assessmentTitle", assessment.getTitle());
        response.put("totalMarks", attempt.getTotalMarks());
        response.put("remarks", attempt.getRemarks());

        List<Map<String, Object>> questionList = new ArrayList<>();

        for (Section section : assessment.getSections()) {
            for (Question question : section.getQuestions()) {

                Map<String, Object> qData = new HashMap<>();

                qData.put("questionId", question.getId());
                qData.put("question", question.getQuestion());
                qData.put("section", section.getSectionName());
                qData.put("correctAnswer", question.getCorrectAnswer());
                

                String selectedAnswer = null;
                Integer marks = 0;
                boolean evaluated = false; 

                for (SectionAnswer secAns : attempt.getAnswers()) {
                    for (QuestionAnswer qa : secAns.getQuestions()) {

                        if (qa.getQuestionId().equals(question.getId())) {
                            selectedAnswer = qa.getAnswer();
                            marks = qa.getMarks();
                            evaluated=qa.isEvaluated();
                        }
                    }
                }

                qData.put("selectedAnswer", selectedAnswer);
                qData.put("marks", marks);
                qData.put("evaluated", evaluated);

                boolean isCorrect = selectedAnswer != null &&
                        selectedAnswer.equalsIgnoreCase(question.getCorrectAnswer());

                qData.put("isCorrect", isCorrect);

                questionList.add(qData);
            }
        }

        response.put("questions", questionList);

        return response;
    }
    
    
    public Map<String, Object> getEvaluationByAttemptId(Long attemptId) {

        TraineeAssessmentAttempt attempt = attemptRepo.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        Assessment assessment = assessmentRepo
                .findById(attempt.getAssessmentId())
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        Map<String, Object> response = new HashMap<>();

        response.put("attemptId", attempt.getId());
        response.put("assessmentTitle", assessment.getTitle());
        response.put("remarks", attempt.getRemarks());

        List<Map<String, Object>> questionList = new ArrayList<>();

        for (Section section : assessment.getSections()) {
            for (Question question : section.getQuestions()) {

                Map<String, Object> qData = new HashMap<>();

                qData.put("questionId", question.getId());
                qData.put("question", question.getQuestion());
                qData.put("section", section.getSectionName());
                qData.put("correctAnswer", question.getCorrectAnswer());
                qData.put("sectionType", section.getType());

                String selectedAnswer = null;
                Integer marks = 0;
                boolean evaluted=false;

                for (SectionAnswer secAns : attempt.getAnswers()) {
                    for (QuestionAnswer qa : secAns.getQuestions()) {

                        if (qa.getQuestionId().equals(question.getId())) {
                            selectedAnswer = qa.getAnswer();
                            marks = qa.getMarks();
                            evaluted=qa.isEvaluated();
                            
                        }
                    }
                }

                qData.put("selectedAnswer", selectedAnswer);
                qData.put("marks", marks);
                qData.put("submitted", evaluted);

                questionList.add(qData);
            }
        }

        response.put("questions", questionList);

        return response;
    }
}