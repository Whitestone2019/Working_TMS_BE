package whitestone.trainee_management.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.transaction.Transactional;
import whitestone.trainee_management.models.Role;
import whitestone.trainee_management.models.User;
import whitestone.trainee_management.payload.ApiResponse;
import whitestone.trainee_management.repository.RoleRepository;
import whitestone.trainee_management.repository.UserRepository;
import whitestone.trainee_management.service.TraineeAssessmentService;
import whitestone.trainee_management.service.UserService;


@RestController


@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TraineeAssessmentService traineeAssessmentService;

	@Autowired
	private RoleRepository roleRepository;

	@PostMapping("/create")
	public ApiResponse createUser(@RequestBody User user) {
		return userService.createUser(user);
	}



	@PostMapping("/bulk-create")
	@Transactional
	public List<User> createUsers(@RequestBody List<User> users) {

	    //  Fetch all existing userIds in ONE DB call
	    Set<String> existingUserIds = userRepository.findAllUserIds();

	    List<User> usersToSave = new ArrayList<>();

	    for (User user : users) {

	        // Validate roleId
	        if (user.getRoleId() == null) {
	            throw new RuntimeException("Role ID is missing for user: " + user.getUserid());
	        }

	        Role role = roleRepository.findByRoleId(user.getRoleId())
	                .orElseThrow(() ->
	                        new RuntimeException("Invalid Role ID: " + user.getRoleId())
	                );

	        // UPSERT logic
	        if (existingUserIds.contains(user.getUserid())) {

	            // UPDATE existing user
	            User existingUser = userRepository
	                    .findByUserid(user.getUserid())
	                    .orElseThrow();

	            existingUser.setTrngid(user.getTrngid());
	            existingUser.setUsername(user.getUsername());
	            existingUser.setFirstname(user.getFirstname());
	            existingUser.setLastname(user.getLastname());
	            existingUser.setEmailid(user.getEmailid());
	            existingUser.setPhonenumber(user.getPhonenumber());
	           
	            existingUser.setRole(role);

	            //  Update password only if needed
	            if (user.getPassword() != null) {
	                existingUser.setPassword(user.getPassword());
	            }

	            usersToSave.add(existingUser);

	        } else {
	            // INSERT new user
	            user.setRole(role);
	            usersToSave.add(user);
	        }
	    }

	    //  Save in bulk
	    return userRepository.saveAll(usersToSave);
	}

	@GetMapping("/all")
	public ApiResponse getAllUsers() {
		return userService.getAllUsers();
	}

	@GetMapping("/{userid}")
	public ApiResponse getUserById(@PathVariable String userid) {
		return userService.getUser(userid);
	}

	@GetMapping("/summary")
	public ApiResponse getSummary() {
		return traineeAssessmentService.getTraineeSummary();
	}

	@PostMapping("/login")
	public ApiResponse login(@RequestBody Map<String, String> body) {

		String trngId = body.get("trngId");
		String password = body.get("password");

		ApiResponse response = userService.login(trngId, password);
		return response;
	}

	@PostMapping("/send-otp")
	public ApiResponse sendOtp(@RequestBody Map<String, String> body) {
		String trngId = body.get("trngId");
		if (trngId == null || trngId.isEmpty()) {
			return new ApiResponse(400, false, "Trainee ID is required", null);
		}
		return userService.sendOtp(trngId);
	}

	@PostMapping("/verify-otp")
	public ApiResponse verifyOtp(@RequestBody Map<String, String> body) {
		String trngId = body.get("trngId");
		String otp = body.get("otp");
		String newPassword = body.get("newPassword");

		if (trngId == null || otp == null || newPassword == null || trngId.isEmpty() || otp.isEmpty()
				|| newPassword.isEmpty()) {
			return new ApiResponse(400, false, "Trainee ID, OTP, and new password are required", null);
		}
		return userService.verifyOtpAndResetPassword(trngId, otp, newPassword);
	}


	@GetMapping("/{managerUserId}/trainee-summary")
	public ResponseEntity<ApiResponse> getManagerTraineeSummary(@PathVariable String managerUserId) {
	    return ResponseEntity.ok(traineeAssessmentService.getTraineeSummaryByManager(managerUserId));
	}
	
	 @GetMapping("/all-trainee/summary")
	    public ResponseEntity<ApiResponse> getAllTraineeSummary() {

	        ApiResponse response = traineeAssessmentService.getAllTraineeSummary();

	        return ResponseEntity.status(response.getStatus()).body(response);
	    }
	
	@GetMapping("/manager/{managerId}")
	public ApiResponse getTraineesByManager(@PathVariable String managerId) {
	    // Note: use the new method name here
	    List<User> trainees = userRepository.findByManagerData_UseridAndDelFlag(managerId, "N");
	    return new ApiResponse(200, true, "Fetched successfully", trainees);
	}
	
	@GetMapping("/manager/all")
    public ApiResponse getAllManagers() {
        return userService.getAllManagers();
    }
	@GetMapping("/addtrainee/{trngid}")
    public User getTrainee(@PathVariable String trngid) {
        return userService.getTraineeById(trngid);
    }

	

	@PostMapping("/addtrainees")
	public User addTrainees(@RequestBody User user) {
	    return userService.addTrainees(user);
	}

	@Transactional
    @PutMapping("/addtrainee/{trngid}")
    public User updateTrainee(@PathVariable String trngid, @RequestBody User user) {
        return userService.updateTrainee(trngid, user);
    }

	@DeleteMapping("/delete/{trngid}")
	public ResponseEntity<?> deleteTrainee(@PathVariable String trngid) {

	    userService.deleteTraineeByTrngid(trngid);

	    return ResponseEntity.ok("Trainee deleted successfully");
	}
}
