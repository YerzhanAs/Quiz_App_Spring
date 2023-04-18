package com.devrezaur.main.controller;

import java.util.List;

import com.devrezaur.main.model.Users;
import com.devrezaur.main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.devrezaur.main.model.QuestionForm;
import com.devrezaur.main.model.Result;
import com.devrezaur.main.service.QuizService;

@Controller
public class QuizController {

	@Autowired
	private QuizService qService;

	@Autowired
	private UserService userService;



	Boolean submitFlag=false;


	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("currentUser", getUserData());
			return "login";

	}

	@GetMapping("/logout")
	public String logout(Model model) {
			return "login";
	}



	@GetMapping(value = "/register")
	@PreAuthorize("isAnonymous()")
	public String register(Model model){
		return "register";
	}

	@PostMapping(value="/register")
	@PreAuthorize("isAnonymous()")
	public String toRegister(@RequestParam(name="user_email") String email,
							 @RequestParam(name="user_password") String password,
							 @RequestParam(name="re_user_password") String rePassword,
							 @RequestParam(name="user_full_name") String fullName){

		if(password.equals(rePassword)){
			Users newUser=new Users();

			newUser.setFullName(fullName);
			newUser.setPassword(password);
			newUser.setEmail(email);

			if(userService.createUser(newUser)!=null){
				return "login";
			}

		}

		return "register";
	}

	@GetMapping(value="/check")
	public String accessDenied(Model model){
		model.addAttribute("currentUser", getUserData());
		return "validation/check";
	}

	@GetMapping("/quiz")
	public String quiz(Model m, RedirectAttributes ra, Model model) {


		model.addAttribute("currentUser", getUserData());


		if(getUserData().getRoles().get(0).getRole().equals("ROLE_USER")){
			QuestionForm qForm = qService.getQuestions();
			m.addAttribute("qForm", qForm);
			return "quiz";
		}


		return "profile";
	}

	@GetMapping("/profile")
	@PreAuthorize("isAuthenticated()")
	public String profile(Model model) {
		model.addAttribute("currentUser", getUserData());
		return "profile";
	}

	@GetMapping("/submit")
	public String submitShow(Model model) {

			  Users user=getUserData();
			  model.addAttribute("currentUser", getUserData());

			  if(user.getResult()==null){
				  return "validation/chechTest";
			  }

			  return "result";

	}

	
	@PostMapping("/submit")
	@PreAuthorize("hasAnyRole('ROLE_USER')")
	public String submit(@ModelAttribute QuestionForm qForm, Model m) {

		Users user = getUserData();

		if(!submitFlag) {

			Result result = new Result();
			result.setTotalCorrect(qService.getResult(qForm));
			result.setCheckSubmit(true);
			qService.saveScore(result);

			user.setResult(result);
			userService.saveUser(user);
			m.addAttribute("user", user);

			submitFlag=true;
			return "result";

		}

		return "validation/check";


	}
	
	@GetMapping("/score")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
	public String score(Model m) {
		List<Users> allUser=userService.findAll();
        List<String> allName=userService.findNameByUsers(allUser);
		List<Integer> allScore=userService.findScoreByUsers(allUser);

		m.addAttribute("allUser", allUser);
		m.addAttribute("allScore", allScore);
		m.addAttribute("allName", allName);
		return "scoreboard.html";
	}

	private Users getUserData(){
		Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
		if(!(authentication instanceof AnonymousAuthenticationToken)){
			User secUser=(User)authentication.getPrincipal();
			Users myUser=userService.getUserByEmail(secUser.getUsername());
			return myUser;
		}

		return null;
	}

}
