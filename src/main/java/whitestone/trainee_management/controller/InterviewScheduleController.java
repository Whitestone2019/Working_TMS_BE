package whitestone.trainee_management.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import whitestone.trainee_management.models.InterviewSchedule;
import whitestone.trainee_management.models.ScheduleTraineeMap;
import whitestone.trainee_management.models.User;
import whitestone.trainee_management.payload.ApiResponse;
import whitestone.trainee_management.service.InterviewScheduleService;
import whitestone.trainee_management.service.*;

@RestController
@CrossOrigin

@RequestMapping("/api/schedule")
public class InterviewScheduleController {

    @Autowired
    private InterviewScheduleService interviewScheduleService;

    //  Create schedule
//    @PostMapping("/create/{trainerId}")
//    public ResponseEntity<?> createSchedule(@PathVariable String trainerId,
//                                            @RequestBody InterviewSchedule schedule){
//    	System.out.println("Controller Meeting date"+schedule.getDate());
//        Object result = interviewScheduleService.createSchedule(schedule, trainerId);
//        return ResponseEntity.ok(
//                new ApiResponse(200, true, "Schedule created successfully", result)
//        );
//    }
    
    @PostMapping("/create")
    public ResponseEntity<?> createSchedule(
            @RequestBody InterviewSchedule schedule,
            @RequestParam List<String> trainerIds) {

        System.out.println("Controller Meeting date: " + schedule.getDate());
        System.out.println("Trainer IDs: " + trainerIds);

        if (trainerIds == null || trainerIds.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(400, false, "At least one manager must be assigned", null));
        }

        Object result = interviewScheduleService.createSchedule(schedule, trainerIds);

        return ResponseEntity.ok(
                new ApiResponse(200, true, "Schedule created successfully", result)
        );
    }
    //  Assign trainees to schedule
    @PostMapping("/assign/{scheduleId}")
    public ResponseEntity<?> assignTrainees(@PathVariable Long scheduleId,
                                            @RequestBody List<String> empids){

        Object result = interviewScheduleService.assignTrainees(scheduleId,empids);
        return ResponseEntity.ok(
                new ApiResponse(200, true, "Trainees assigned successfully", result)
        );
    }

    //  Get all schedules
    @GetMapping("/all-user")
    public ResponseEntity<?> getAllSchedulesInUser(){
    	Map<Long, Map<String, Object>> result = interviewScheduleService.getAllSchedulesInUser();
        return ResponseEntity.ok(
                new ApiResponse(200, true, "Schedules fetched successfully", result)
        );
    }
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllSchedules(){
    	List<?> result = interviewScheduleService.getAllSchedules();
        return ResponseEntity.ok(
                new ApiResponse(200, true, "Schedules fetched successfully", result)
        );
    }
  
    
    @GetMapping("/user-interview/{empId}")
    public ResponseEntity<ApiResponse> getAllSyllabusByEmpid(@PathVariable String empId) {
        List<ScheduleTraineeMap> result = interviewScheduleService.getAllSyllabusByEmpid(empId);
        if (empId == null || empId.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(400, false, "Employee ID is required", null));
        }
        return ResponseEntity.ok(
                new ApiResponse(200, true, "Schedules fetched successfully", result)
        );
    }
    
    
    
 
    
//    @PutMapping("/update/{scheduleId}")
//    public ResponseEntity<ApiResponse> updateSchedule(
//            @PathVariable Long scheduleId,
//            @RequestBody Map<String, Object> request) {
//
//    	System.out.print(request);
//        // 🔹 Extract trainerId
//        String trainerId = request.get("interviewer") != null
//                ? request.get("interviewer").toString()
//                : null;
//
////        String trainerId = 
//        // 🔹 Extract trainees
//        List<String> empids = request.get("trainees") != null
//                ? (List<String>) request.get("trainees")
//                : Collections.emptyList();
//        
//        InterviewSchedule schedule = new InterviewSchedule();
//
//        if (request.get("date") != null) {
//            schedule.setDate(LocalDate.parse(
//                    request.get("date").toString().substring(0, 10)
//            ));
//        }
//
//        if (request.get("time") != null) {
//            schedule.setTime(LocalTime.parse(request.get("time").toString()));
//        }
//
//        schedule.setDuration(
//                request.get("duration") != null
//                        ? Integer.parseInt(request.get("duration").toString())
//                        : null
//        );
//
//        schedule.setInterviewType((String) request.get("interviewType"));
//        schedule.setLocation((String) request.get("location"));
//        schedule.setNotes((String) request.get("notes"));
//
//        System.out.println(request.get("subTopicIds"));
//
//		
//        
//        if (request.get("subTopicIds") != null) {
//            List<Long> subTopicIds =
//                ((List<?>) request.get("subTopicIds"))
//                    .stream()
//                    .map(id -> Long.valueOf(id.toString()))
//                    .toList();
//
//            schedule.setSubTopicIds(subTopicIds);
//        }
//
//        InterviewSchedule result = interviewScheduleService.updateSchedule(
//                scheduleId,
//                schedule,
//                trainerId,
//                empids
//        );
//
//        return ResponseEntity.ok(
//                new ApiResponse(200, true, "Schedule updated successfully", result)
//        );
//        
//    }
    
    
    @PutMapping("/update/{scheduleId}")
    public ResponseEntity<ApiResponse> updateSchedule(
            @PathVariable Long scheduleId,
            @RequestBody Map<String, Object> request) {

        System.out.println("Request: " + request);

        //  Extract trainerIds (multiple)
        List<String> trainerIds = request.get("interviewer") != null
                ? ((List<?>) request.get("interviewer"))
                    .stream()
                    .map(Object::toString)
                    .toList()
                : Collections.emptyList();

        //  Extract trainees
        List<String> empids = request.get("trainees") != null
                ? ((List<?>) request.get("trainees"))
                    .stream()
                    .map(Object::toString)
                    .toList()
                : Collections.emptyList();

        //  Create schedule object
        InterviewSchedule schedule = new InterviewSchedule();

        //  Date parsing (safe)
        if (request.get("date") != null) {
            schedule.setDate(LocalDate.parse(request.get("date").toString().substring(0, 10)));
        }

        //  Time parsing
        if (request.get("time") != null) {
            schedule.setTime(LocalTime.parse(request.get("time").toString()));
        }

        //  Duration
        if (request.get("duration") != null) {
            schedule.setDuration(Integer.parseInt(request.get("duration").toString()));
        }

        //  Other fields
        schedule.setInterviewType((String) request.get("interviewType"));
        schedule.setLocation((String) request.get("location"));
        schedule.setNotes((String) request.get("notes"));

        //  SubTopicIds
        if (request.get("subTopicIds") != null) {
            List<Long> subTopicIds = ((List<?>) request.get("subTopicIds"))
                    .stream()
                    .map(id -> Long.valueOf(id.toString()))
                    .toList();

            schedule.setSubTopicIds(subTopicIds);
        }

        //  CALL SERVICE (IMPORTANT FIX)
        InterviewSchedule result = interviewScheduleService.updateSchedule(
                scheduleId,
                schedule,
                trainerIds,   
                empids
        );

        return ResponseEntity.ok(
                new ApiResponse(200, true, "Schedule updated successfully", result)
        );
    }
    
    
//    private final GoogleMeetService googleMeetService;
//
//    public InterviewScheduleController(GoogleMeetService googleMeetService) {
//        this.googleMeetService = googleMeetService;
//    }
 // Soft delete an entire interview schedule along with related trainee mappings
    @DeleteMapping("/delete/{scheduleId}")
    public ResponseEntity<ApiResponse> deleteSchedule(@PathVariable Long scheduleId) {
        try {
            String result = interviewScheduleService.deleteSchedule(scheduleId);
            return ResponseEntity.ok(
                    new ApiResponse(200, true, result, null)
            );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(400, false, "Error deleting schedule: " + e.getMessage(), null));
        }
    }

    

}
