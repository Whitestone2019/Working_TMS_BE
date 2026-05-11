package whitestone.trainee_management.controller;
import java.util.HashMap;
import java.util.List;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import whitestone.trainee_management.models.*;
import whitestone.trainee_management.models.QuestionAnswer;
import whitestone.trainee_management.models.TraineeAssessmentAttempt;
import whitestone.trainee_management.repository.AssessmentRepository;
import whitestone.trainee_management.repository.QuestionAnswerRepository;
import whitestone.trainee_management.repository.TraineeAssessmentAttemptRepository;
import whitestone.trainee_management.repository.TraineeAssessmentRepository;
import whitestone.trainee_management.service.TraineeAssessmentAttemptService;

@RestController
@RequestMapping("/api/assessmenttest")
@CrossOrigin(origins = "*") 
public class TraineeAssessmentAttemptController {

@Autowired
 private final TraineeAssessmentAttemptService service;

 @Autowired
 private TraineeAssessmentAttemptRepository attemptRepo;
 
 
 @Autowired
 private AssessmentRepository assessmentRepo;
 
 @Autowired
 private QuestionAnswerRepository questionAnswerRepo;

 
 public TraineeAssessmentAttemptController(TraineeAssessmentAttemptService service) {
     this.service = service;
 }

 @PostMapping("/submit/{assessmentId}")
 public ResponseEntity<?> submitAssessment(
         @PathVariable Long assessmentId,
         @RequestBody TraineeAssessmentAttempt attempt
 ) {
     attempt.setAssessmentId(assessmentId);
     var savedAttempt = service.saveAttempt(attempt);
     return ResponseEntity.ok(savedAttempt);
 }
 
 
 @GetMapping("/assessment/check/{assessmentId}/{traineeId}")
 public ResponseEntity<?> checkSubmission(@PathVariable Long assessmentId, @PathVariable String traineeId) {
     boolean alreadySubmitted = service.hasAlreadySubmitted(traineeId, assessmentId);
     return ResponseEntity.ok(alreadySubmitted);
 }
 
// public int evaluateMCQ(Long attemptId) {
//
//     TraineeAssessmentAttempt attempt = attemptRepo.findById(attemptId).orElseThrow();
//     Assessment assessment = assessmentRepo.findById(attempt.getAssessmentId()).orElseThrow();
//
//     int score = 0;
//
//     for (SectionAnswer secAns : attempt.getAnswers()) {
//         for (QuestionAnswer qAns : secAns.getQuestions()) {
//
//             for (Section sec : assessment.getSections()) {
//                 for (Question q : sec.getQuestions()) {
//
//                     if (q.getId().equals(qAns.getQuestionId())) {
//
//                         // ✅ Only MCQ
//                         if ("MCQ".equalsIgnoreCase(q.getType())) {
//
//                             if (q.getCorrectAnswer() != null &&
//                                 q.getCorrectAnswer().equalsIgnoreCase(qAns.getAnswer())) {
//
//                                 qAns.setMarks(1); // fixed
//                                 score += 1;
//                             } else {
//                                 qAns.setMarks(0);
//                             }
//
//                             qAns.setEvaluated(true);
//                         }
//                     }
//                 }
//             }
//         }
//     }
//
//     attemptRepo.save(attempt); // save updated marks
//     return score;
// }
//
//
// //  MANUAL EVALUATION (TEXT + CODING)
// public void evaluateQuestion(Long id, int marks, String remarks) {
//
//     QuestionAnswer qa = questionAnswerRepo.findById(id).orElseThrow();
//
//     qa.setMarks(marks);
//     qa.setRemarks(remarks);
//     qa.setEvaluated(true);
//
//     questionAnswerRepo.save(qa);
// }
 
// @PostMapping("/mcq/{attemptId}")
// public ResponseEntity<?> evaluateMCQ(@PathVariable Long attemptId) {
//
//     int score = service.evaluateMCQ(attemptId);
//
//     Map<String, Object> response = new HashMap<>();
//     response.put("message", "MCQ Evaluated (Not Saved)");
//     response.put("score", score);
//
//     return ResponseEntity.ok(response);
// }
//
// //  MANUAL EVALUATION
// @PostMapping("/question")
// public ResponseEntity<?> evaluateQuestion(@RequestBody Map<String, Object> req) {
//
//     Long id = Long.valueOf(req.get("id").toString());
//     int marks = Integer.parseInt(req.get("marks").toString());
//     String remarks = req.get("remarks").toString();
//
//     service.evaluateQuestion(id, marks, remarks);
//
//     return ResponseEntity.ok("Saved Successfully");
// }
 
 @PostMapping("/evaluate-question")
 public ResponseEntity<?> evaluateQuestion(@RequestBody Map<String, Object> req) {

     Long id = Long.valueOf(req.get("id").toString());
     int marks = Integer.parseInt(req.get("marks").toString());
     String remarks = req.get("remarks").toString();

     QuestionAnswer qa = questionAnswerRepo.findById(id).orElseThrow();

     qa.setMarks(marks);
     
     qa.setEvaluated(true);

     questionAnswerRepo.save(qa);

     return ResponseEntity.ok("Saved");
 }
 
 @GetMapping("/score/{attemptId}")
 public int getTotalScore(@PathVariable Long attemptId) {

     TraineeAssessmentAttempt attempt =
             attemptRepo.findById(attemptId).orElseThrow();

     Assessment assessment =
             assessmentRepo.findById(attempt.getAssessmentId()).orElseThrow();

     return service.calculateTotalScore(attempt, assessment);
 }
 
 @GetMapping("/trainee/{traineeId}/assessments")
 public List<Map<String, Object>> getAttempts(@PathVariable String traineeId) {
     return service.getDetailedAttempts(traineeId);
 }
 
 @GetMapping("/submissions/{attemptId}")
 public TraineeAssessmentAttempt getSubmission(@PathVariable Long attemptId) {
     return attemptRepo.findById(attemptId).orElseThrow();
 }
 
 @GetMapping("/attempt/{attemptId}")
 public Map<String, Object> getAttemptById(@PathVariable Long attemptId) {
     return service.getAttemptDetailById(attemptId);
 }
 

 @GetMapping("/report/{traineeId}")
 public ResponseEntity<Map<String, Object>> getReport(
         @PathVariable String traineeId) {

     return ResponseEntity.ok(service.getTraineeReport(traineeId));
 }
}
