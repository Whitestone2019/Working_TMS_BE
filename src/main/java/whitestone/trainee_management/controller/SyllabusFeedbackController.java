package whitestone.trainee_management.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import whitestone.trainee_management.models.SyllabusFeedback;
import whitestone.trainee_management.service.SyllabusFeedbackService;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin
public class SyllabusFeedbackController {

    private final SyllabusFeedbackService feedbackService;

    public SyllabusFeedbackController(
            SyllabusFeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

   
    @PostMapping("/trainee")
    public ResponseEntity<?> submitTraineeFeedback(
            @RequestParam String traineeId,
            @RequestParam String trainerId,
            @RequestParam Long syllabusId,
            @RequestParam String feedback) {

        SyllabusFeedback saved =
                feedbackService.submitTraineeFeedback(
                        traineeId,
                        trainerId,
                        syllabusId,
                        feedback
                );

        return ResponseEntity.ok(saved);
    }

    
    @PostMapping("/trainer")
    public ResponseEntity<?> submitTrainerFeedback(
            @RequestParam String traineeId,
            @RequestParam String trainerId,
            @RequestParam Long syllabusId,
            @RequestParam String feedback) {

        SyllabusFeedback saved =
                feedbackService.submitTrainerFeedback(
                        traineeId,
                        trainerId,
                        syllabusId,
                        feedback
                );

        return ResponseEntity.ok(saved);
    }

    
    @GetMapping("/data")
    public ResponseEntity<?> getFeedback(
            @RequestParam String traineeId,
            @RequestParam String trainerId,
            @RequestParam Long syllabusId) {

        Optional<SyllabusFeedback> feedback =
                feedbackService.getFeedback(
                        traineeId,
                        trainerId,
                        syllabusId
                );

        return feedback
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/assigned")
    public List<Map<String, Object>> getAssignedSyllabusWithFeedback(
            @RequestParam String traineeId,
            @RequestParam String managerId) {

        return feedbackService.getAssignedSyllabusWithFeedback(
                traineeId,
                managerId);
    }
    
    @GetMapping("/assigned-syllabus")
    public List<Map<String, Object>> getAssignedSyllabus(
            @RequestParam String traineeId) {

        return feedbackService.getAssignedSyllabusByTrainee(traineeId);
    }
    
    @GetMapping("/feedback-by-syllabus")
    public List<Map<String, Object>> getFeedbackBySyllabus(
            @RequestParam String traineeId,
            @RequestParam Long syllabusId) {

        return feedbackService
                .getFeedbackByTraineeAndSyllabus(
                        traineeId,
                        syllabusId);
    }

    @GetMapping("/trainee-feedback-by-syllabus")
    public List<Map<String, Object>> getOnlyTraineeFeedback(
            @RequestParam String traineeId,
            @RequestParam Long syllabusId) {

        return feedbackService.getOnlyTraineeFeedback(
                traineeId,
                syllabusId);
    }



}
