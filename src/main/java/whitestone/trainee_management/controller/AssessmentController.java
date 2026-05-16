package whitestone.trainee_management.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import whitestone.trainee_management.models.*;
import whitestone.trainee_management.service.*;
//
//@RestController
//@CrossOrigin(origins = "*")
//@RequestMapping("/api/assessment/test")
//public class AssessmentController {
//
//    @Autowired
//    private AssessmentService assessmentService;
//
//    @PostMapping("/create")
//    public Assessment createAssessment(@RequestBody Assessment assessment) {
//        return assessmentService.saveAssessment(assessment);
//    }
//
//    @GetMapping("/all")
//    public List<Assessment> getAllAssessments() {
//        return assessmentService.getAllAssessments();
//    }
//
//    @GetMapping("/{id}")
//    public Assessment getAssessment(@PathVariable Long id) {
//        return assessmentService.getAssessment(id);
//    }
//    
//    @PutMapping("/update/{id}")
//    public Assessment updateAssessment(
//            @PathVariable Long id,
//            @RequestBody Assessment assessment) {
//
//        return assessmentService.updateAssessment(id, assessment);
//    }
//
//    @DeleteMapping("/{id}")
//    public String deleteAssessment(@PathVariable Long id) {
//    	assessmentService.deleteAssessment(id);
//        return "Assessment deleted successfully";
//    }
//    
//    @GetMapping("/assessments/{departmentId}")
//    public List<Assessment> getAssessments(@PathVariable Long departmentId){
//
//        return assessmentService.getAssessmentByDepartment(departmentId);
//
//    }
//    
//    @GetMapping("/assessments")
//    public List<Assessment> getAssessments(@RequestParam List<Long> departmentIds){
//
//        return assessmentService.getAssessmentByDepartments(departmentIds);
//
//    }
//    
//    @GetMapping("/assessment/{id}")
//    public Assessment getAssessmentById(@PathVariable Long id) {
//        return assessmentService.getAssessmentById(id);
//    }
//}

@RestController
@RequestMapping("/api/assessment/test")
@CrossOrigin
public class AssessmentController {

    @Autowired
    private AssessmentService service;

    //  CREATE
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Assessment assessment) {
        return ResponseEntity.ok(service.create(assessment));
    }

    //  GET ALL
    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    //  DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Deleted Successfully");
    }
    
 //  GET ASSESSMENT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Assessment> getAssessmentById(@PathVariable Long id) {

        Assessment assessment = service.getById(id);

        return ResponseEntity.ok(assessment);
    }

    //  UPDATE
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody Assessment assessment) {
        return ResponseEntity.ok(service.update(id, assessment));
    }
  
    @GetMapping("/trainee/{traineeId}")
    public ResponseEntity<List<Assessment>> getAssessmentsForTrainee(
            @PathVariable String traineeId,
            @RequestParam List<Long> departmentIds) {

        return ResponseEntity.ok(
                service.getAssessmentsForTrainee(traineeId, departmentIds)
        );
    }
    
    @GetMapping("/assessment/summary/{userId}")
    public Map<String, Object> getSummary(
            @PathVariable String  userId,
            @RequestParam List<Long> departmentIds) {

        return service.getAssessmentSummary(userId, departmentIds);
    }
  }