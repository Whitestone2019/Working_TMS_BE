package whitestone.trainee_management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import whitestone.trainee_management.models.Department;
import whitestone.trainee_management.service.TraineeDepartmentService;

@RestController
@RequestMapping("/api/trainee-departments")
@CrossOrigin
public class TraineeDepartmentController {

    private final TraineeDepartmentService traineeDepartmentService;

    // Manual Constructor Injection 
    public TraineeDepartmentController(TraineeDepartmentService traineeDepartmentService) {
        this.traineeDepartmentService = traineeDepartmentService;
    }

  
    @PostMapping("/{trngid}")
    public ResponseEntity<String> assignDepartments(
            @PathVariable String trngid,
            @RequestBody List<Long> departmentIds
    ) {

        traineeDepartmentService.assignDepartments(trngid, departmentIds);

        return ResponseEntity.ok("Departments assigned successfully");
    }

   
    @PutMapping("/{trngid}")
    public ResponseEntity<String> updateDepartments(
            @PathVariable String trngid,
            @RequestBody List<Long> departmentIds
    ) {

        traineeDepartmentService.updateTraineeDepartments(trngid, departmentIds);

        return ResponseEntity.ok("Departments updated successfully");
    }

    
    @DeleteMapping("/{trngid}/{departmentId}")
    public ResponseEntity<String> removeDepartment(
            @PathVariable String trngid,
            @PathVariable Long departmentId
    ) {

        traineeDepartmentService.removeDepartment(trngid, departmentId);

        return ResponseEntity.ok("Department removed successfully");
    }

    
//    @DeleteMapping("/remove")
//    public ResponseEntity<String> removeDepartments(
//            @RequestParam String trngid,
//            @RequestBody List<Long> departmentIds) {
//
//        traineeDepartmentService.removeDepartments(trngid, departmentIds);
//
//        return ResponseEntity.ok("Departments removed successfully and email sent");
//    }
   
    @GetMapping("/{trngid}")
    public ResponseEntity<List<Department>> getDepartments(
            @PathVariable String trngid
    ) {

        List<Department> departments =
                traineeDepartmentService.getDepartmentsByTrainee(trngid);

        return ResponseEntity.ok(departments);
    }
}
