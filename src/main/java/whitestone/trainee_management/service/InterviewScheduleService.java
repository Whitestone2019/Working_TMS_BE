package whitestone.trainee_management.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import whitestone.trainee_management.models.InterviewSchedule;
import whitestone.trainee_management.models.ScheduleTraineeMap;
import whitestone.trainee_management.models.SubTopic;
import whitestone.trainee_management.models.Syllabus;

import whitestone.trainee_management.models.User;
import whitestone.trainee_management.repository.InterviewScheduleRepository;
import whitestone.trainee_management.repository.ScheduleTraineeMapRepository;
import whitestone.trainee_management.repository.SubTopicRepository;

import whitestone.trainee_management.repository.UserRepository;

@Service
public class InterviewScheduleService {

	@Autowired
	private InterviewScheduleRepository interviewScheduleRepository;

	

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ScheduleTraineeMapRepository scheduleTraineeMapRepository;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private SubTopicRepository subTopicRepo;

//	@Autowired
//	private GoogleMeetService googleMeetService;

	

	private void sendInterviewEmail(String email, String scheduleDate, String scheduleTime) {

		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setTo(email);
			helper.setSubject("Interview Status Update");
			helper.setFrom("career@whitestones.co.in");

			String html = "<html><body style='font-family:Arial,sans-serif; line-height:1.6;'>" +

					"<div style='max-width:600px;margin:auto;padding:20px;border-radius:12px;"
					+ "background:#ffffff;box-shadow:0 2px 10px rgba(0,0,0,0.1)'>" +

					"<h2 style='color:#2B547E;text-align:center;'>Interview Status Update</h2>" +

					"<p>Hello,</p>" + "<p><b>Interview Date:</b> " + scheduleDate + "</p>"
					+ "<p><b>Interview Time:</b> " + scheduleTime + "</p>" +

//					(meetingLink != null && !meetingLink.isEmpty() && !meetingLink.equals("null")
//							? "<p><b>Meeting Link:</b> <a href='" + meetingLink
//									+ "' style='color:#1a73e8;font-weight:bold;'>Join Meeting</a></p>"
//							: "")
//					+

					"<p style='margin-top:25px;'>Regards,<br>" + "<b>Whitestone Trainee Management Team</b></p>" +

					"</div></body></html>";

			helper.setText(html, true);
			mailSender.send(message);

		} catch (Exception e) {
			throw new RuntimeException("Status email sending failed: " + e.getMessage());
		}
	}

//	public InterviewSchedule createSchedule(InterviewSchedule schedule, String trainerId) {
//		System.out.println(schedule);
//		System.out.println("Meeting Date"+schedule.getDate());
//
//		User trainer = userRepository.findByUseridAndRole_IsManagerTrueAndDelFlag(trainerId, "N")
//				.orElseThrow(() -> new RuntimeException("Manager not found or user is not a manager"));
//
//		if (trainer == null) {
//			throw new RuntimeException("Trainer not found or user is not a trainer");
//		}
//
//		schedule.setManagerId(trainer);
//
//		System.out.println(trainer);
//		List<Long> subTopicIds = schedule.getSubTopicIds();
//
//		System.out.println("HIi " + subTopicIds);
//		if (subTopicIds != null && !subTopicIds.isEmpty()) {
//
//			List<SubTopic> subTopics = subTopicRepo.findAllById(subTopicIds);
//			if (subTopics.size() != subTopicIds.size()) {
//				throw new RuntimeException("One or more SubTopics not found");
//			}
//
//			String subtopicStr = subTopicIds.stream().map(String::valueOf).collect(Collectors.joining("|"));
//
//			schedule.setSubTopics(subtopicStr);
//		}
////		schedule.setTrainer(trainer);
//
//		System.out.println(schedule);
////		log.info("Interview schedule created successfully: {}", schedule);
//
//		return interviewScheduleRepository.save(schedule);
//	}
	
	public InterviewSchedule createSchedule(InterviewSchedule schedule, List<String> trainerIds) {
	    System.out.println(schedule);
	    System.out.println("Meeting Date: " + schedule.getDate());

	    // Fetch all managers from the trainerIds list
	    List<User> managers = userRepository.findAllByUseridInAndRole_IsManagerTrueAndDelFlag(trainerIds, "N");

	    if (managers == null || managers.isEmpty()) {
	        throw new RuntimeException("No valid managers found for the given IDs");
	    }

	    // Set managers list
	    schedule.setManagers(managers);

	    System.out.println("Assigned Managers: " + managers);

	    // Handle subTopics
	    List<Long> subTopicIds = schedule.getSubTopicIds();
	    System.out.println("SubTopic IDs: " + subTopicIds);

	    if (subTopicIds != null && !subTopicIds.isEmpty()) {
	        List<SubTopic> subTopics = subTopicRepo.findAllById(subTopicIds);
	        if (subTopics.size() != subTopicIds.size()) {
	            throw new RuntimeException("One or more SubTopics not found");
	        }

	        String subtopicStr = subTopicIds.stream()
	                .map(String::valueOf)
	                .collect(Collectors.joining("|"));

	        schedule.setSubTopics(subtopicStr);
	    }

	    System.out.println("Final Schedule: " + schedule);

	    return interviewScheduleRepository.save(schedule);
	}

	// Assign Trainees to Schedule
//	public String assignTrainees(Long scheduleId, List<String> empids) {
//		InterviewSchedule schedule = interviewScheduleRepository.findById(scheduleId)
//				.orElseThrow(() -> new RuntimeException("Schedule Not Found"));
//
//		System.out.print(empids);
//		List<String> emails = new ArrayList<>();
//
//
//		for (String empid : empids) {
//			User trainee = userRepository.findByTrngidAndDelFlag(empid, "N")
//					.orElseThrow(() -> new RuntimeException("User not found for empId: " + empid));
//
//			ScheduleTraineeMap map = new ScheduleTraineeMap();
//			map.setInterviewSchedule(schedule);
//
//			map.setUser(trainee);
//
//			String role = trainee.getRole().isManager() ? "MANAGER" : "TRAINEE";
//			map.setRoleRvsp(role);
//
//			scheduleTraineeMapRepository.save(map);
//
//			emails.add(trainee.getEmailid());
//
//		}
//
//		System.out.println("dsff" + emails);
////////		String link = "";
////////		try {
////////			String start = schedule.getDate() + "T" + schedule.getTime() + ":00+05:30";
////////			String end = schedule.getDate() + "T" + schedule.getTime().plusMinutes(30) + ":00+05:30";
////////
////////			System.out.println(emails);
////////			GoogleMeetService.MeetEvent meetEvent = googleMeetService.generateMeetLink(start, end, emails);
////////
////////			schedule.setMeetingLink(meetEvent.getMeetLink());
////////			interviewScheduleRepository.save(schedule);
////////
////////			List<Map<String, String>> rsvpStatus = googleMeetService.getRSVPStatus(meetEvent.getEventId());
////////			rsvpStatus.forEach(s -> System.out.println(s.get("email") + " : " + s.get("status")));
////////
////////			List<ScheduleTraineeMap> mappingList = scheduleTraineeMapRepository
////////					.findByInterviewScheduleScheduleId(scheduleId);
////////			for (ScheduleTraineeMap map : mappingList) {
////////				map.setEventId(meetEvent.getEventId());
////////				scheduleTraineeMapRepository.save(map);
////////			}
////////
////////		} catch (Exception e) {
////////			throw new RuntimeException("Meet link creation failed: " + e.getMessage());
////////		}
//
////////		schedule.setMeetingLink(link);
//
//	List<ScheduleTraineeMap> mappingList = scheduleTraineeMapRepository
//				.findByInterviewScheduleScheduleId(scheduleId);
//
////		User trainer = schedule.getManagerId();
////		String trainerEmail = trainer != null ? trainer.getEmailid() : "nerdm09@gmail.com";
//	
//List<User> managers = schedule.getManagers(); // ManyToMany list
//    
//    if (managers != null && !managers.isEmpty()) {
//        for (User manager : managers) {
//            if (manager.getEmailid() != null) {
//                // Har manager ko email jayega
//                sendInterviewEmail(manager.getEmailid(), date, time);
//            }
//        }
//    } else {
//        // Fallback agar koi manager assigned nahi hai
//        sendInterviewEmail("nerdm09@gmail.com", date, time);
//    }
//
//		List<String> traineeEmails = mappingList.stream().map(m -> m.getUser().getEmailid()).filter(Objects::nonNull)				.
//				distinct().collect(Collectors.toList());
//
//	String date = schedule.getDate().toString();
//
//		String time = schedule.getTime().toString();
//	//String meetLink = schedule.getMeetingLink().toString();
//		String meetLink=null;
//
//		if (trainerEmail != null) {
//			sendInterviewEmail(trainerEmail, date, time);
//		}
//
//		for (String email : traineeEmails) {
//			sendInterviewEmail(email, date, time);
//		}
//
//		// return "Trainees Assigned Successfully";
//		return "Trainees Assigned Successfully & Emails Sent";
//	}
	
//	
//	public String assignTrainees(Long scheduleId, List<String> empids) {
//        InterviewSchedule schedule = interviewScheduleRepository.findById(scheduleId)
//                .orElseThrow(() -> new RuntimeException("Schedule Not Found"));
//
//        String date = schedule.getDate().toString();
//        String time = schedule.getTime().toString();
//        List<String> traineeEmails = new ArrayList<>();
//
//        // 1. Assign trainees and collect emails
//        for (String empid : empids) {
////            User trainee = userRepository.findByTrngidAndDelFlag(empid, "N")
////                    .orElseThrow(() -> new RuntimeException("User not found for empId: " + empid));
//        	User trainee = userRepository
//        		    .findByTrngidAndDelFlag(empid, "N")
//        		    .orElseGet(() -> 
//        		        userRepository.findByUseridAndDelFlag(empid, "N").orElse(null)
//        		    );
//
//        		if (trainee == null) {
//        		    throw new RuntimeException("User not found for empId: " + empid);
//        		}
//
//            ScheduleTraineeMap map = new ScheduleTraineeMap();
//            map.setInterviewSchedule(schedule);
//            map.setUser(trainee);
//            
//            String role = (trainee.getRole() != null && trainee.getRole().isManager()) ? "MANAGER" : "TRAINEE";
//            map.setRoleRvsp(role);
//
//            scheduleTraineeMapRepository.save(map);
//            if (trainee.getEmailid() != null) {
//                traineeEmails.add(trainee.getEmailid());
//            }
//        }
//
//        // 2. Send Emails to all assigned Managers
//        List<User> managers = schedule.getManagers();
//        if (managers != null && !managers.isEmpty()) {
//            for (User manager : managers) {
//                if (manager.getEmailid() != null) {
//                    sendInterviewEmail(manager.getEmailid(), date, time);
//                }
//            }
//        } 
//        // 3. Send Emails to all Trainees
//        for (String email : traineeEmails) {
//            sendInterviewEmail(email, date, time);
//        }
//
//        return "Trainees Assigned Successfully & Emails Sent to all participants";
//    }\\
	
	

	@Transactional
	public String assignTrainees(Long scheduleId, List<String> empids) {
	    // 1. Validate Schedule
	    InterviewSchedule schedule = interviewScheduleRepository.findById(scheduleId)
	            .orElseThrow(() -> new RuntimeException("Schedule Not Found with ID: " + scheduleId));

	    String date = schedule.getDate().toString();
	    String time = schedule.getTime().toString();
	    List<String> participantEmails = new ArrayList<>();

	    // 2. Process all IDs (Trainees and Interviewers)
	    for (String empid : empids) {
	        // Find user by either Trainee ID or User ID
	        User participant = userRepository.findByTrngidAndDelFlag(empid, "N")
	                .orElseGet(() -> userRepository.findByUseridAndDelFlag(empid, "N").orElse(null));

	        if (participant == null) {
	            // Log it and continue or throw error based on your strictness
	            System.out.println("Warning: User not found for ID: " + empid);
	            continue; 
	        }

	        // Check if already assigned to avoid duplicates
	        boolean alreadyAssigned = scheduleTraineeMapRepository.existsByInterviewScheduleAndUser(schedule, participant);
	        
	        if (!alreadyAssigned) {
	            ScheduleTraineeMap map = new ScheduleTraineeMap();
	            map.setInterviewSchedule(schedule);
	            map.setUser(participant);
	            
	            // Determine Role for the mapping table
	            String role = (participant.getRole() != null && participant.getRole().isManager()) ? "MANAGER" : "TRAINEE";
	            map.setRoleRvsp(role);

	            scheduleTraineeMapRepository.save(map);
	        }

	        if (participant.getEmailid() != null) {
	            participantEmails.add(participant.getEmailid());
	        }
	    }

	    // 3. Send Emails to all collected participants (Managers + Trainees)
	    for (String email : participantEmails) {
	        try {
	            sendInterviewEmail(email, date, time);
	        } catch (Exception e) {
	            System.err.println("Failed to send email to: " + email);
	        }
	    }

	    return "Trainees Assigned Successfully & Emails Sent";
	}

	
//	public String assignTrainees(Long scheduleId, List<String> empids) {
//	    InterviewSchedule schedule = interviewScheduleRepository.findById(scheduleId)
//	            .orElseThrow(() -> new RuntimeException("Schedule Not Found"));
//
//	    System.out.print(empids);
//	    List<String> emails = new ArrayList<>();
//
//	    // Assign trainees
//	    for (String empid : empids) {
//	        User trainee = userRepository.findByTrngidAndDelFlag(empid, "N")
//	                .orElseThrow(() -> new RuntimeException("User not found for empId: " + empid));
//
//	        ScheduleTraineeMap map = new ScheduleTraineeMap();
//	        map.setInterviewSchedule(schedule);
//	        map.setUser(trainee);
//
//	        String role = trainee.getRole().isManager() ? "MANAGER" : "TRAINEE";
//	        map.setRoleRvsp(role);
//
//	        scheduleTraineeMapRepository.save(map);
//	        emails.add(trainee.getEmailid());
//	    }
//
//	    System.out.println("Trainee Emails: " + emails);
//
//	    // Get all managers (previously single)
//	    List<User> managers = schedule.getManagers(); // <-- updated entity field
//	    if (managers == null) managers = new ArrayList<>();
//
//	    List<String> managerEmails = managers.stream()
//	            .map(User::getEmailid)
//	            .filter(Objects::nonNull)
//	            .distinct()
//	            .collect(Collectors.toList());
//
//	    // Send emails to managers
//	    String date = schedule.getDate().toString();
//	    String time = schedule.getTime().toString();
//
//	    for (String email : managerEmails) {
//	        sendInterviewEmail(email, date, time);
//	    }
//
//	    // Send emails to trainees
//	    for (String email : emails) {
//	        sendInterviewEmail(email, date, time);
//	    }
//
//	    return "Trainees Assigned Successfully & Emails Sent";
//	}

//	public InterviewSchedule updateSchedule(Long scheduleId, InterviewSchedule updatedSchedule, String trainerId,
//			List<String> empids) {
//
//		System.out.println(scheduleId);
//
//		System.out.println("SubTopicIds: " + updatedSchedule.getSubTopicIds());
//		System.out.println("Date: " + updatedSchedule.getDate());
//		System.out.println("Time: " + updatedSchedule.getTime());
//
//		System.out.println(updatedSchedule.getNotes());
//
//		System.out.println(trainerId);
//
//		System.out.println(empids);
//
//		InterviewSchedule existingSchedule = interviewScheduleRepository.findById(scheduleId)
//				.orElseThrow(() -> new RuntimeException("Interview Schedule not found with ID: " + scheduleId));
//
//		//  Update Trainer
//		if (trainerId != null) {
//			User trainer = userRepository.findByTrngidAndDelFlag(trainerId, "N")
//					.orElseThrow(() -> new RuntimeException("Trainer not found with ID: " + trainerId));
//			existingSchedule.setManagerId(trainer);
//		}
//
//		//  Update Date
//		if (updatedSchedule.getDate() != null) {
//			existingSchedule.setDate(updatedSchedule.getDate());
//		}
//
//		//  Update Time
//		if (updatedSchedule.getTime() != null) {
//			existingSchedule.setTime(updatedSchedule.getTime());
//		}
//
//		//  Update Duration
//		if (updatedSchedule.getDuration() != null) {
//			existingSchedule.setDuration(updatedSchedule.getDuration());
//		}
//
//		//  Update Notes
//		if (updatedSchedule.getNotes() != null) {
//			existingSchedule.setNotes(updatedSchedule.getNotes());
//		}
//
//		//  Update Interview Type
//		if (updatedSchedule.getInterviewType() != null) {
//			existingSchedule.setInterviewType(updatedSchedule.getInterviewType());
//		}
//
//		//  Update Location
//		if (updatedSchedule.getLocation() != null) {
//			existingSchedule.setLocation(updatedSchedule.getLocation());
//		}
//
//		//  Update SubTopics
//		List<Long> subTopicIds = updatedSchedule.getSubTopicIds();
//		if (subTopicIds != null && !subTopicIds.isEmpty()) {
//
//			List<SubTopic> subTopics = subTopicRepo.findAllById(subTopicIds);
//			if (subTopics.size() != subTopicIds.size()) {
//				throw new RuntimeException("One or more SubTopics not found");
//			}
//
//			String subtopicStr = subTopicIds.stream().map(String::valueOf).collect(Collectors.joining("|"));
//
//			existingSchedule.setSubTopics(subtopicStr);
//		}
//
//		//  Save updated schedule
//		InterviewSchedule savedSchedule = interviewScheduleRepository.save(existingSchedule);
//
//		//  Reassign trainees (uses existing assignTrainees logic)
//
//		if (empids != null && !empids.isEmpty()) {
//			List<ScheduleTraineeMap> mappingList = scheduleTraineeMapRepository
//					.findByInterviewScheduleScheduleId(scheduleId);
//
//			List<String> traineeEmails = mappingList.stream().map(m -> m.getUser().getEmailid())
//					.filter(Objects::nonNull).distinct().collect(Collectors.toList());
//
//			User trainer = savedSchedule.getManagerId();
//			String trainerEmail = trainer.getEmailid();
//
//			System.out.println(trainerEmail);
//
//			String date = savedSchedule.getDate().toString();
//
//			String time = savedSchedule.getTime().toString();
//
//			//String meetLink = savedSchedule.getMeetingLink().toString();
//			String meetLink = null;
//			if (trainerEmail != null) {
//				sendInterviewEmail(trainerEmail, date, time);
//				System.out.println("Email to Trainer");
//			}
//
//			for (String email : traineeEmails) {
//
//				sendInterviewEmail(email, date, time);
//				System.out.println("Email to traineeesssss" + email);
//			}
//		}
//
//		return savedSchedule;
//	}
	
//	
//	public InterviewSchedule updateSchedule(
//	        Long scheduleId,
//	        InterviewSchedule updatedSchedule,
//	        List<String> trainerIds, // <-- now multiple trainers
//	        List<String> empids) {
//
//	    System.out.println("Schedule ID: " + scheduleId);
//	    System.out.println("SubTopicIds: " + updatedSchedule.getSubTopicIds());
//	    System.out.println("Date: " + updatedSchedule.getDate());
//	    System.out.println("Time: " + updatedSchedule.getTime());
//	    System.out.println("Notes: " + updatedSchedule.getNotes());
//	    System.out.println("Trainer IDs: " + trainerIds);
//	    System.out.println("Employee IDs: " + empids);
//
//	    InterviewSchedule existingSchedule = interviewScheduleRepository.findById(scheduleId)
//	            .orElseThrow(() -> new RuntimeException("Interview Schedule not found with ID: " + scheduleId));
//
//	    // ------------------ UPDATE MANAGERS ------------------
//	    if (trainerIds != null && !trainerIds.isEmpty()) {
//	        List<User> managers = userRepository.findAllByUseridInAndRole_IsManagerTrueAndDelFlag(trainerIds, "N");
//	        if (managers.size() != trainerIds.size()) {
//	            throw new RuntimeException("One or more managers not found or not valid");
//	        }
//	        existingSchedule.setManagers(managers); // <-- updated entity field
//	    }
//
//	    // ------------------ UPDATE DATE, TIME, DURATION ------------------
//	    if (updatedSchedule.getDate() != null) {
//	        existingSchedule.setDate(updatedSchedule.getDate());
//	    }
//	    if (updatedSchedule.getTime() != null) {
//	        existingSchedule.setTime(updatedSchedule.getTime());
//	    }
//	    if (updatedSchedule.getDuration() != null) {
//	        existingSchedule.setDuration(updatedSchedule.getDuration());
//	    }
//
//	    // ------------------ UPDATE NOTES, INTERVIEW TYPE, LOCATION ------------------
//	    if (updatedSchedule.getNotes() != null) {
//	        existingSchedule.setNotes(updatedSchedule.getNotes());
//	    }
//	    if (updatedSchedule.getInterviewType() != null) {
//	        existingSchedule.setInterviewType(updatedSchedule.getInterviewType());
//	    }
//	    if (updatedSchedule.getLocation() != null) {
//	        existingSchedule.setLocation(updatedSchedule.getLocation());
//	    }
//
//	    // ------------------ UPDATE SUBTOPICS ------------------
//	    List<Long> subTopicIds = updatedSchedule.getSubTopicIds();
//	    if (subTopicIds != null && !subTopicIds.isEmpty()) {
//	        List<SubTopic> subTopics = subTopicRepo.findAllById(subTopicIds);
//	        if (subTopics.size() != subTopicIds.size()) {
//	            throw new RuntimeException("One or more SubTopics not found");
//	        }
//	        String subtopicStr = subTopicIds.stream().map(String::valueOf).collect(Collectors.joining("|"));
//	        existingSchedule.setSubTopics(subtopicStr);
//	    }
//
//	    // ------------------ SAVE UPDATED SCHEDULE ------------------
//	    InterviewSchedule savedSchedule = interviewScheduleRepository.save(existingSchedule);
//
//	    // ------------------ SEND EMAILS TO MANAGERS & TRAINEES ------------------
//	    if (empids != null && !empids.isEmpty()) {
//	        List<ScheduleTraineeMap> mappingList = scheduleTraineeMapRepository
//	                .findByInterviewScheduleScheduleId(scheduleId);
//
//	        List<String> traineeEmails = mappingList.stream()
//	                .map(m -> m.getUser().getEmailid())
//	                .filter(Objects::nonNull)
//	                .distinct()
//	                .collect(Collectors.toList());
//
//	        List<String> managerEmails = savedSchedule.getManagers().stream()
//	                .map(User::getEmailid)
//	                .filter(Objects::nonNull)
//	                .distinct()
//	                .collect(Collectors.toList());
//
//	        String date = savedSchedule.getDate().toString();
//	        String time = savedSchedule.getTime().toString();
//
//	        // Send emails to managers
//	        for (String email : managerEmails) {
//	            sendInterviewEmail(email, date, time);
//	            System.out.println("Email sent to Manager: " + email);
//	        }
//
//	        // Send emails to trainees
//	        for (String email : traineeEmails) {
//	            sendInterviewEmail(email, date, time);
//	            System.out.println("Email sent to Trainee: " + email);
//	        }
//	    }
//
//	    return savedSchedule;
//	}

	public Map<Long, Map<String, Object>> getAllSchedulesInUser() {
		List<ScheduleTraineeMap> mappings = scheduleTraineeMapRepository.findAll();
		return mappings.stream().collect(Collectors.groupingBy(m -> m.getInterviewSchedule().getScheduleId(),
				Collectors.collectingAndThen(Collectors.toList(), list -> {

					ScheduleTraineeMap first = list.get(0);

					// Extract schedule and trainer only once
					InterviewSchedule schedule = first.getInterviewSchedule();
					//User trainer = schedule.getManagerId();
					List<User> managers = schedule.getManagers() != null ? schedule.getManagers() : new ArrayList<>();

					// Extract trainees from mapping list
					List<User> trainees = list.stream().map(ScheduleTraineeMap::getUser).distinct()
							.collect(Collectors.toList());

					// Put in map
					Map<String, Object> map = new HashMap<>();
					map.put("schedule", schedule);
					//map.put("trainer", trainer);
					 map.put("managers", managers);
					map.put("trainees", trainees);

					return map;
				})));
	}

	public List<ScheduleTraineeMap> getAllSchedules() {

		return scheduleTraineeMapRepository.findByDelFlag("N");
	}

	public List<ScheduleTraineeMap> getAllSyllabusByEmpid(String empId) {

		User user = userRepository.findByTrngidAndDelFlag(empId, "N")
				.orElseThrow(() -> new RuntimeException("User not found for empId: " + empId));

		return scheduleTraineeMapRepository.findByUserTrngId(empId);

	}

	// Soft delete an interview schedule
	public String deleteSchedule(Long scheduleId) {
		// Fetch the interview schedule
		InterviewSchedule schedule = interviewScheduleRepository.findById(scheduleId)
				.orElseThrow(() -> new RuntimeException("Interview Schedule not found with ID: " + scheduleId));

		// Mark as deleted
		schedule.markDeleted();

		// Save the updated schedule
		interviewScheduleRepository.save(schedule);

		// Optionally, mark related ScheduleTraineeMap records as deleted too
		List<ScheduleTraineeMap> mappings = scheduleTraineeMapRepository.findByInterviewScheduleScheduleId(scheduleId);
		for (ScheduleTraineeMap map : mappings) {
			map.markDeleted(); // assuming ScheduleTraineeMap has delFlag field
			scheduleTraineeMapRepository.save(map);
		}

		return "Interview schedule deleted successfully (soft delete)";
	}

//
//	public InterviewSchedule updateSchedule(Long scheduleId, InterviewSchedule updatedSchedule, List<String> trainerIds,
//			List<String> empids) {
//
//		System.out.println(scheduleId);
//
//		System.out.println("SubTopicIds: " + updatedSchedule.getSubTopicIds());
//		System.out.println("Date: " + updatedSchedule.getDate());
//		System.out.println("Time: " + updatedSchedule.getTime());
//
//		System.out.println(updatedSchedule.getNotes());
//
//		System.out.println(trainerIds);
//
//		System.out.println(empids);
//
//		InterviewSchedule existingSchedule = interviewScheduleRepository.findById(scheduleId)
//				.orElseThrow(() -> new RuntimeException("Interview Schedule not found with ID: " + scheduleId));
//
//		//  Update Trainer
////		if (trainerId != null) {
////			User trainer = userRepository.findByTrngidAndDelFlag(trainerId, "N")
////					.orElseThrow(() -> new RuntimeException("Trainer not found with ID: " + trainerId));
////			existingSchedule.setManagerId(trainer);
////		}
//		
//		if (trainerIds != null && !trainerIds.isEmpty()) {
//	        List<User> trainers = userRepository.findAllByTrngidInAndDelFlag(trainerIds, "N");
//	        if (trainers.size() != trainerIds.size()) {
//	            throw new RuntimeException("One or more Trainers not found");
//	        }
//	        // Purane managers ko clear karke naye add karo
//	        existingSchedule.getManagers().clear();
//	        existingSchedule.getManagers().addAll(trainers);
//	    }
//
//		//  Update Date
//		if (updatedSchedule.getDate() != null) {
//			existingSchedule.setDate(updatedSchedule.getDate());
//		}
//
//		//  Update Time
//		if (updatedSchedule.getTime() != null) {
//			existingSchedule.setTime(updatedSchedule.getTime());
//		}
//
//		//  Update Duration
//		if (updatedSchedule.getDuration() != null) {
//			existingSchedule.setDuration(updatedSchedule.getDuration());
//		}
//
//		//  Update Notes
//		if (updatedSchedule.getNotes() != null) {
//			existingSchedule.setNotes(updatedSchedule.getNotes());
//		}
//
//		//  Update Interview Type
//		if (updatedSchedule.getInterviewType() != null) {
//			existingSchedule.setInterviewType(updatedSchedule.getInterviewType());
//		}
//
//		//  Update Location
//		if (updatedSchedule.getLocation() != null) {
//			existingSchedule.setLocation(updatedSchedule.getLocation());
//		}
//
//		//  Update SubTopics
//		List<Long> subTopicIds = updatedSchedule.getSubTopicIds();
//		if (subTopicIds != null && !subTopicIds.isEmpty()) {
//
//			List<SubTopic> subTopics = subTopicRepo.findAllById(subTopicIds);
//			if (subTopics.size() != subTopicIds.size()) {
//				throw new RuntimeException("One or more SubTopics not found");
//			}
//
//			String subtopicStr = subTopicIds.stream().map(String::valueOf).collect(Collectors.joining("|"));
//
//			existingSchedule.setSubTopics(subtopicStr);
//		}
//
//		//  Save updated schedule
//		InterviewSchedule savedSchedule = interviewScheduleRepository.save(existingSchedule);
//
//		//  Reassign trainees (uses existing assignTrainees logic)
//
//		if (empids != null && !empids.isEmpty()) {
//			List<ScheduleTraineeMap> mappingList = scheduleTraineeMapRepository
//					.findByInterviewScheduleScheduleId(scheduleId);
//
//			List<String> traineeEmails = mappingList.stream().map(m -> m.getUser().getEmailid())
//					.filter(Objects::nonNull).distinct().collect(Collectors.toList());
//
////			User trainer = savedSchedule.getManagerId();
////			String trainerEmail = trainer.getEmailid();
//			
//			for (User manager : savedSchedule.getManagers()) {
//		        if (manager.getEmailid() != null) {
//		            sendInterviewEmail(manager.getEmailid(), dateStr, timeStr);
//		            System.out.println("Email sent to Trainer: " + manager.getEmailid());
//		        }
//		    }
//
//			System.out.println(trainerEmail);
//
//			String date = savedSchedule.getDate().toString();
//
//			String time = savedSchedule.getTime().toString();
//
//			//String meetLink = savedSchedule.getMeetingLink().toString();
//			String meetLink = null;
//			if (trainerEmail != null) {
//				sendInterviewEmail(trainerEmail, date, time);
//				System.out.println("Email to Trainer");
//			}
//
//			for (String email : traineeEmails) {
//
//				sendInterviewEmail(email, date, time);
//				System.out.println("Email to traineeesssss" + email);
//			}
//		}
//
//		return savedSchedule;
//	}
	
	public InterviewSchedule updateSchedule(Long scheduleId, InterviewSchedule updatedSchedule, 
            List<String> trainerIds, List<String> empids) {

// 1. Existing Schedule find karo
InterviewSchedule existingSchedule = interviewScheduleRepository.findById(scheduleId)
.orElseThrow(() -> new RuntimeException("Interview Schedule not found with ID: " + scheduleId));

if (trainerIds != null && !trainerIds.isEmpty()) {

    System.out.println("Trainer IDs from request: " + trainerIds);

    // ✅ FIX: userid se fetch karo
    List<User> trainers = userRepository.findAllByUseridInAndRole_IsManagerTrueAndDelFlag(trainerIds, "N");

    System.out.println("Fetched Trainers: " + trainers);

    if (trainers.isEmpty()) {
        throw new RuntimeException("No valid Trainers found for the provided IDs");
    }

// Clear old list and add new trainers
existingSchedule.getManagers().clear();
existingSchedule.getManagers().addAll(trainers);
}

// 3. Basic Fields Update
if (updatedSchedule.getDate() != null) existingSchedule.setDate(updatedSchedule.getDate());
if (updatedSchedule.getTime() != null) existingSchedule.setTime(updatedSchedule.getTime());
if (updatedSchedule.getDuration() != null) existingSchedule.setDuration(updatedSchedule.getDuration());
if (updatedSchedule.getNotes() != null) existingSchedule.setNotes(updatedSchedule.getNotes());
if (updatedSchedule.getInterviewType() != null) existingSchedule.setInterviewType(updatedSchedule.getInterviewType());
if (updatedSchedule.getLocation() != null) existingSchedule.setLocation(updatedSchedule.getLocation());

// 4. SubTopics Update (String pipe separated format)
List<Long> subTopicIds = updatedSchedule.getSubTopicIds();
if (subTopicIds != null && !subTopicIds.isEmpty()) {
List<SubTopic> subTopics = subTopicRepo.findAllById(subTopicIds);
if (subTopics.size() != subTopicIds.size()) {
throw new RuntimeException("One or more SubTopics not found in database");
}
String subtopicStr = subTopicIds.stream()
.map(String::valueOf)
.collect(Collectors.joining("|"));
existingSchedule.setSubTopics(subtopicStr);
}

// 5. Save updated schedule
InterviewSchedule savedSchedule = interviewScheduleRepository.save(existingSchedule);

// 6. Email Notification Logic
String dateStr = savedSchedule.getDate().toString();
String timeStr = savedSchedule.getTime().toString();

// A. Email to all assigned Managers
for (User manager : savedSchedule.getManagers()) {
if (manager.getEmailid() != null) {
sendInterviewEmail(manager.getEmailid(), dateStr, timeStr);
System.out.println("Email sent to Trainer: " + manager.getEmailid());
}
}

// B. Email to Trainees
// Note: Empids check is for new assignments, 
// but here we are fetching existing mappings from DB
List<ScheduleTraineeMap> mappingList = scheduleTraineeMapRepository
.findByInterviewScheduleScheduleId(scheduleId);

if (!mappingList.isEmpty()) {
List<String> traineeEmails = mappingList.stream()
.map(m -> m.getUser().getEmailid())
.filter(Objects::nonNull)
.distinct()
.collect(Collectors.toList());

for (String email : traineeEmails) {
sendInterviewEmail(email, dateStr, timeStr);
System.out.println("Email sent to trainee: " + email);
}
}

return savedSchedule;
}
}
