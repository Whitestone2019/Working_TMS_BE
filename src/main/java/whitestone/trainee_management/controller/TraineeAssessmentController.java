package whitestone.trainee_management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import whitestone.trainee_management.models.TraineeAssessment;
import whitestone.trainee_management.payload.ApiResponse;
import whitestone.trainee_management.service.TraineeAssessmentService;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class TraineeAssessmentController {

    @Autowired
    private TraineeAssessmentService service;

    @PostMapping("/assessments/create/{empId}")
    public ApiResponse create(@RequestBody TraineeAssessment assessment, @PathVariable String empId) {
        return service.createAssessment(assessment, empId);
    }

    @GetMapping("/assessments/all")
    public ApiResponse getAll() {
        return service.getAllAssessments();
    }

    @GetMapping("/assessments/trainee/{id}")
    public ApiResponse getById(@PathVariable String id) {
        return service.getAssessmentByEmpId(id);
    }
    
    @GetMapping("/summary")
    public ApiResponse getSummary() {
        return service.getTraineeSummary();
    }
    
    @PutMapping("/assessments/update/{id}")
    public ApiResponse update(@PathVariable String id, @RequestBody TraineeAssessment assessment) {
        return service.updateAssessment(id, assessment);
    }

    @DeleteMapping("/assessments/delete/{id}")
    public ApiResponse delete(@PathVariable String id) {
        return service.deleteAssessment(id);
    }
}
