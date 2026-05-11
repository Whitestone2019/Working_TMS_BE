package whitestone.trainee_management.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import whitestone.trainee_management.models.StepProgress;
import whitestone.trainee_management.models.SubTopic;
import whitestone.trainee_management.models.Syllabus;
import whitestone.trainee_management.models.TraineeAssessment;
import whitestone.trainee_management.payload.ApiResponse;
import whitestone.trainee_management.repository.SubTopicRepository;
import whitestone.trainee_management.repository.TraineeAssessmentRepository;
import whitestone.trainee_management.repository.TraineeDepartmentRepository;
import whitestone.trainee_management.repository.UserRepository;

import whitestone.trainee_management.models.User;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.*;

import whitestone.trainee_management.repository.SyllabusRepository;
import whitestone.trainee_management.repository.StepProgressRepository;
import whitestone.trainee_management.models.*;

@Service
public class TraineeAssessmentService {

	@Autowired
	private TraineeAssessmentRepository assessmentRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private SubTopicRepository subTopicRepo;
	
	@Autowired
	private SyllabusRepository syllabusRepository;

	@Autowired
	private StepProgressRepository stepProgressRepository;

	@Autowired
	private TraineeDepartmentRepository traineedepartmentRepository;

	
//	public ApiResponse createAssessment(TraineeAssessment assessment, String empId) {
//
//		return userRepo.findByTrngidAndDelFlag(empId, "N").map(user -> {
//
//			//  STORE TRANSIENT DATA
//			List<Long> subTopicIds = assessment.getSubTopicIds();
//
//			if (subTopicIds == null || subTopicIds.isEmpty()) {
//				throw new RuntimeException("SubTopicIds cannot be null or empty");
//			}
//
//			//  SET BASE FIELDS
//			assessment.setAssessmentId("ASM-" + UUID.randomUUID().toString().substring(0, 6));
//			assessment.setUser(user);
//			assessment.setCreatedAt(LocalDateTime.now());
//			assessment.setCurrentStep(assessment.getCurrentStep() == null ? 1 : assessment.getCurrentStep() + 1);
//			assessment.markActive();
//			
//
//			List<SubTopic> subTopics = subTopicRepo.findAllById(subTopicIds);
//
//			if (subTopics.size() != subTopicIds.size()) {
//				throw new RuntimeException("One or more SubTopics not found");
//			}
//
//			String subtopicStr = subTopicIds.stream().map(String::valueOf).collect(Collectors.joining("|"));
//			assessment.setSubTopics(subtopicStr);
//
//			//  FETCH SUBTOPICS
//
//			assessment.setUpdatedAt(LocalDateTime.now());
//
//			assessmentRepo.save(assessment);
//
//			return new ApiResponse(200, true, "Assessment Created Successfully", assessment);
//
//		}).orElse(new ApiResponse(404, false, "Trainee Not Found", null));
//	}

	public ApiResponse getAllAssessments() {
		List<TraineeAssessment> list = assessmentRepo.findByDelFlag("N");

		return new ApiResponse(200, true, "Data Fetched", list);
	}


	public ApiResponse getAssessmentByEmpId(String empId) {
		List<TraineeAssessment> assessments = assessmentRepo.findByUser_TrngidAndDelFlag(empId, "N");

		if (assessments.isEmpty()) {
			return new ApiResponse(404, false, "No Assessments Found", null);
		}

		return new ApiResponse(200, true, "Assessments Found", assessments);
	}

	// summary for dashboard imp
	public ApiResponse getTraineeSummary() {

	    // Fetch all active users (trainees)
	    List<User> users = userRepo.findByDelFlag("N");

	    // Fetch all active assessments
	    List<TraineeAssessment> assessments = assessmentRepo.findByDelFlag("N");

	    if (users.isEmpty()) {
	        return new ApiResponse(404, false, "No Trainees Found", null);
	    }

	    List<Map<String, Object>> summaryList = new ArrayList<>();

	    for (User user : users) {
	    	
	        TraineeAssessment latest = null;
	        for (TraineeAssessment a : assessments) {
	            if (a.getUser() != null && a.getUser().getTrngid().equals(user.getTrngid())) {
	                if (latest == null || (a.getUpdatedAt() != null && a.getUpdatedAt().isAfter(latest.getUpdatedAt()))) {
	                    latest = a;
	                }
	            }
	        }

	        // Prepare summary map (handle null safely)
	        Map<String, Object> summary = new HashMap<>();
	        summary.put("userid", user.getUserid());
	        summary.put("traineeId", user.getTrngid());
	        summary.put("name", user.getFirstname() + " " + user.getLastname());
	        summary.put("email", user.getEmailid());
	        
	        String subtopicsStr = latest!=null? latest.getSubTopics() : null;
	        
	        List<String> syllabusTitles = new ArrayList<>();
	        
	        if (subtopicsStr != null && !subtopicsStr.isEmpty()) {

	        	List<Long> subTopicIds = Arrays.stream(subtopicsStr.split("\\|"))
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
	        
	        for(Long i : subTopicIds) {

		        SubTopic subTopics = subTopicRepo.findById(i)
		        		.orElseThrow(() -> new RuntimeException("SubTopic not found with id: " + i));
;
		        
		        if (!syllabusTitles.contains(subTopics.getSyllabus().getTitle())) {
		            syllabusTitles.add(subTopics.getSyllabus().getTitle());
		        }
		        
	        }
	        
	       
	        }
	        
	        summary.put("subtopics", syllabusTitles);
	        
	        summary.put("currentStep", latest != null ? "Step " + latest.getCurrentStep() : "No Assessment Yet");
	        summary.put("completionPercentage", latest != null ? latest.getPercentage() : 0);

	        summary.put("interviewStatus", latest != null ? latest.isInterviewDone() : null);
	        summary.put("lastAssessmentDate", latest != null && latest.getAssessmentDate() != null
	                ? latest.getAssessmentDate().toString() : "N/A");
	        summary.put("lastAssessmentScore", latest != null ? latest.getPercentage() : null);

	       
	        summaryList.add(summary);
	    }

	    return new ApiResponse(200, true, "Trainee Summary List Fetched", summaryList);
	}

	public ApiResponse updateAssessment(String id, TraineeAssessment request) {
		Optional<TraineeAssessment> existing = assessmentRepo.findByAssessmentIdAndDelFlag(id, "N");

		if (existing.isEmpty())
			return new ApiResponse(404, false, "Assessment Not Found", null);

		TraineeAssessment a = existing.get();
		a.setAssessmentType(request.getAssessmentType());
		a.setAssessmentDate(request.getAssessmentDate());
		a.setMarks(request.getMarks());
		a.setMaxMarks(request.getMaxMarks());
		a.setPercentage(request.getPercentage());
		a.setRemarks(request.getRemarks());
		a.setStrengths(request.getStrengths());
		a.setImprovements(request.getImprovements());
		a.setRecommendations(request.getRecommendations());
		a.setInterviewDone(request.isInterviewDone());
		a.setUpdatedAt(LocalDateTime.now());

		List<Long> subTopicIds = request.getSubTopicIds();
		System.out.println(subTopicIds);
		
		if (subTopicIds != null && !subTopicIds.isEmpty()) {

			List<SubTopic> subTopics = subTopicRepo.findAllById(subTopicIds);
			if (subTopics.size() != subTopicIds.size()) {
				throw new RuntimeException("One or more SubTopics not found");
			}

			String subtopicStr = subTopicIds.stream().map(String::valueOf).collect(Collectors.joining("|"));

			a.setSubTopics(subtopicStr);
		}
		assessmentRepo.save(a);
		return new ApiResponse(200, true, "Assessment Updated", a);
	}

	// Soft Delete
	public ApiResponse deleteAssessment(String id) {
		Optional<TraineeAssessment> existing = assessmentRepo.findByAssessmentIdAndDelFlag(id, "N");

		if (existing.isEmpty())
			return new ApiResponse(404, false, "Assessment Not Found", null);

		TraineeAssessment a = existing.get();
		a.markDeleted();
		assessmentRepo.save(a);

		return new ApiResponse(200, true, "Assessment Deleted", null);
	}
	

	

	
	public ApiResponse createAssessment(TraineeAssessment assessment, String empId) {

        return userRepo.findByTrngidAndDelFlag(empId, "N").map(user -> {

            List<Long> subTopicIds = assessment.getSubTopicIds();
            if (subTopicIds == null || subTopicIds.isEmpty()) {
                throw new RuntimeException("SubTopicIds cannot be null or empty");
            }

            assessment.setAssessmentId("ASM-" + UUID.randomUUID().toString().substring(0, 6));
            assessment.setUser(user);
            assessment.setCreatedAt(LocalDateTime.now());
            assessment.setCurrentStep(assessment.getCurrentStep() == null ? 1 : assessment.getCurrentStep() + 1);
            assessment.markActive();

            List<SubTopic> subTopics = subTopicRepo.findAllById(subTopicIds);
            if (subTopics.size() != subTopicIds.size()) {
                throw new RuntimeException("One or more SubTopics not found");
            }

            String subtopicStr = subTopicIds.stream().map(String::valueOf).collect(Collectors.joining("|"));
            assessment.setSubTopics(subtopicStr);

            assessment.setUpdatedAt(LocalDateTime.now());
            assessmentRepo.save(assessment);

            return new ApiResponse(200, true, "Assessment Created Successfully", assessment);

        }).orElse(new ApiResponse(404, false, "Trainee Not Found", null));
    }


 
	
	public ApiResponse getTraineeSummaryByManager(String managerUserId) {

	    List<User> trainees = userRepo.findByManagerData_UseridAndDelFlag(managerUserId, "N");
	    if (trainees.isEmpty()) {
	        return new ApiResponse(404, false, "No Trainees found for this manager", null);
	    }

	    List<Map<String, Object>> summaryList = new ArrayList<>();

	    for (User user : trainees) {
	        Map<String, Object> summary = new LinkedHashMap<>();
	        summary.put("userid", user.getUserid());
	        summary.put("traineeId", user.getTrngid());
	        summary.put("name", user.getFirstname() + " " + user.getLastname());
	        summary.put("email", user.getEmailid());
	     
	     // ASSIGNED DEPARTMENTS 
	    

	     List<TraineeDepartment> traineeDepartments =
	             traineedepartmentRepository.findByTrainee(user);

	     List<Map<String, Object>> assignedDepartments = new ArrayList<>();

	     for (TraineeDepartment td : traineeDepartments) {
	         Map<String, Object> deptMap = new LinkedHashMap<>();
	         deptMap.put("departmentId", td.getDepartment().getId());
	         deptMap.put("name", td.getDepartment().getName());
	         assignedDepartments.add(deptMap);
	     }

	     summary.put("assignedDepartments", assignedDepartments);

	        //  Trainee ke assigned department IDs fetch
	        List<Long> traineeDeptIds = traineedepartmentRepository.findDepartmentIdsByTraineeTrngid(user.getTrngid());

	        List<Syllabus> syllabusList = syllabusRepository.findAll();
	        List<Map<String, Object>> syllabusResponse = new ArrayList<>();
 List<TraineeAssessment> calculation = assessmentRepo.findByUser_TrngidAndDelFlag(user.getTrngid(), "N");
	        
	        double average = calculation.stream()
	                .map(TraineeAssessment::getMarks)     // String
	                .filter(m -> m != null && !m.trim().isEmpty())
	                .mapToDouble(m -> {
	                    try {
	                        return Double.parseDouble(m);
	                    } catch (NumberFormatException e) {
	                        return 0.0;
	                    }
	                })
	                .average()
	                .orElse(.0);

	        for (Syllabus syllabus : syllabusList) {

	            // -------------------------
	            // SYLLABUS TRAINERS
	            // -------------------------
	            List<Map<String, Object>> trainersList = new ArrayList<>();
	            if (syllabus.getManagers() != null) {
	                for (User tr : syllabus.getManagers()) {
	                    Map<String, Object> trainerMap = new LinkedHashMap<>();
	                    trainerMap.put("trainerId", tr.getTrngid());
	                    trainerMap.put("name", tr.getFirstname() + " " + tr.getLastname());
	                    trainerMap.put("email", tr.getEmailid());
	                    trainersList.add(trainerMap);
	                }
	            }

//	        	 Map<String, Object> trainerMap = null;
//
//	             if (syllabus.getManager() != null) {
//	                 User tr = syllabus.getManager();
//
//	                 trainerMap = new LinkedHashMap<>();
//	                 trainerMap.put("trainerId", tr.getTrngid());
//	                 trainerMap.put("name", tr.getFirstname() + " " + tr.getLastname());
//	                 trainerMap.put("email", tr.getEmailid());
//	             }
	            // -------------------------
	            // SYLLABUS DEPARTMENTS FILTERED
	            // -------------------------
	            List<Map<String, Object>> departmentsList = new ArrayList<>();
	            if (syllabus.getDepartments() != null) {
	                for (Department dept : syllabus.getDepartments()) {
	                    // 🔹 Sirf wahi department jo trainee ke assigned department me ho
	                    if (traineeDeptIds.contains(dept.getId())) {
	                        Map<String, Object> deptMap = new LinkedHashMap<>();
	                        deptMap.put("departmentId", dept.getId());
	                        deptMap.put("name", dept.getName());
	                        departmentsList.add(deptMap);
	                    }
	                }
	            }

	            // Agar syllabus me trainee ke liye koi department nahi hai, skip
	            if (departmentsList.isEmpty()) continue;

	            // -------------------------
	            // SUBTOPIC + PROGRESS LOGIC (unchanged)
	            // -------------------------
	            List<StepProgress> progressList =
	                    stepProgressRepository.findByUser_UseridAndSubTopic_Syllabus_Id(user.getUserid(), syllabus.getId());
	            if (progressList.isEmpty()) continue;

	            Map<Long, List<StepProgress>> progressBySubTopic = new HashMap<>();
	            for (StepProgress sp : progressList) {
	                progressBySubTopic.computeIfAbsent(sp.getSubTopic().getId(), k -> new ArrayList<>()).add(sp);
	            }

	            boolean isSyllabusCompleted = syllabus.getSubTopics().stream().allMatch(st ->
	                    progressBySubTopic.containsKey(st.getId()) &&
	                    progressBySubTopic.get(st.getId()).stream().anyMatch(sp -> sp.isComplete())
	            );
	            if (!isSyllabusCompleted) continue;

	            List<Map<String, Object>> subTopicResponseList = new ArrayList<>();
	            for (SubTopic st : syllabus.getSubTopics()) {
	                if (!progressBySubTopic.containsKey(st.getId())) continue;

	                List<StepProgress> validProgresses = progressBySubTopic.get(st.getId()).stream()
	                        .filter(sp -> sp.isComplete() && sp.isChecker())
	                        .toList();
	                if (validProgresses.isEmpty()) continue;

	                Map<String, Object> subTopicMap = new LinkedHashMap<>();
	                subTopicMap.put("subTopicId", st.getId());
	                subTopicMap.put("stepNumber", st.getStepNumber());
	                subTopicMap.put("name", st.getName());
	                subTopicMap.put("description", st.getDescription());
	                subTopicMap.put("filePath", st.getFilePath());

	                List<Map<String, Object>> progressResponseList = new ArrayList<>();
	                for (StepProgress sp : progressBySubTopic.get(st.getId())) {
	                    if (!sp.isComplete()) continue;

	                    Map<String, Object> progressMap = new LinkedHashMap<>();
	                    progressMap.put("stepProgressId", sp.getId());
	                    progressMap.put("complete", sp.isComplete());
	                    progressMap.put("checker", sp.isChecker());
	                    progressMap.put("review", sp.getReview());
	                    progressMap.put("timeSpentSeconds", sp.getTimeSpentSeconds());
	                    progressMap.put("startDateTime", sp.getStartDateTime());
	                    progressMap.put("endDateTime", sp.getEndDateTime());
	                    progressResponseList.add(progressMap);
	                }
	                subTopicMap.put("stepProgress", progressResponseList);
	                subTopicResponseList.add(subTopicMap);
	            }

	            Map<String, Object> syllabusMap = new LinkedHashMap<>();
	            syllabusMap.put("syllabusId", syllabus.getId());
	            syllabusMap.put("title", syllabus.getTitle());
	            syllabusMap.put("topic", syllabus.getTopic());
	            syllabusMap.put("durationInDays", syllabus.getDurationInDays());
	            syllabusMap.put("trainers", trainersList);
	            syllabusMap.put("departments", departmentsList); // filtered
	            syllabusMap.put("subTopics", subTopicResponseList);

	            syllabusResponse.add(syllabusMap);
	        }

	        summary.put("syllabusProgress", syllabusResponse);

	        // Assessment summary (unchanged)
	        TraineeAssessment latest =
	                assessmentRepo.findTopByUser_UseridAndDelFlagOrderByUpdatedAtDesc(user.getUserid(), "N");
	       
	        
	        
	        Double score = (latest != null && latest.getPercentage() != null) ? latest.getPercentage() : 0.0;
	        //summary.put("completionPercentage", average);
	        summary.put("completionPercentage", (int) Math.round(average));

	        summary.put("lastAssessmentScore", score);
	        summary.put("interviewStatus", latest != null ? latest.isInterviewDone() : null);
	        summary.put("lastAssessmentDate",
	                latest != null && latest.getAssessmentDate() != null
	                        ? latest.getAssessmentDate().toString()
	                        : "N/A");

	        summaryList.add(summary);
	    }

	    return new ApiResponse(200, true, "Manager's Trainee Summary Fetched", summaryList);
	}
	public ApiResponse getAllTraineeSummary() {

	    List<User> trainees = userRepo.findByManagerDataIsNotNullAndDelFlag("N");

	    if (trainees.isEmpty()) {
	        return new ApiResponse(404, false, "No Trainees found", null);
	    }

	    List<Map<String, Object>> summaryList = new ArrayList<>();

	    for (User user : trainees) {
	        Map<String, Object> summary = new LinkedHashMap<>();
	        summary.put("userid", user.getUserid());
	        summary.put("traineeId", user.getTrngid());
	        summary.put("name", user.getFirstname() + " " + user.getLastname());
	        summary.put("email", user.getEmailid());
	        
	        List<TraineeDepartment> traineeDepartments =
		             traineedepartmentRepository.findByTrainee(user);

		     List<Map<String, Object>> assignedDepartments = new ArrayList<>();

		     for (TraineeDepartment td : traineeDepartments) {
		         Map<String, Object> deptMap = new LinkedHashMap<>();
		         deptMap.put("departmentId", td.getDepartment().getId());
		         deptMap.put("name", td.getDepartment().getName());
		         assignedDepartments.add(deptMap);
		     }

		     summary.put("assignedDepartments", assignedDepartments);

	        //  Trainee ke assigned department IDs fetch
	        List<Long> traineeDeptIds = traineedepartmentRepository.findDepartmentIdsByTraineeTrngid(user.getTrngid());

	        List<Syllabus> syllabusList = syllabusRepository.findAll();
	        List<Map<String, Object>> syllabusResponse = new ArrayList<>();
 List<TraineeAssessment> calculation = assessmentRepo.findByUser_TrngidAndDelFlag(user.getTrngid(), "N");
	        
	        double average = calculation.stream()
	                .map(TraineeAssessment::getMarks)     // String
	                .filter(m -> m != null && !m.trim().isEmpty())
	                .mapToDouble(m -> {
	                    try {
	                        return Double.parseDouble(m);
	                    } catch (NumberFormatException e) {
	                        return 0.0;
	                    }
	                })
	                .average()
	                .orElse(.0);

	        for (Syllabus syllabus : syllabusList) {

	            // -------------------------
	            // SYLLABUS TRAINERS
	            // -------------------------
	            List<Map<String, Object>> trainersList = new ArrayList<>();
	            if (syllabus.getManagers() != null) {
	                for (User tr : syllabus.getManagers()) {
	                    Map<String, Object> trainerMap = new LinkedHashMap<>();
	                    trainerMap.put("trainerId", tr.getTrngid());
	                    trainerMap.put("name", tr.getFirstname() + " " + tr.getLastname());
	                    trainerMap.put("email", tr.getEmailid());
	                    trainersList.add(trainerMap);
	                }
	            }
	        	

//	        	 Map<String, Object> trainerMap = null;
//
//	             if (syllabus.getManager() != null) {
//	                 User tr = syllabus.getManager();
//
//	                 trainerMap = new LinkedHashMap<>();
//	                 trainerMap.put("trainerId", tr.getTrngid());
//	                 trainerMap.put("name", tr.getFirstname() + " " + tr.getLastname());
//	                 trainerMap.put("email", tr.getEmailid());
//	             }

	            // -------------------------
	            // SYLLABUS DEPARTMENTS FILTERED
	            // -------------------------
	            List<Map<String, Object>> departmentsList = new ArrayList<>();
	            if (syllabus.getDepartments() != null) {
	                for (Department dept : syllabus.getDepartments()) {
	                    // 🔹 Sirf wahi department jo trainee ke assigned department me ho
	                    if (traineeDeptIds.contains(dept.getId())) {
	                        Map<String, Object> deptMap = new LinkedHashMap<>();
	                        deptMap.put("departmentId", dept.getId());
	                        deptMap.put("name", dept.getName());
	                        departmentsList.add(deptMap);
	                    }
	                }
	            }

	            // Agar syllabus me trainee ke liye koi department nahi hai, skip
	            if (departmentsList.isEmpty()) continue;

	            // -------------------------
	            // SUBTOPIC + PROGRESS LOGIC (unchanged)
	            // -------------------------
	            List<StepProgress> progressList =
	                    stepProgressRepository.findByUser_UseridAndSubTopic_Syllabus_Id(user.getUserid(), syllabus.getId());
	            if (progressList.isEmpty()) continue;

	            Map<Long, List<StepProgress>> progressBySubTopic = new HashMap<>();
	            for (StepProgress sp : progressList) {
	                progressBySubTopic.computeIfAbsent(sp.getSubTopic().getId(), k -> new ArrayList<>()).add(sp);
	            }

	            boolean isSyllabusCompleted = syllabus.getSubTopics().stream().allMatch(st ->
	                    progressBySubTopic.containsKey(st.getId()) &&
	                    progressBySubTopic.get(st.getId()).stream().anyMatch(sp -> sp.isComplete())
	            );
	            if (!isSyllabusCompleted) continue;

	            List<Map<String, Object>> subTopicResponseList = new ArrayList<>();
	            for (SubTopic st : syllabus.getSubTopics()) {
	                if (!progressBySubTopic.containsKey(st.getId())) continue;

	                List<StepProgress> validProgresses = progressBySubTopic.get(st.getId()).stream()
	                        .filter(sp -> sp.isComplete() && sp.isChecker())
	                        .toList();
	                if (validProgresses.isEmpty()) continue;

	                Map<String, Object> subTopicMap = new LinkedHashMap<>();
	                subTopicMap.put("subTopicId", st.getId());
	                subTopicMap.put("stepNumber", st.getStepNumber());
	                subTopicMap.put("name", st.getName());
	                subTopicMap.put("description", st.getDescription());
	                subTopicMap.put("filePath", st.getFilePath());

	                List<Map<String, Object>> progressResponseList = new ArrayList<>();
	                for (StepProgress sp : progressBySubTopic.get(st.getId())) {
	                    if (!sp.isComplete()) continue;

	                    Map<String, Object> progressMap = new LinkedHashMap<>();
	                    progressMap.put("stepProgressId", sp.getId());
	                    progressMap.put("complete", sp.isComplete());
	                    progressMap.put("checker", sp.isChecker());
	                    progressMap.put("review", sp.getReview());
	                    progressMap.put("timeSpentSeconds", sp.getTimeSpentSeconds());
	                    progressMap.put("startDateTime", sp.getStartDateTime());
	                    progressMap.put("endDateTime", sp.getEndDateTime());
	                    progressResponseList.add(progressMap);
	                }
	                subTopicMap.put("stepProgress", progressResponseList);
	                subTopicResponseList.add(subTopicMap);
	            }

	            Map<String, Object> syllabusMap = new LinkedHashMap<>();
	            syllabusMap.put("syllabusId", syllabus.getId());
	            syllabusMap.put("title", syllabus.getTitle());
	            syllabusMap.put("topic", syllabus.getTopic());
	            syllabusMap.put("durationInDays", syllabus.getDurationInDays());
	            syllabusMap.put("trainers", trainersList);
	            syllabusMap.put("departments", departmentsList); // filtered
	            syllabusMap.put("subTopics", subTopicResponseList);

	            syllabusResponse.add(syllabusMap);
	        }

	        summary.put("syllabusProgress", syllabusResponse);

	        // Assessment summary (unchanged)
	        TraineeAssessment latest =
	                assessmentRepo.findTopByUser_UseridAndDelFlagOrderByUpdatedAtDesc(user.getUserid(), "N");
	       
	        
	        
	        Double score = (latest != null && latest.getPercentage() != null) ? latest.getPercentage() : 0.0;
//	        summary.put("completionPercentage", average);
	        summary.put("completionPercentage", (int) Math.round(average));

	        summary.put("lastAssessmentScore", score);
	        summary.put("interviewStatus", latest != null ? latest.isInterviewDone() : null);
	        summary.put("lastAssessmentDate",
	                latest != null && latest.getAssessmentDate() != null
	                        ? latest.getAssessmentDate().toString()
	                        : "N/A");

	        summaryList.add(summary);
	    }

	    return new ApiResponse(200, true, "Manager's Trainee Summary Fetched", summaryList);
	}
}

	
