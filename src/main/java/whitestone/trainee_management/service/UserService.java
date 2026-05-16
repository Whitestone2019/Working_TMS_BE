package whitestone.trainee_management.service;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.MailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import whitestone.trainee_management.models.Role;
import whitestone.trainee_management.models.TraineeDepartment;
import whitestone.trainee_management.models.User;
import whitestone.trainee_management.repository.RoleRepository;
import whitestone.trainee_management.repository.ScheduleTraineeMapRepository;
import whitestone.trainee_management.repository.StepProgressRepository;
import whitestone.trainee_management.repository.SyllabusFeedbackRepository;
import whitestone.trainee_management.repository.TraineeAssessmentRepository;
import whitestone.trainee_management.repository.TraineeDepartmentRepository;
import whitestone.trainee_management.repository.UserRepository;
import whitestone.trainee_management.payload.ApiResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private  TraineeDepartmentRepository traineeDepartmentRepository;
	
	@Autowired 
	private StepProgressRepository stepProgressRepo;
	
	@Autowired
	private SyllabusFeedbackRepository syllabusFeedbackRepo;
	
	@Autowired
	private TraineeAssessmentRepository traineeAssessmentFormRepo;
	
	@Autowired
	private ScheduleTraineeMapRepository  scheduleUserMapRepo;
	
	@Autowired
	private JavaMailSender mailSender;
	// Create User
	public ApiResponse createUser(User user) {
		userRepository.save(user);
		return new ApiResponse(200, true, "User created successfully", user);
	}

	// Get All Users
	public ApiResponse getAllUsers() {
		List<User> users = userRepository.findByDelFlag("N");
		return new ApiResponse(200, true, "Users fetched successfully", users);
	}

	// Get User by ID
	public ApiResponse getUser(String userid) {
		User user = userRepository.findByTrngidAndDelFlag(userid, "N").orElse(null);
		if (user == null) {
			return new ApiResponse(404, false, "User not found", null);
		}
		return new ApiResponse(200, true, "User fetched successfully", user);
	}

	public ApiResponse login(String trngId, String password) {
		User user = userRepository.findByTrngidAndDelFlag(trngId, "N").orElse(null);
		if (user == null) {
			return new ApiResponse(401, false, "Invalid Trainee ID", null);
		}
		if (!user.getPassword().equals(password)) {
			return new ApiResponse(401, false, "Invalid Password", null);
		}
		
		 if (user.getCreatedAt() == null) {
		        user.setCreatedAt(LocalDateTime.now());
		        userRepository.save(user);  // DB update
		    }

		
		String redirectPath = (user.getRole() != null && user.getRole().isManager())
                ? "/manager-dashboard"
                : "/trainee-dashboard";
		
		

		
		return new ApiResponse(200, true, "Login successful", Map.of("redirect", redirectPath, "user", user));
	}

	public void mailSending(String emailid, String otp) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			
			helper.setTo(emailid);
			helper.setSubject("Your OTP for Trainee Management System");
			helper.setFrom("career@whitestones.co.in");
			
			String html = "<!DOCTYPE html>" + "<html>" + "<head>" + "<style>"
					+ "  .container { font-family: Arial, sans-serif; padding: 20px; text-align: center; }"
					+ "  .otp { font-size: 24px; font-weight: bold; color: #1E3A8A; margin: 20px 0; }"
					+ "  .note { font-size: 14px; color: #555; margin-top: 10px; }" + "</style>" + "</head>" + "<body>"
					+ "  <div class='container'>" + "    <h2>Trainee Management System</h2>"
					+ "    <p>Your OTP for password reset is:</p>" + "    <div class='otp'>" + otp + "</div>"
					+ "    <p class='note'>This OTP is valid for 5 minutes. Do not share it with anyone.</p>"
					+ "  </div>" + "</body>" + "</html>";
			
			helper.setText(html,true);
			mailSender.send(message);
			

		} catch (Exception e) {
			throw new RuntimeException("Status email sending failed: " + e.getMessage());
		}
	}

	public ApiResponse sendOtp(String trngId) {
		User user = userRepository.findByTrngidAndDelFlag(trngId, "N").orElse(null);
		
		if (user == null) {
			return new ApiResponse(404, false, "Trainee ID not found", null);
		}

		int otpCode = (int) (Math.random() * 900000) + 100000;
		String otp = String.valueOf(otpCode);

		user.setOtp(otp);
		user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
		userRepository.save(user);

		// Mock sending OTP via email (replace with actual email/SMS)
		System.out.println("Sending OTP to user: " + user.getEmailid());
		System.out.println("Your OTP is: " + otp);
		mailSending(user.getEmailid(),otp);
		return new ApiResponse(200, true, "OTP sent to your registered email.", null);
	}

	public ApiResponse verifyOtpAndResetPassword(String trngId, String otp, String newPassword) {
		User user = userRepository.findByTrngidAndDelFlag(trngId, "N").orElse(null);
		if (user == null) {
			return new ApiResponse(404, false, "Trainee ID not found", null);
		}

		if (user.getOtp() == null || !user.getOtp().equals(otp)) {
			return new ApiResponse(400, false, "Invalid OTP", null);
		}

		if (user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
			return new ApiResponse(400, false, "OTP has expired", null);
		}

		user.setPassword(newPassword);

		user.setOtp(null);
		user.setOtpExpiry(null);

		userRepository.save(user);

		return new ApiResponse(200, true, "Password reset successful", null);
	}
	
//	public ApiResponse getAllManagers() {
//	    List<User> managers = userRepository.findAllManagers();
//	    return new ApiResponse(200, true, "Managers fetched successfully", managers);
//	}
//	
	public ApiResponse getAllManagers() {

	    List<User> managers = userRepository.findAllManagersExcludingTopRoles();

	    return new ApiResponse(
	            200,
	            true,
	            "Managers fetched successfully",
	            managers
	    );
	}
	public List<Map<String, Object>> getManagerTraineeSummary(String managerId) {
	    // This managerId should be the 'userid' (e.g., "201910053")
	    List<Map<String, Object>> rawData = userRepository.findTraineeSummaryByManager(managerId);
	    List<Map<String, Object>> formattedList = new ArrayList<>();

	    for (Map<String, Object> row : rawData) {
	        Map<String, Object> traineeMap = new LinkedHashMap<>();

	        traineeMap.put("userId", row.get("userId"));
	        traineeMap.put("traineeId", row.get("traineeId"));
	        traineeMap.put("name", row.get("name"));
	        traineeMap.put("email", row.get("email"));

	        if (row.get("lastAssessmentDate") == null) {
	            // Match your specific "Rahul Kadam" example style
	            traineeMap.put("currentStep", "No Assessment Yet");
	            traineeMap.put("completionPercentage", 0);
	            traineeMap.put("interviewStatus", null);
	            traineeMap.put("lastAssessmentScore", null);
	            traineeMap.put("subtopics", new ArrayList<>());
	            traineeMap.put("lastAssessmentDate", "N/A");
	        } else {
	            traineeMap.put("currentStep", "Step " + row.get("currentStep"));
	            traineeMap.put("completionPercentage", row.get("completionPercentage"));
	            
	            // Handle Boolean conversion safely
	            Object status = row.get("interviewStatus");
	            traineeMap.put("interviewStatus", (status instanceof Boolean) ? status : 
	                           (status instanceof Number && ((Number)status).intValue() == 1));
	            
	            traineeMap.put("lastAssessmentScore", row.get("lastAssessmentScore"));
	            traineeMap.put("lastAssessmentDate", row.get("lastAssessmentDate"));

	           
	            String rawTopics = (String) row.get("subTopicsRaw");
	            if (rawTopics != null && !rawTopics.isEmpty()) {
	                traineeMap.put("subtopics", Arrays.asList(rawTopics.split("\\s*,\\s*")));
	            } else {
	                traineeMap.put("subtopics", new ArrayList<>());
	            }
	        }
	        formattedList.add(traineeMap);
	    }
	    return formattedList;
	}
	// Add trainee
	public User addTrainees(User user) {

	    Long maxId = userRepository.findMaxUserId();

	    if (maxId == null) {
	        maxId = 20191000L; // starting ID
	    }

	    Long nextId = maxId + 1;

	    user.setUserid(String.valueOf(nextId));

	    if (user.getRole() != null && user.getRole().getRoleId() != null) {
	        Role role = roleRepository.findById(user.getRole().getRoleId())
	                .orElseThrow(() -> new RuntimeException("Role not found"));
	        user.setRole(role);
	    }

	    return userRepository.save(user);
	}


	// Update Trainee by Training ID
	public User updateTrainee(String trngid, User updatedUser) {

	    User existingUser = userRepository.findByTrngid(trngid)
	            .orElseThrow(() -> new RuntimeException("Trainee not found with trngid: " + trngid));

	    existingUser.setFirstname(updatedUser.getFirstname());
	    existingUser.setLastname(updatedUser.getLastname());
	    existingUser.setUsername(updatedUser.getUsername());
	    existingUser.setEmailid(updatedUser.getEmailid());
	    existingUser.setPhonenumber(updatedUser.getPhonenumber());
	    existingUser.setDesignation(updatedUser.getDesignation());

	    // Update password only if provided
	    if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
	        existingUser.setPassword(updatedUser.getPassword());
	    }

	    // Update Role
	    if (updatedUser.getRole() != null && updatedUser.getRole().getRoleId() != null) {
	        Role role = roleRepository.findById(updatedUser.getRole().getRoleId())
	                .orElseThrow(() -> new RuntimeException("Role not found"));
	        existingUser.setRole(role);
	    }

	    return userRepository.save(existingUser);
	}



	public User getTraineeById(String trngid) {
	    return userRepository.findByTrngid(trngid)
	            .orElseThrow(() -> new RuntimeException("Trainee not found with trngid: " + trngid));
	}

	
	@Transactional
	public void deleteTraineeByTrngid(String trngid) {

	    User user = userRepository.findByTrngid(trngid)
	            .orElseThrow(() -> new RuntimeException("User not found"));
	    
	    scheduleUserMapRepo.deleteByTrngid(trngid);

	    
	    traineeAssessmentFormRepo.deleteByTrngid(trngid);

	    //  StepProgress delete
	    stepProgressRepo.deleteByUser(user);

	    //  SyllabusFeedback delete
	    syllabusFeedbackRepo.deleteByTraineeOrTrainer(user, user);

	    //  TraineeDepartment delete
	    traineeDepartmentRepository.deleteByTrainee(user);

	    //  Finally user delete
	    userRepository.delete(user);
	}
}
