package com.voicera.demoController;


import java.security.SecureRandom;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.voicera.dto.UpdatePasswordDto;
import com.voicera.repository.UserRepository;
import com.voicera.user.User;
import com.voicera.utils.FileUtils;
import com.voicera.utils.SendOtp;



@RestController
@CrossOrigin
public class ForgotPasswordController {
	
	@Autowired
	private SendOtp sendOtp;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private FileUtils fileUtils;
	@Autowired
	private  PasswordEncoder passwordEncoder;

	
	@Value("${LearningAppBaseUrl}")
	private String baseUrl;
	
	 private static final ConcurrentHashMap<String, String> otpMap = new ConcurrentHashMap<>();
	    
	@PostMapping("/forgetPassword/{email}")
	public String endEmail(@PathVariable String email) {
		Optional<User> user = userRepository.findByEmail(email);
		if(user.isPresent()) {
			int otp = new SecureRandom().nextInt(900000) + 100000;
			otpMap.put(email, String.valueOf(otp));
			System.out.println("otp generated for Email:- "+email+" --> "+otp);
			
		 
			// Mail Operation Here
	        String from="udhaarkhaata@gmail.com";
	        String to=email;
	        String subject="Request for reset Learning Portal password";
	        
	        boolean otpSend = sendOtp.otpSend(from, to, subject,formatAssessmentInviteEmail(email,otp));
	             if (otpSend){
					System.out.println("Otp Successfuly Send");
					return "otpsend";
				}else {
					System.out.println("Someting Went Wrong");
					return "otpfailed";
				}	
		}
		
		return "email not exists";
		
		}
	
	@PostMapping("/updatePassword")
	 public boolean verifyOtpAndUpdatePassword(@RequestBody UpdatePasswordDto updatePasswordDto) {
	        String email = updatePasswordDto.getEmail();
	        String otp = updatePasswordDto.getOtp();
	        if (otpMap.containsKey(email) && otpMap.get(email).equals(otp)) {
	            // OTP is valid, update password
	            Optional<User> optionalUser = userRepository.findByEmail(email);
	            if (optionalUser.isPresent()) {
	                User user = optionalUser.get();
	                user.setPassword(passwordEncoder.encode(updatePasswordDto.getNewPassword()));
	                userRepository.save(user);
	                otpMap.remove(email); // Remove OTP from map after use
	                return true;
	            }
	        }
	        return false;
	    }

	private String formatAssessmentInviteEmail(String email,int otp) {
		if(email!=null) {
		try {
				String mailBody = fileUtils.getStringFromFile("PasswordRecoveryEmail.txt");
				if (!mailBody.isEmpty()) {
					return mailBody.replace("{email}", email)
							.replace("{otp}",String.valueOf(otp))
							.replace("{portalUrl}", "https://learning-portal-front-endwith-react-spring.vercel.app/forgetPassword/?email="+email);
				}

				return "";
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	
		return "<b>Voicera Assessment Invite</b><br/> Something went wrong, please contact to HR.";
	}

}
