package whitestone.trainee_management.controller;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import whitestone.trainee_management.models.TraineeAssessmentAttempt;
import whitestone.trainee_management.service.EvaluationService;

import java.util.HashMap;
import java.util.Map;

@RestController

@CrossOrigin
@RequestMapping("/api/assessmenttestcheck")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    // GET attempt detail
//    @GetMapping("/attempt/{id}")
//    public ResponseEntity<TraineeAssessmentAttempt> getAttempt(@PathVariable Long id) {
//        TraineeAssessmentAttempt attempt = evaluationService.getAttempt(id);
//        return ResponseEntity.ok(attempt);
//    }

    @PostMapping("/submit-evaluation/{attemptId}")
    public TraineeAssessmentAttempt submitEvaluation(
            @PathVariable Long attemptId,
            @RequestBody Map<String, Object> payload) {

        Map<String, Object> marksRaw = (Map<String, Object>) payload.get("marks");
        String review = (String) payload.get("review");

        Map<Long, Integer> marks = new HashMap<>();
        if (marksRaw != null) {
            marksRaw.forEach((key, value) -> {
                // Safe conversion from String/Double to Long/Integer
                Long qId = Long.parseLong(key);
                Integer score = ((Number) value).intValue(); 
                marks.put(qId, score);
            });
        }

        return evaluationService.submitEvaluation(attemptId, marks, review);
    }
    
//    @GetMapping("/trainee/result/{attemptId}")
//    public ResponseEntity<?> getResult(@PathVariable Long attemptId) {
//
//        return ResponseEntity.ok(
//        		evaluationService.getTraineeResult(attemptId)
//        );
//    }
    
    @GetMapping("/result")
    public Map<String, Object> getResult(
            @RequestParam String traineeId,
            @RequestParam Long assessmentId) {

        return evaluationService.getTraineeResult(traineeId, assessmentId);
    }
   
    
    @GetMapping("/evaluation/{attemptId}")
    public Map<String, Object> getEvaluationByAttempt(@PathVariable Long attemptId) {
        return evaluationService.getEvaluationByAttemptId(attemptId);
    }
    
    @PutMapping("/update-evaluation/{attemptId}")
    public TraineeAssessmentAttempt updateEvaluation(
            @PathVariable Long attemptId,
            @RequestBody Map<String, Object> payload) {

        Map<String, Object> marksRaw = (Map<String, Object>) payload.get("marks");
        String review = (String) payload.get("review");

        Map<Long, Integer> marks = new HashMap<>();

        if (marksRaw != null) {
            marksRaw.forEach((key, value) -> {
                Long qId = Long.parseLong(key);
                Integer score = ((Number) value).intValue();
                marks.put(qId, score);
            });
        }

        return evaluationService.submitEvaluation(attemptId, marks, review);
    }
}