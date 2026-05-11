
package whitestone.trainee_management.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import whitestone.trainee_management.models.StepProgress;
import whitestone.trainee_management.service.StepProgressService;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/step-progress")
@CrossOrigin(origins = "*")
public class StepProgressController {

	@Autowired
	private StepProgressService service;

	@GetMapping("/")
	public String name() {
		return "Hiiiiii";
	}

	@PostMapping("/start")
	public ResponseEntity<?> start(@RequestBody Map<String, Object> req) {
		
		
		System.out.println(req);
		
		 Object empObj = req.get("empid");
		    Object subObj = req.get("subtopicId");

		    if (empObj == null || subObj == null) {
		        return ResponseEntity.badRequest()
		                .body("empid and subtopicId are required");
		    }

		    service.startSubTopic(
		            empObj.toString(),
		            Long.valueOf(subObj.toString())
		    );

		return ResponseEntity.ok().build();
	}

	@PostMapping("/complete")
	public ResponseEntity<?> complete(@RequestBody Map<String, Object> req) {
		System.out.println(req);
		
		 Object empObj = req.get("empid");
		    Object subObj = req.get("subtopicId");

		    if (empObj == null || subObj == null) {
		        return ResponseEntity.badRequest()
		                .body("empid and subtopicId are required");
		    }

		    service.completeSubTopic(
		            empObj.toString(),
		            Long.valueOf(subObj.toString())
		    );

		return ResponseEntity.ok().build();
	}

	@GetMapping("/emp/{empid}")
	public List<StepProgress> getProgress(@PathVariable String empid) {
		return service.getAllByEmpid(empid);
	}
	@GetMapping("/completed-subtopics")
	public List<StepProgress> getCompletedSyllabus() {
	    return service.getCompletedSyllabus();
	}
	
	 @PutMapping("/approve/{progressId}")
	    public ResponseEntity<StepProgress> approveSubTopic(
	            @PathVariable Long progressId,
	            @RequestParam(required = false) String review
	    ) {

	        StepProgress result =
	                service.approveSubTopic(progressId, review);

	        return ResponseEntity.ok(result);
	    }

	    
	    @PutMapping("/reject/{progressId}")
	    public ResponseEntity<StepProgress> rejectSubTopic(
	            @PathVariable Long progressId,
	            @RequestParam String review
	    ) {

	        StepProgress result =
	                service.rejectSubTopic(progressId, review);

	        return ResponseEntity.ok(result);
	    }
	    
	    @GetMapping("/structured")
	    public ResponseEntity<List<Map<String, Object>>> getStructuredProgress() {
	        return ResponseEntity.ok(
	                service.getProgressStructuredResponse()
	        );
	    } 
	
	    @GetMapping("/steps/{empid}")
	    public ResponseEntity<List<Map<String, Object>>> getSteps(
	            @PathVariable String empid) {

	        return ResponseEntity.ok(
	                service.getUserStepStatus(empid)
	        );
	    }

}
