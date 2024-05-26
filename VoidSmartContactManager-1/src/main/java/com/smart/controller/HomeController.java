package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.UserRepository;
import com.smart.entity.Contact;
import com.smart.entity.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	
//	@Autowired(required = true)
//	
	@Autowired(required = true)
	private PasswordEncoder passwordEncoder;

	@Autowired(required = true)
	private UserRepository userRepository;

	@RequestMapping("/")
	public String Home(Model model) {
		model.addAttribute("pageTitle", "Home-Smart Contact Manager");
		return "index";
	}

	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("pageTitle", "About-Smart Contact Manager");
		return "about";
	}

	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("pageTitle", "SignUp-Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}

	@RequestMapping("/login")
	public String login(Model model) {
		model.addAttribute("pageTitle", "Login-Smart Contact Manager");
		return "login";
	}
	// Handler for registering User

	@RequestMapping(value = "/do_register", method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, @RequestParam("profileImage") MultipartFile file , Principal principal, Model model, BindingResult result1,  HttpSession session) {

		try {
			if (!agreement) {
				System.out.println("You are Not agree the term and conditions");
				throw new Exception("You are Not agree the term and conditions");
			}
			if(result1.hasErrors()) {
				System.out.println("Error"+result1.toString());
				model.addAttribute("user",user);
				return "signup";
			}
			
			if(file.isEmpty()) {
				System.out.println("no file in database");
				user.setImageUrl("default.jpg");
			}
			else {
				user.setImageUrl(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/img").getFile();
				
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				
				Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
				System.out.println("image is uploaded");
				
			}
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			//user.setImageUrl("default.jpg");
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			System.out.println("Agreement" + agreement);
			System.out.println("User" + user);
			model.addAttribute("user", new User());
			User result = this.userRepository.save(user);
			System.out.println(result);
			session.setAttribute("message", new Message("success full register ! ","alert-sucess"));
			return "signup";
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user",user);
		session.setAttribute("message", new Message("something went wrong ! "+e.getMessage(),"alert-danger"));
			return "signup";
		}

		
	}

	@GetMapping("/test")
	@ResponseBody
	public String test() {

		User user = new User();
		user.setName("shubhendu");
		user.setEmail("shubhendubordia2002@gmail.com");

		Contact contact = new Contact();
		user.getContacts().add(contact);

		userRepository.save(user);
		return "wroking";
	}
}
