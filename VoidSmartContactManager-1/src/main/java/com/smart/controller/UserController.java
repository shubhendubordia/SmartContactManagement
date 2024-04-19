package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.UserRepository;
import com.smart.dao.ContactRepository;
import com.smart.entity.Contact;
import com.smart.entity.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired(required = true)
	private UserRepository userRepository;

	@Autowired(required = true)
	private ContactRepository contactRepository;

	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();

		User user = userRepository.getUserByUserName(userName);
		model.addAttribute("user", user);
	}

	@RequestMapping("/index")
	public String dashBoard(Model model, Principal prinicipal) {

		String userName = prinicipal.getName();

		User user = userRepository.getUserByUserName(userName);
//	get the user by username

		model.addAttribute("pageTitle", user.getName());

		model.addAttribute("user", user);

		return "user_dashboard";
	}

//	add form controller
	@GetMapping("/addContact")
	public String openAddContactForm(Model model) {

		model.addAttribute("pageTitle", "Add Contacts");
		model.addAttribute("contact", new Contact());
		return "addContactForm";
	}

//	Procession Add Contact From

	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {

		try {

			String name = principal.getName();

			User user = this.userRepository.getUserByUserName(name);
			
			

//			processing and uploading file.....
			if (file.isEmpty()) {
				System.out.println("no file is in database");
				contact.setImage("contact.png");
			} else {
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

//			Paths path = 	Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("Image is uploaded");
			}

			
			
			user.getContacts().add(contact);

			contact.setUser(user);
			

			this.userRepository.save(user);

			session.setAttribute("message",
					new Message("Your contact has been successfully saved  congratulations !!", "alert-sucess"));

			System.out.println("DAta save");
		} catch (Exception e) {
			System.out.println("Error" + e.getMessage());
			session.setAttribute("message", new Message("something went wrong", "alert-danger"));
		}
//		System.out.println(contact);

		return "addContactForm";

	}

	// viewing the contact by 5 per page
	// current page 0;

	@GetMapping("/viewContact/{page}")
	public String showContact(@PathVariable("page") Integer page, Model model, Principal principal) {

		PageRequest pageRequest = PageRequest.of(page, 5);

		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		Page<Contact> contact = this.contactRepository.findContactsByUser(user.getId(), pageRequest);

		model.addAttribute("contacts", contact);

		model.addAttribute("currentPage", page);

		model.addAttribute("totalPages", contact.getTotalPages());

		model.addAttribute("pageTitle", "view Contact");

		return "viewContact";
	}

//	showing specific detailsl
	@RequestMapping("/{cId}/contact")
	public String showContactDetails(@PathVariable("cId") Integer cId, Model model, Principal principal) {
		System.out.println("Cid" + cId);

		Optional<Contact> contactoptional = this.contactRepository.findById(cId);

		Contact contact = contactoptional.get();

		
		String userName = principal.getName();
	User user = 	this.userRepository.getUserByUserName(userName);
		
		if(user.getId() ==  contact.getUser().getId()) {
			model.addAttribute("contact", contact);
		}
		
		return "contactDetails";
	}
	
	
	//delete Contact handler
	
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId, Model model, HttpSession session) {
		
	Contact contactCid = 	this.contactRepository.findById(cId).get();
	//Contact contact = contactCid.get();
	contactCid.setUser(null);
	
	this.contactRepository.delete(contactCid);
	session.setAttribute("message", new Message("Contact delete successfully","success"));
		
		return "redirect:/user/viewContact/0";
	}
	
	
	
	//Your profile Handler
	@GetMapping("/userView")
	public String userView(Model model) {
		
		model.addAttribute("pageTitle", "profile page");
		return "userView";
	}

}




























