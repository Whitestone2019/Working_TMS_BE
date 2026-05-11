

package whitestone.trainee_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import whitestone.trainee_management.models.*;
import whitestone.trainee_management.repository.*;

import java.util.*;
import java.util.stream.Collectors;
import java.io.File;

@Service
public class SyllabusService {

    private final SyllabusRepository syllabusRepository;
    private final SubTopicRepository subtopicRepository;
    private final StepProgressRepository stepProgressRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final TraineeDepartmentRepository trianeedepartmentRepository;
    private final SyllabusFeedbackService feedbackService;
    private final EmailService emailService;
    private final SyllabusDeadlineStatusRepository deadlineRepository;
    private final SyllabusFeedbackRepository syllabusFeedbackRepository;
    @Value("${file.upload-dir}")
    private String uploadDirConfig;

    public SyllabusService(
            SyllabusRepository syllabusRepository,
            SubTopicRepository subtopicRepository,
            StepProgressRepository stepProgressRepository,
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            TraineeDepartmentRepository trianeedepartmentRepository,
            SyllabusFeedbackService feedbackService,
            EmailService emailService,
            SyllabusDeadlineStatusRepository deadlineRepository,
            SyllabusFeedbackRepository syllabusFeedbackRepository
            
    ) {
        this.syllabusRepository = syllabusRepository;
        this.subtopicRepository = subtopicRepository;
        this.stepProgressRepository = stepProgressRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.trianeedepartmentRepository=trianeedepartmentRepository;
        this.feedbackService=feedbackService;
        this.emailService = emailService;
        this.deadlineRepository=deadlineRepository;
        this.syllabusFeedbackRepository=syllabusFeedbackRepository;
    }

     
    

    public Syllabus saveSyllabus(Syllabus syllabus, MultipartFile[] files) throws Exception {

        Syllabus s = new Syllabus();
        s.setTitle(syllabus.getTitle());
        s.setTopic(syllabus.getTopic());
        s.setDurationInDays(syllabus.getDurationInDays());

        // ------------------ SET TRAINER / MANAGER ------------------
        Set<User> trainers = new HashSet<>();

        if (syllabus.getManagers() != null) {

            for (User t : syllabus.getManagers()) {

                User u = userRepository
                        .findByTrngidAndDelFlag(t.getTrngid(), "N")
                        .orElseThrow(() -> new RuntimeException("Trainer not found"));

                trainers.add(u);
            }

            s.setManagers(trainers);
        }
        
//       if (syllabus.getManagers() != null) {
//           Set<User> trainers = new HashSet<>();
//           for (User t : syllabus.getManagers()) {
//               User u = userRepository.findByTrngidAndDelFlag(t.getTrngid(), "N")
//                       .orElseThrow(() -> new RuntimeException("Trainer not found"));
//               trainers.add(u);
//           }
//           s.setManagers(trainers);
//       }
//        User trainer = null;
//        if (syllabus.getManager() != null) {
//            trainer = userRepository
//                    .findByTrngidAndDelFlag(syllabus.getManager().getTrngid(), "N")
//                    .orElseThrow(() -> new RuntimeException("Trainer not found"));
//            s.setManager(trainer);
//        }

        // ------------------ SET DEPARTMENTS ------------------
        if (syllabus.getDepartments() != null) {
            Set<Department> depts = new HashSet<>();
            for (Department d : syllabus.getDepartments()) {
                Department dept = departmentRepository.findById(d.getId())
                        .orElseThrow(() -> new RuntimeException("Department not found"));
                depts.add(dept);
            }
            s.setDepartments(depts);
        }

        // ------------------ SET SUBTOPICS ------------------
        List<SubTopic> subTopicList = new ArrayList<>();
        if (syllabus.getSubTopics() != null) {
            if (files == null || files.length != syllabus.getSubTopics().size()) {
                throw new IllegalArgumentException("Files count must match subtopics count.");
            }

            for (int i = 0; i < syllabus.getSubTopics().size(); i++) {
                SubTopic inputSubTopic = syllabus.getSubTopics().get(i);
                MultipartFile file = files[i];
                if (file == null || file.isEmpty()) {
                    throw new IllegalArgumentException("File missing for subtopic: " + inputSubTopic.getName());
                }

                String savedPath = FileUploadUtil.saveFile(file, uploadDirConfig);

                SubTopic st = new SubTopic();
                st.setName(inputSubTopic.getName());
                st.setDescription(inputSubTopic.getDescription());
                st.setStepNumber(i + 1);
                st.setFilePath(savedPath);
                st.setSyllabus(s);

                subTopicList.add(st);
            }
        }
        s.setSubTopics(subTopicList);

        // ------------------ SAVE SYLLABUS ------------------
        Syllabus savedSyllabus = syllabusRepository.save(s);

        // ------------------ SEND EMAIL TO TRAINER ------------------
//        if (trainer != null && trainer.getEmailid() != null) {
//            emailService.sendEmailToTrainer(trainer, savedSyllabus);
//        }
        
        for (User trainer : trainers) {

            if (trainer.getEmailid() != null) {

                emailService.sendEmailToTrainer(trainer, savedSyllabus);

            }
        }

        return savedSyllabus;
    }

 
    // ================= GET ALL SYLLABUS =================
    public List<Syllabus> getAllSyllabus() {
        return syllabusRepository.findAll();
    }

    public List<SubTopic> getAllSubTopicsWithSyllabus() {
        return subtopicRepository.findAllWithSyllabusAndManager();
    }

    // ================= GET BY ID =================
    public Syllabus getSyllabusById(Long id) {
        return syllabusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Syllabus not found with id " + id));
    }

    // ================= UPDATE SYLLABUS =================
//    public Syllabus updateSyllabus(Long id, Syllabus updated, MultipartFile[] files) throws Exception {
//
//        Syllabus existing = syllabusRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Syllabus not found"));
//        
//        int oldCount = existing.getSubTopics().size();
//
//
//        existing.setTitle(updated.getTitle());
//        existing.setTopic(updated.getTopic());
//        existing.setDurationInDays(updated.getDurationInDays());
//
//        // ------------------ UPDATE TRAINERS ------------------
//        if (updated.getManagers() != null) {
//            Set<User> trainers = new HashSet<>();
//            for (User t : updated.getManagers()) {
//                User u = userRepository.findByTrngidAndDelFlag(t.getTrngid(), "N")
//                        .orElseThrow(() -> new RuntimeException("Trainer not found"));
//                trainers.add(u);
//            }
//            existing.setManagers(trainers);
//        }
//
////        if (updated.getManager() != null) {
////
////            User trainer = userRepository
////                    .findByTrngidAndDelFlag(
////                            updated.getManager().getTrngid(),
////                            "N"
////                    )
////                    .orElseThrow(() -> new RuntimeException("Trainer not found"));
////
////            existing.setManager(trainer);
////        }
//        // ------------------ UPDATE DEPARTMENTS ------------------
//        if (updated.getDepartments() != null) {
//            Set<Department> depts = new HashSet<>();
//            for (Department d : updated.getDepartments()) {
//                Department dept = departmentRepository.findById(d.getId())
//                        .orElseThrow(() -> new RuntimeException("Department not found"));
//                depts.add(dept);
//            }
//            existing.setDepartments(depts);
//        }
//
//
//        List<SubTopic> finalSubtopics = new ArrayList<>();
//        int step = 1;
//
//        for (int i = 0; i < updated.getSubTopics().size(); i++) {
//
//            SubTopic incoming = updated.getSubTopics().get(i);
//            SubTopic sub;
//
//            // If ID exists → fetch from DB (update case)
//            if (incoming.getId() != null) {
//                sub = subtopicRepository.findById(incoming.getId())
//                        .orElseThrow(() -> new RuntimeException("Subtopic not found"));
//            } 
//            // If ID null → create new (new subtopic)
//            else {
//                sub = new SubTopic();
//            }
//
//            sub.setName(incoming.getName());
//            sub.setDescription(incoming.getDescription());
//            sub.setSyllabus(existing);
//            sub.setStepNumber(step++);
//
//            MultipartFile file = null;
//            if (files != null && i < files.length) {
//                file = files[i];
//            }
//
//            if (file != null && !file.isEmpty()) {
//                String newPath = FileUploadUtil.saveFile(file, uploadDirConfig);
//                sub.setFilePath(newPath);
//            } 
//            else if (incoming.getFilePath() != null) {
//                sub.setFilePath(incoming.getFilePath());
//            }
//
//            finalSubtopics.add(sub);
//        }
//
 //       existing.getSubTopics().clear();
 //       existing.getSubTopics().addAll(finalSubtopics);

      //  return syllabusRepository.save(existing);
    //}
    
    public Syllabus updateSyllabus(Long id, Syllabus updated, MultipartFile[] files) throws Exception {

        Syllabus existing = syllabusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Syllabus not found"));

        // ================= SAVE OLD TRAINERS =================
        Set<User> oldTrainers = new HashSet<>(existing.getManagers());

        existing.setTitle(updated.getTitle());
        existing.setTopic(updated.getTopic());
        existing.setDurationInDays(updated.getDurationInDays());

        // ================= UPDATE TRAINERS =================
        Set<User> newTrainers = new HashSet<>();

        if (updated.getManagers() != null) {

            for (User t : updated.getManagers()) {

                User trainer = userRepository
                        .findByTrngidAndDelFlag(t.getTrngid(), "N")
                        .orElseThrow(() -> new RuntimeException("Trainer not found"));

                newTrainers.add(trainer);
            }
        }

        existing.setManagers(newTrainers);

        // ================= UPDATE DEPARTMENTS =================
        if (updated.getDepartments() != null) {

            Set<Department> depts = new HashSet<>();

            for (Department d : updated.getDepartments()) {

                Department dept = departmentRepository
                        .findById(d.getId())
                        .orElseThrow(() -> new RuntimeException("Department not found"));

                depts.add(dept);
            }

            existing.setDepartments(depts);
        }

        // ================= UPDATE SUBTOPICS =================
        List<SubTopic> finalSubtopics = new ArrayList<>();
        int step = 1;

        for (int i = 0; i < updated.getSubTopics().size(); i++) {

            SubTopic incoming = updated.getSubTopics().get(i);

            SubTopic sub;

            if (incoming.getId() != null) {

                sub = subtopicRepository.findById(incoming.getId())
                        .orElseThrow(() -> new RuntimeException("Subtopic not found"));

            } else {

                sub = new SubTopic();
            }

            sub.setName(incoming.getName());
            sub.setDescription(incoming.getDescription());
            sub.setSyllabus(existing);
            sub.setStepNumber(step++);

            MultipartFile file = (files != null && i < files.length) ? files[i] : null;

            if (file != null && !file.isEmpty()) {

                String path = FileUploadUtil.saveFile(file, uploadDirConfig);
                sub.setFilePath(path);

            } else if (incoming.getFilePath() != null) {

                sub.setFilePath(incoming.getFilePath());
            }

            finalSubtopics.add(sub);
        }

        existing.getSubTopics().clear();
        existing.getSubTopics().addAll(finalSubtopics);

        // ================= SAVE SYLLABUS =================
        Syllabus saved = syllabusRepository.save(existing);

        // ================= TRAINER CHANGE DETECTION =================
        Set<User> removedTrainers = new HashSet<>(oldTrainers);
        removedTrainers.removeAll(newTrainers);

        Set<User> addedTrainers = new HashSet<>(newTrainers);
        addedTrainers.removeAll(oldTrainers);

        // ================= SEND EMAIL =================

        // Existing trainers update mail
        for (User trainer : newTrainers) {

            if (trainer.getEmailid() != null) {

                emailService.sendSyllabusUpdateEmail(trainer, saved);
            }
        }

        // Removed trainers
        for (User trainer : removedTrainers) {

            if (trainer.getEmailid() != null) {

                emailService.sendTrainerRemovedMail(trainer, saved);
            }
        }

        // Newly added trainers
        for (User trainer : addedTrainers) {

            if (trainer.getEmailid() != null) {

                emailService.sendEmailToTrainer(trainer, saved);
            }
        }

        return saved;
    }


    public List<Map<String, Object>> getAllSyllabusProgress() {

        List<Syllabus> syllabusList = syllabusRepository.findAll();
        if (syllabusList.isEmpty()) return Collections.emptyList();

        List<Map<String, Object>> finalResponse = new ArrayList<>();

        for (Syllabus syllabus : syllabusList) {

            Map<String, Object> syllabusMap = new LinkedHashMap<>();
            syllabusMap.put("syllabusId", syllabus.getId());
            syllabusMap.put("title", syllabus.getTitle());
            syllabusMap.put("topic", syllabus.getTopic());
            syllabusMap.put("durationInDays", syllabus.getDurationInDays());

            // Departments
            List<Long> syllabusDeptIds = syllabus.getDepartments()
                    .stream()
                    .map(Department::getId)
                    .toList();

            List<Map<String, Object>> departmentsList = syllabus.getDepartments().stream().map(dept -> {
                Map<String, Object> deptMap = new LinkedHashMap<>();
                deptMap.put("departmentId", dept.getId());
                deptMap.put("name", dept.getName());
                return deptMap;
            }).toList();

            syllabusMap.put("departments", departmentsList);

            List<Map<String, Object>> trainersList = syllabus.getManagers().stream().map(tr -> {
                Map<String, Object> trainerMap = new LinkedHashMap<>();
                trainerMap.put("trainerId", tr.getTrngid());
                trainerMap.put("name", tr.getFirstname() + " " + tr.getLastname());
                trainerMap.put("email", tr.getEmailid());
                return trainerMap;
            }).toList();

            syllabusMap.put("trainers", trainersList);
            
//            if (syllabus.getManager() != null) {
//
//                User tr = syllabus.getManager();
//
//                Map<String, Object> trainerMap = new LinkedHashMap<>();
//                trainerMap.put("trainerId", tr.getTrngid());
//                trainerMap.put("name", tr.getFirstname() + " " + tr.getLastname());
//                trainerMap.put("email", tr.getEmailid());
//
//                syllabusMap.put("trainer", trainerMap); // single object
//            } else {
//                syllabusMap.put("trainer", null);
//            }


            // Fetch progress for this syllabus
            List<StepProgress> progressList =
                    stepProgressRepository.findBySubTopic_Syllabus_Id(syllabus.getId());

            Map<Long, List<StepProgress>> progressBySubTopic = new HashMap<>();

            for (StepProgress sp : progressList) {

                User user = sp.getUser();
                if (user == null) continue;

                // 🔥 Fetch trainee departments
                List<Long> traineeDeptIds =
                        trianeedepartmentRepository.findDepartmentIdsByTraineeTrngid(user.getTrngid());

                // ✅ Check if syllabus department matches trainee department
                boolean matchesDepartment = traineeDeptIds.stream()
                        .anyMatch(syllabusDeptIds::contains);

                if (!matchesDepartment) continue;

                progressBySubTopic
                        .computeIfAbsent(sp.getSubTopic().getId(), k -> new ArrayList<>())
                        .add(sp);
            }

            // Subtopics
            List<Map<String, Object>> subTopicsList = new ArrayList<>();

            for (SubTopic st : syllabus.getSubTopics()) {

                Map<String, Object> subTopicMap = new LinkedHashMap<>();
                subTopicMap.put("subTopicId", st.getId());
                subTopicMap.put("stepNumber", st.getStepNumber());
                subTopicMap.put("name", st.getName());
                subTopicMap.put("description", st.getDescription());
                subTopicMap.put("filePath", st.getFilePath());

                List<StepProgress> subTopicProgress =
                        progressBySubTopic.getOrDefault(st.getId(), Collections.emptyList());

                List<Map<String, Object>> progressResponseList = new ArrayList<>();

                for (StepProgress sp : subTopicProgress) {

                    Map<String, Object> progressMap = new LinkedHashMap<>();
                    progressMap.put("stepProgressId", sp.getId());
                    progressMap.put("complete", sp.isComplete());
                    progressMap.put("checker", sp.isChecker());
                    progressMap.put("review", sp.getReview());
                    progressMap.put("startDateTime", sp.getStartDateTime());
                    progressMap.put("endDateTime", sp.getEndDateTime());

                    User u = sp.getUser();
                    Map<String, Object> userMap = new LinkedHashMap<>();
                    userMap.put("userid", u.getUserid());
                    userMap.put("trngid", u.getTrngid());
                    userMap.put("firstname", u.getFirstname());
                    userMap.put("lastname", u.getLastname());
                    userMap.put("email", u.getEmailid());
                    userMap.put("designation", u.getDesignation());

                    progressMap.put("user", userMap);
                    progressResponseList.add(progressMap);
                }

                subTopicMap.put("stepProgress", progressResponseList);
                subTopicsList.add(subTopicMap);
            }

            syllabusMap.put("subTopics", subTopicsList);
            finalResponse.add(syllabusMap);
        }

        return finalResponse;
    }


    @Transactional
    public void deleteSubTopic(Long subTopicId) {
        SubTopic subTopic = subtopicRepository.findById(subTopicId)
                .orElseThrow(() -> new RuntimeException("SubTopic not found"));

        stepProgressRepository.deleteBySubTopic(subTopic);
        subtopicRepository.delete(subTopic);
    }

  

    	//@Transactional
//    	public void deleteSyllabusWithSubTopics(Long syllabusId) {
//
//    	    if (!syllabusRepository.existsById(syllabusId)) {
//    	        throw new RuntimeException("Syllabus not found with id: " + syllabusId);
//    	    }
//    	    
//    	    
//
//    	    // 1️ Delete Step Progress (FK dependent)
//    	    stepProgressRepository.deleteBySyllabusId(syllabusId);
//
//    	    syllabusFeedbackRepository.deleteBySyllabusId(syllabusId);
//    	    // 2️ Delete Deadline Status
//    	    deadlineRepository.deleteBySyllabusId(syllabusId);
//
//    	    // 3️ Delete Syllabus (SubTopic + syllabus_departments auto delete)
//    	    syllabusRepository.deleteById(syllabusId);
//
//    	    System.out.println("Syllabus and all related data deleted successfully ✅");
//    	}
    	
    	@Transactional
    	public void deleteSyllabusWithSubTopics(Long syllabusId) {

    	    Syllabus syllabus = syllabusRepository.findById(syllabusId)
    	            .orElseThrow(() -> new RuntimeException("Syllabus not found"));

    	    // -------- SEND EMAIL BEFORE DELETE --------
    	    for (User trainer : syllabus.getManagers()) {

    	        if (trainer.getEmailid() != null) {

    	            emailService.sendSyllabusDeletedMail(trainer, syllabus);

    	        }
    	    }

    	    // 1️⃣ Delete Step Progress
    	    stepProgressRepository.deleteBySyllabusId(syllabusId);

    	    syllabusFeedbackRepository.deleteBySyllabusId(syllabusId);

    	    // 2️⃣ Delete Deadline
    	    deadlineRepository.deleteBySyllabusId(syllabusId);

    	    // 3️⃣ Delete Syllabus
    	    syllabusRepository.deleteById(syllabusId);

    	    System.out.println("Syllabus deleted successfully ✅");
    	}

//    // ================== HELPERS ==================
    	   private List<Map<String, Object>> mapTrainers(Set<User> trainers) {
    	       List<Map<String, Object>> list = new ArrayList<>();
    	       if (trainers == null) return list;

    	       for (User t : trainers) {
    	           Map<String, Object> map = new LinkedHashMap<>();
    	           map.put("trainerId", t.getTrngid());
    	           map.put("name", t.getFirstname() + " " + t.getLastname());
    	           map.put("email", t.getEmailid());
    	           list.add(map);
    	       }
    	       return list;
    	   }
    
//    private Map<String, Object> mapTrainer(User trainer) {
//
//        if (trainer == null) return null;
//
//        Map<String, Object> map = new LinkedHashMap<>();
//        map.put("trainerId", trainer.getTrngid());
//        map.put("name", trainer.getFirstname() + " " + trainer.getLastname());
//        map.put("email", trainer.getEmailid());
//
//        return map;
//    }


    private List<Map<String, Object>> mapDepartments(Set<Department> departments) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (departments == null) return list;

        for (Department d : departments) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("departmentId", d.getId());
            map.put("name", d.getName());
            list.add(map);
        }
        return list;
    }
    
    
    
 

    
    public List<Map<String, Object>> getSyllabusByEmpId(String trngid) {

        // 1️⃣ Fetch syllabus assigned to trainee's department
        List<Syllabus> syllabusList = syllabusRepository.findSyllabusByTraineeDepartmentsNative(trngid);
        if (syllabusList.isEmpty()) return Collections.emptyList();

        // 2️⃣ Fetch trainee's department(s)
        List<Long> traineeDeptIds = trianeedepartmentRepository.findDepartmentIdsByTraineeTrngid(trngid);

        List<Map<String, Object>> finalResponse = new ArrayList<>();

        for (Syllabus syllabus : syllabusList) {
            Map<String, Object> syllabusMap = new LinkedHashMap<>();
            syllabusMap.put("syllabusId", syllabus.getId());
            syllabusMap.put("title", syllabus.getTitle());
            syllabusMap.put("topic", syllabus.getTopic());
            syllabusMap.put("durationInDays", syllabus.getDurationInDays());

            // Filter departments: only those assigned to trainee
            List<Map<String, Object>> departmentsList = syllabus.getDepartments().stream()
                    .filter(dept -> traineeDeptIds.contains(dept.getId()))
                    .map(dept -> {
                        Map<String, Object> deptMap = new LinkedHashMap<>();
                        deptMap.put("departmentId", dept.getId());
                        deptMap.put("name", dept.getName());
                        return deptMap;
                    }).toList();
            syllabusMap.put("departments", departmentsList);

            List<Map<String, Object>> trainersList = syllabus.getManagers().stream().map(tr -> {
                Map<String, Object> trainerMap = new LinkedHashMap<>();
                trainerMap.put("trainerId", tr.getTrngid());
                trainerMap.put("name", tr.getFirstname() + " " + tr.getLastname());
                trainerMap.put("email", tr.getEmailid());
                return trainerMap;
            }).toList();

            syllabusMap.put("trainers", trainersList);
            // Manager/Trainer
//            if (syllabus.getManagers() != null) {
//                User tr = (User) syllabus.getManagers();
//                Map<String, Object> trainerMap = new LinkedHashMap<>();
//                trainerMap.put("trainerId", tr.getTrngid());
//                trainerMap.put("name", tr.getFirstname() + " " + tr.getLastname());
//                trainerMap.put("email", tr.getEmailid());
//                syllabusMap.put("trainer", trainerMap);
//            } else {
//                syllabusMap.put("trainer", null);
//            }

            // Subtopics & progress logic
            List<Map<String, Object>> subTopicsList = new ArrayList<>();
            for (SubTopic st : syllabus.getSubTopics()) {
                Map<String, Object> subTopicMap = new LinkedHashMap<>();
                subTopicMap.put("subTopicId", st.getId());
                subTopicMap.put("stepNumber", st.getStepNumber());
                subTopicMap.put("name", st.getName());
                subTopicMap.put("description", st.getDescription());
                subTopicMap.put("filePath", st.getFilePath());

                //  Correct StepProgress fetch
                List<StepProgress> progressList = stepProgressRepository
                        .findBySubTopic_IdAndUser_Trngid(st.getId(), trngid);

                List<Map<String, Object>> progressResponseList = progressList.stream().map(sp -> {
                    Map<String, Object> progressMap = new LinkedHashMap<>();
                    progressMap.put("stepProgressId", sp.getId());
                    progressMap.put("complete", sp.isComplete());
                    progressMap.put("checker", sp.isChecker());   //  checker will now reflect true
                    progressMap.put("review", sp.getReview());
                    progressMap.put("timeSpentSeconds", sp.getTimeSpentSeconds());
                    progressMap.put("startDateTime", sp.getStartDateTime());
                    progressMap.put("endDateTime", sp.getEndDateTime());

                    Map<String, Object> userMap = new LinkedHashMap<>();
                    userMap.put("userid", sp.getUser().getUserid());
                    userMap.put("trngid", sp.getUser().getTrngid());
                    userMap.put("firstname", sp.getUser().getFirstname());
                    userMap.put("lastname", sp.getUser().getLastname());
                    userMap.put("email", sp.getUser().getEmailid());
                    progressMap.put("user", userMap);

                    return progressMap;
                }).toList();

                subTopicMap.put("stepProgress", progressResponseList);

                //  Calculate overall subtopic completion
                boolean subCompleted = progressList.stream().allMatch(p -> p.isComplete() && p.isChecker());
                subTopicMap.put("completed", subCompleted);

                subTopicsList.add(subTopicMap);
            }

            syllabusMap.put("subTopics", subTopicsList);
            finalResponse.add(syllabusMap);
        }

        return finalResponse;
    }

    public List<Map<String, Object>> getAllSyllabusProgress(String managerUserId) {

    	 List<User> managerTrainees =
    	            userRepository.findByManagerData_UseridAndDelFlag(managerUserId, "N");

    	    if (managerTrainees == null || managerTrainees.isEmpty()) {
    	        return Collections.emptyList();
    	    }

    	    //  Convert to Set of trainee IDs (trngid)
    	    Set<String> managerTraineeIds = managerTrainees.stream()
    	            .map(User::getTrngid)
    	            .collect(Collectors.toSet());

    	    List<Syllabus> syllabusList = syllabusRepository.findAll();
    	    if (syllabusList == null || syllabusList.isEmpty()) {
    	        return Collections.emptyList();
    	    }
        List<Map<String, Object>> finalResponse = new ArrayList<>();

        for (Syllabus syllabus : syllabusList) {

            Map<String, Object> syllabusMap = new LinkedHashMap<>();
            syllabusMap.put("syllabusId", syllabus.getId());
            syllabusMap.put("title", syllabus.getTitle());
            syllabusMap.put("topic", syllabus.getTopic());
            syllabusMap.put("durationInDays", syllabus.getDurationInDays());

            // Departments
            List<Long> syllabusDeptIds = syllabus.getDepartments()
                    .stream()
                    .map(Department::getId)
                    .toList();

            List<Map<String, Object>> departmentsList = syllabus.getDepartments().stream().map(dept -> {
                Map<String, Object> deptMap = new LinkedHashMap<>();
                deptMap.put("departmentId", dept.getId());
                deptMap.put("name", dept.getName());
                return deptMap;
            }).toList();

            syllabusMap.put("departments", departmentsList);

            // Trainers
            List<Map<String, Object>> trainersList = syllabus.getManagers().stream().map(tr -> {
                Map<String, Object> trainerMap = new LinkedHashMap<>();
                trainerMap.put("trainerId", tr.getTrngid());
                trainerMap.put("name", tr.getFirstname() + " " + tr.getLastname());
                trainerMap.put("email", tr.getEmailid());
                return trainerMap;
           }).toList();

           syllabusMap.put("trainers", trainersList);
            
//            if (syllabus.getManager() != null) {
//
//                User tr = syllabus.getManager();
//
//                Map<String, Object> trainerMap = new LinkedHashMap<>();
//                trainerMap.put("trainerId", tr.getTrngid());
//                trainerMap.put("name", tr.getFirstname() + " " + tr.getLastname());
//                trainerMap.put("email", tr.getEmailid());
//
//                syllabusMap.put("trainer", trainerMap); // single object
//            } else {
//                syllabusMap.put("trainer", null);
//            }


            //  Fetch progress for this syllabus
            List<StepProgress> progressList =
                    stepProgressRepository.findBySubTopic_Syllabus_Id(syllabus.getId());

            Map<Long, List<StepProgress>> progressBySubTopic = new HashMap<>();

            for (StepProgress sp : progressList) {

                User user = sp.getUser();
                if (user == null) continue;

                //  Only manager assigned trainees allowed
                if (!managerTraineeIds.contains(user.getTrngid())) continue;

                //  Department match check
                List<Long> traineeDeptIds =
                        trianeedepartmentRepository
                                .findDepartmentIdsByTraineeTrngid(user.getTrngid());

                boolean matchesDepartment = traineeDeptIds.stream()
                        .anyMatch(syllabusDeptIds::contains);

                if (!matchesDepartment) continue;

                progressBySubTopic
                        .computeIfAbsent(sp.getSubTopic().getId(), k -> new ArrayList<>())
                        .add(sp);
            }

            // Subtopics
            List<Map<String, Object>> subTopicsList = new ArrayList<>();

            for (SubTopic st : syllabus.getSubTopics()) {

                Map<String, Object> subTopicMap = new LinkedHashMap<>();
                subTopicMap.put("subTopicId", st.getId());
                subTopicMap.put("stepNumber", st.getStepNumber());
                subTopicMap.put("name", st.getName());
                subTopicMap.put("description", st.getDescription());
                subTopicMap.put("filePath", st.getFilePath());

                List<StepProgress> subTopicProgress =
                        progressBySubTopic.getOrDefault(st.getId(), Collections.emptyList());

                List<Map<String, Object>> progressResponseList = new ArrayList<>();

                for (StepProgress sp : subTopicProgress) {

                    Map<String, Object> progressMap = new LinkedHashMap<>();
                    progressMap.put("stepProgressId", sp.getId());
                    progressMap.put("complete", sp.isComplete());
                    progressMap.put("checker", sp.isChecker());
                    progressMap.put("review", sp.getReview());
                    progressMap.put("startDateTime", sp.getStartDateTime());
                    progressMap.put("endDateTime", sp.getEndDateTime());

                    User u = sp.getUser();
                    Map<String, Object> userMap = new LinkedHashMap<>();
                    userMap.put("userid", u.getUserid());
                    userMap.put("trngid", u.getTrngid());
                    userMap.put("firstname", u.getFirstname());
                    userMap.put("lastname", u.getLastname());
                    userMap.put("email", u.getEmailid());
                    userMap.put("designation", u.getDesignation());

                    progressMap.put("user", userMap);
                    progressResponseList.add(progressMap);
                }

                subTopicMap.put("stepProgress", progressResponseList);
                subTopicsList.add(subTopicMap);
            }

            syllabusMap.put("subTopics", subTopicsList);
            finalResponse.add(syllabusMap);
        }

        return finalResponse;
    }


    public List<Map<String, Object>> getAllSyllabusProgressByTrainer(String trainerId) {

        List<Syllabus> syllabusList = syllabusRepository.findAll();
        if (syllabusList == null || syllabusList.isEmpty()) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> finalResponse = new ArrayList<>();

        for (Syllabus syllabus : syllabusList) {

            //  Check trainer exists in managers set
            if (syllabus.getManagers() == null || syllabus.getManagers().isEmpty()) {
                continue;
            }

            boolean trainerMatch = syllabus.getManagers()
                    .stream()
                    .anyMatch(t -> trainerId.equals(t.getTrngid()));

            if (!trainerMatch) {
                continue;
            }

            Map<String, Object> syllabusMap = new LinkedHashMap<>();
            syllabusMap.put("syllabusId", syllabus.getId());
            syllabusMap.put("title", syllabus.getTitle());
            syllabusMap.put("topic", syllabus.getTopic());
            syllabusMap.put("durationInDays", syllabus.getDurationInDays());

            // Departments
            List<Long> syllabusDeptIds = syllabus.getDepartments()
                    .stream()
                    .map(Department::getId)
                    .toList();

            List<Map<String, Object>> departmentsList = syllabus.getDepartments()
                    .stream()
                    .map(dept -> {
                        Map<String, Object> deptMap = new LinkedHashMap<>();
                        deptMap.put("departmentId", dept.getId());
                        deptMap.put("name", dept.getName());
                        return deptMap;
                    }).toList();

            syllabusMap.put("departments", departmentsList);

            //  Trainers list (multiple)
            List<Map<String, Object>> trainerList = syllabus.getManagers()
                    .stream()
                    .map(tr -> {
                        Map<String, Object> trainerMap = new LinkedHashMap<>();
                        trainerMap.put("trainerId", tr.getTrngid());
                        trainerMap.put("name", tr.getFirstname() + " " + tr.getLastname());
                        trainerMap.put("email", tr.getEmailid());
                        return trainerMap;
                    }).toList();

            syllabusMap.put("trainers", trainerList);

            // Fetch progress
            List<StepProgress> progressList =
                    stepProgressRepository.findBySubTopic_Syllabus_Id(syllabus.getId());

            Map<Long, List<StepProgress>> progressBySubTopic = new HashMap<>();

            for (StepProgress sp : progressList) {

                User user = sp.getUser();
                if (user == null) continue;

                List<Long> traineeDeptIds = trianeedepartmentRepository
                        .findDepartmentIdsByTraineeTrngid(user.getTrngid());

                boolean matchesDepartment = traineeDeptIds.stream()
                        .anyMatch(syllabusDeptIds::contains);

                if (!matchesDepartment) continue;

                progressBySubTopic
                        .computeIfAbsent(sp.getSubTopic().getId(), k -> new ArrayList<>())
                        .add(sp);
            }

            // Subtopics
            List<Map<String, Object>> subTopicsList = new ArrayList<>();

            for (SubTopic st : syllabus.getSubTopics()) {

                Map<String, Object> subTopicMap = new LinkedHashMap<>();
                subTopicMap.put("subTopicId", st.getId());
                subTopicMap.put("stepNumber", st.getStepNumber());
                subTopicMap.put("name", st.getName());
                subTopicMap.put("description", st.getDescription());
                subTopicMap.put("filePath", st.getFilePath());

                List<StepProgress> subTopicProgress =
                        progressBySubTopic.getOrDefault(st.getId(), Collections.emptyList());

                List<Map<String, Object>> progressResponseList = new ArrayList<>();

                for (StepProgress sp : subTopicProgress) {

                    Map<String, Object> progressMap = new LinkedHashMap<>();
                    progressMap.put("stepProgressId", sp.getId());
                    progressMap.put("complete", sp.isComplete());
                    progressMap.put("checker", sp.isChecker());
                    progressMap.put("review", sp.getReview());
                    progressMap.put("startDateTime", sp.getStartDateTime());
                    progressMap.put("endDateTime", sp.getEndDateTime());

                    User u = sp.getUser();

                    Map<String, Object> userMap = new LinkedHashMap<>();
                    userMap.put("userid", u.getUserid());
                    userMap.put("trngid", u.getTrngid());
                    userMap.put("firstname", u.getFirstname());
                    userMap.put("lastname", u.getLastname());
                    userMap.put("email", u.getEmailid());
                    userMap.put("designation", u.getDesignation());

                    progressMap.put("user", userMap);
                    progressResponseList.add(progressMap);
                }

                subTopicMap.put("stepProgress", progressResponseList);
                subTopicsList.add(subTopicMap);
            }

            syllabusMap.put("subTopics", subTopicsList);
            finalResponse.add(syllabusMap);
        }

        return finalResponse;
    }

   
}

