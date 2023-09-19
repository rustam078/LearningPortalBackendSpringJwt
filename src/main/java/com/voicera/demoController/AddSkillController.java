package com.voicera.demoController;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voicera.user.User;

@RestController
@CrossOrigin
public class AddSkillController {


  @GetMapping("/skill")
  public ResponseEntity<List<Skill>> sayHellotest() {
	   List<Skill> skills = new ArrayList<>();

       // Add skills to the list (in this case, we're adding 5 skills)
       skills.add(new Skill("Skill 1"));
       skills.add(new Skill("Skill 2"));
       skills.add(new Skill("Skill 3"));
       skills.add(new Skill("Skill 4"));
       skills.add(new Skill("Skill 5"));

       // Return the list of skills as a response
       return ResponseEntity.ok(skills);
  }
  
  
  @GetMapping("/user-info")
  public String getUserInfo(Authentication authentication) {
      if (authentication != null) {
    	  User userDetails = (User) authentication.getPrincipal();
          System.out.println(userDetails);
          // You can access the user's ID or other details here
          Integer userId = userDetails.getId(); // Assuming the username holds the user's ID
          
          return "User ID: " + userId;
      }
      return "User not authenticated";
  }

}
