package whitestone.trainee_management.models;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User extends AuditModel {

	@Id
	
	@Column(length = 20)
	private String userid;

	@Column(length = 20, unique = true, nullable = false)
	private String trngid;

	@Column(nullable = false)
	private String password;
	
	private String username;
	private String firstname;
	private String lastname;

	@Column(length = 150)
	private String emailid;

	@Column(length = 20)
	private String phonenumber;

	@Column(length = 20)
	private String designation;


	private String otp;
	private LocalDateTime otpExpiry;
	
	private LocalDateTime registrationDate; 
	
	
	
	@Transient
	private String roleId;

	// Actual Role mapping
	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;
	
	@ManyToOne
	@JoinColumn(name = "manager_id")
	@JsonIgnoreProperties("trainees")
	
	 
	
	private User managerData;
	
	
	public User getManagerData() {
		return managerData;
	}

	public void setManagerData(User managerData) {
		this.managerData = managerData;
	}

	public LocalDateTime getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(LocalDateTime registrationDate) {
		this.registrationDate = registrationDate;
	}
	
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getTrngid() {
		return trngid;
	}

	public void setTrngid(String trngid) {
		this.trngid = trngid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmailid() {
		return emailid;
	}

	public void setEmailid(String emailid) {
		this.emailid = emailid;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public LocalDateTime getOtpExpiry() {
		return otpExpiry;
	}

	public void setOtpExpiry(LocalDateTime otpExpiry) {
		this.otpExpiry = otpExpiry;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
	
	
}
